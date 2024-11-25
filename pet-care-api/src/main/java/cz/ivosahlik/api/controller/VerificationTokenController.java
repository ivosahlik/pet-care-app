package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.model.VerificationToken;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.request.VerificationTokenRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.token.IVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cz.ivosahlik.api.utils.FeedBackMessage.EXPIRED_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_ALREADY_VERIFIED;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_DELETE_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_SAVED_SUCCESS;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_VALIDATION_ERROR;
import static cz.ivosahlik.api.utils.FeedBackMessage.USER_FOUND;
import static cz.ivosahlik.api.utils.FeedBackMessage.VALID_VERIFICATION_TOKEN;
import static cz.ivosahlik.api.utils.UrlMapping.CHECK_TOKEN_EXPIRATION;
import static cz.ivosahlik.api.utils.UrlMapping.DELETE_TOKEN;
import static cz.ivosahlik.api.utils.UrlMapping.GENERATE_NEW_TOKEN_FOR_USER;
import static cz.ivosahlik.api.utils.UrlMapping.SAVE_TOKEN;
import static cz.ivosahlik.api.utils.UrlMapping.TOKEN_VERIFICATION;
import static cz.ivosahlik.api.utils.UrlMapping.VALIDATE_TOKEN;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(TOKEN_VERIFICATION)
public class VerificationTokenController {

    private final IVerificationTokenService verificationTokenService;
    private final UserRepository userRepository;

    @GetMapping(VALIDATE_TOKEN)
    public ResponseEntity<ApiResponse> validateToken(String token) {
        String result = verificationTokenService.validateToken(token);
        ApiResponse response = switch (result) {
            case "INVALID" -> new ApiResponse(INVALID_TOKEN, null);
            case "VERIFIED" -> new ApiResponse(TOKEN_ALREADY_VERIFIED, null);
            case "EXPIRED" -> new ApiResponse(EXPIRED_TOKEN, null);
            case "VALID" -> new ApiResponse(VALID_VERIFICATION_TOKEN, null);
            default -> new ApiResponse(TOKEN_VALIDATION_ERROR, null);
        };
        return ok(response);
    }

    @GetMapping(CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<ApiResponse> checkTokenExpiration(String token) {
        boolean isExpired = verificationTokenService.isTokenExpired(token);
        return ok(new ApiResponse(isExpired ? EXPIRED_TOKEN : VALID_VERIFICATION_TOKEN, null));
    }

    @PostMapping(SAVE_TOKEN)
    public ResponseEntity<ApiResponse> saveVerificationTokenForUser(@RequestBody VerificationTokenRequest request) {
        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new RuntimeException(USER_FOUND));
        verificationTokenService.saveVerificationTokenForUser(request.getToken(), user);
        return ok(new ApiResponse(TOKEN_SAVED_SUCCESS, null));
    }

    @PutMapping(GENERATE_NEW_TOKEN_FOR_USER)
    public ResponseEntity<ApiResponse> generateNewVerificationToken(@RequestParam String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ok(new ApiResponse("", newToken));
    }

    @DeleteMapping(DELETE_TOKEN)
    public ResponseEntity<ApiResponse> deleteUserToken(@RequestParam Long userId) {
        verificationTokenService.deleteVerificationToken(userId);
        return ok(new ApiResponse(TOKEN_DELETE_SUCCESS, null));
    }

}
