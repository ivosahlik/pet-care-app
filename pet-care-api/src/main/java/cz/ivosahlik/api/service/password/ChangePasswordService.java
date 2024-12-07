package cz.ivosahlik.api.service.password;

import cz.ivosahlik.api.request.ChangePasswordRequest;

public interface ChangePasswordService {
    void changePassword(Long userId, ChangePasswordRequest request);
}
