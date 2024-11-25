package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.event.RegistrationCompleteEvent;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.model.VerificationToken;
import cz.ivosahlik.api.request.LoginRequest;
import cz.ivosahlik.api.request.PasswordResetRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.response.JwtResponse;
import cz.ivosahlik.api.security.jwt.JwtUtils;
import cz.ivosahlik.api.security.user.UPCUserDetails;
import cz.ivosahlik.api.service.password.PasswordResetService;
import cz.ivosahlik.api.service.token.VerificationTokenService;
import cz.ivosahlik.api.utils.UrlMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static cz.ivosahlik.api.utils.FeedBackMessage.ACCOUNT_DISABLED;
import static cz.ivosahlik.api.utils.FeedBackMessage.AUTHENTICATION_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.ERROR;
import static cz.ivosahlik.api.utils.FeedBackMessage.EXPIRED_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_EMAIL;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_PASSWORD;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_RESET_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_VERIFICATION_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.MISSING_PASSWORD;
import static cz.ivosahlik.api.utils.FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT;
import static cz.ivosahlik.api.utils.FeedBackMessage.PASSWORD_RESET_EMAIL_SENT;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_ALREADY_VERIFIED;
import static cz.ivosahlik.api.utils.FeedBackMessage.VALID_VERIFICATION_TOKEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@RestController
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenService tokenService;
    private final PasswordResetService passwordResetService;
    private final ApplicationEventPublisher publisher;

    @PostMapping(UrlMapping.LOGIN)
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UPCUserDetails userDetails = (UPCUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            return ok(new ApiResponse(AUTHENTICATION_SUCCESS, jwtResponse));

        } catch (DisabledException e) {
            return status(UNAUTHORIZED).body(new ApiResponse(ACCOUNT_DISABLED, null));
        } catch (AuthenticationException e) {
            return status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), INVALID_PASSWORD));

        }
    }

    @GetMapping(UrlMapping.VERIFY_EMAIL)
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        String result = tokenService.validateToken(token);
        return switch (result) {
            case "VALID" -> ok(new ApiResponse(VALID_VERIFICATION_TOKEN, null));
            case "VERIFIED" -> ok(new ApiResponse(TOKEN_ALREADY_VERIFIED, null));
            case "EXPIRED" -> status(HttpStatus.GONE).body(new ApiResponse(EXPIRED_TOKEN, null));
            case "INVALID" -> status(HttpStatus.GONE).body(new ApiResponse(INVALID_VERIFICATION_TOKEN, null));
            default -> internalServerError().body(new ApiResponse(ERROR, null));

        };
    }

    @PutMapping(UrlMapping.RESEND_VERIFICATION_TOKEN)
    public ResponseEntity<ApiResponse> resendVerificationToken(@RequestParam("token") String oldToken) {
        try {
            VerificationToken verificationToken = tokenService.generateNewVerificationToken(oldToken);
            User theUser = verificationToken.getUser();
            publisher.publishEvent(new RegistrationCompleteEvent(theUser));
            return ok(new ApiResponse(NEW_VERIFICATION_TOKEN_SENT, null));
        } catch (Exception e) {
            return badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.REQUEST_PASSWORD_RESET)
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email == null || email.trim().isEmpty()) {
            return badRequest()
                    .body(new ApiResponse(INVALID_EMAIL, null));
        }
        try {
            passwordResetService.requestPasswordReset(email);
            return ok(new ApiResponse(PASSWORD_RESET_EMAIL_SENT, null));
        } catch (ResourceNotFoundException ex) {
            return badRequest().body(new ApiResponse(ex.getMessage(), null));
        } catch (Exception e) {
            return internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.RESET_PASSWORD)
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return badRequest().body(new ApiResponse(MISSING_PASSWORD, null));
        }
        Optional<User> theUser = passwordResetService.findUserByPasswordResetToken(token);
        if (theUser.isEmpty()) {
            return badRequest().body(new ApiResponse(INVALID_RESET_TOKEN, null));
        }
        User user = theUser.get();
        String message = passwordResetService.resetPassword(newPassword, user);
        return ok(new ApiResponse(message, null));
    }

}
