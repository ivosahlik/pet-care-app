package cz.ivosahlik.api.service.token;

import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.model.VerificationToken;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static cz.ivosahlik.api.utils.FeedBackMessage.EXPIRED_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.INVALID_VERIFICATION_TOKEN;
import static cz.ivosahlik.api.utils.FeedBackMessage.TOKEN_ALREADY_VERIFIED;
import static cz.ivosahlik.api.utils.FeedBackMessage.VALID_VERIFICATION_TOKEN;
import static cz.ivosahlik.api.utils.SystemUtils.getExpirationTime;
import static java.util.Calendar.getInstance;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> theToken = findByToken(token);
        if (theToken.isEmpty()) {
            return INVALID_TOKEN;
        }
        User user = theToken.get().getUser();
        if (user.isEnabled()) {
            return TOKEN_ALREADY_VERIFIED;
        }
        if (isTokenExpired(token)) {
            return EXPIRED_TOKEN;
        }
        user.setEnabled(true);
        userRepository.save(user);
        return VALID_VERIFICATION_TOKEN;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        var verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

    }

    @Transactional
    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        Optional<VerificationToken> theToken = findByToken(oldToken);
        if (theToken.isPresent()) {
            var verificationToken = theToken.get();
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpirationDate(getExpirationTime());
            return tokenRepository.save(verificationToken);
        } else {
            throw new IllegalArgumentException(INVALID_VERIFICATION_TOKEN + oldToken);
        }
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteVerificationToken(Long tokenId) {
        tokenRepository.deleteById(tokenId);
    }

    @Override
    public boolean isTokenExpired(String token) {
        Optional<VerificationToken> theToken = findByToken(token);
        if (theToken.isEmpty()) {
            return true;
        }
        VerificationToken verificationToken = theToken.get();
        return verificationToken.getExpirationDate().getTime() <= getInstance().getTime().getTime();
    }
}
