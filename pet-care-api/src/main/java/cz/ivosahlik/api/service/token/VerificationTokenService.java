package cz.ivosahlik.api.service.token;

import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    String validateToken(String token);
    void saveVerificationTokenForUser(String token, User user );
    VerificationToken generateNewVerificationToken(String oldToken);
    Optional<VerificationToken> findByToken(String token);
    void deleteVerificationToken(Long tokenId);
    boolean isTokenExpired(String token);
}
