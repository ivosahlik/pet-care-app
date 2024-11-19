package cz.ivosahlik.api.service.password;

import cz.ivosahlik.api.model.User;

import java.util.Optional;

public interface IPasswordResetService {

    Optional<User> findUserByPasswordResetToken(String token);
    void requestPasswordReset(String email);
    String resetPassword(String password, User user);
}
