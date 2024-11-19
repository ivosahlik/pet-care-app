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
import cz.ivosahlik.api.utils.FeedBackMessage;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
            Authentication authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UPCUserDetails userDetails = (UPCUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.AUTHENTICATION_SUCCESS, jwtResponse));

        } catch (DisabledException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(FeedBackMessage.ACCOUNT_DISABLED, null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), FeedBackMessage.INVALID_PASSWORD));

        }
    }

    @GetMapping(UrlMapping.VERIFY_EMAIL)
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        String result = tokenService.validateToken(token);
        return switch (result) {
            case "VALID" -> ResponseEntity.ok(new ApiResponse(FeedBackMessage.VALID_VERIFICATION_TOKEN, null));
            case "VERIFIED" -> ResponseEntity.ok(new ApiResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null));
            case "EXPIRED" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse(FeedBackMessage.EXPIRED_TOKEN, null));
            case "INVALID" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse(FeedBackMessage.INVALID_VERIFICATION_TOKEN, null));
            default -> ResponseEntity.internalServerError().body(new ApiResponse(FeedBackMessage.ERROR, null));

        };
    }

    @PutMapping(UrlMapping.RESEND_VERIFICATION_TOKEN)
    public ResponseEntity<ApiResponse> resendVerificationToken(@RequestParam("token") String oldToken) {
        try {
            VerificationToken verificationToken = tokenService.generateNewVerificationToken(oldToken);
            User theUser = verificationToken.getUser();
            publisher.publishEvent(new RegistrationCompleteEvent(theUser));
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.REQUEST_PASSWORD_RESET)
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(FeedBackMessage.INVALID_EMAIL, null));
        }
        try {
            passwordResetService.requestPasswordReset(email);
            return ResponseEntity.
                    ok(new ApiResponse(FeedBackMessage.PASSWORD_RESET_EMAIL_SENT, null));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.RESET_PASSWORD)
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(FeedBackMessage.MISSING_PASSWORD, null));
        }
        Optional<User> theUser = passwordResetService.findUserByPasswordResetToken(token);
        if (theUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(FeedBackMessage.INVALID_RESET_TOKEN, null));
        }
        User user = theUser.get();
        String message = passwordResetService.resetPassword(newPassword, user);
        return ResponseEntity.ok(new ApiResponse(message, null));
    }

}
