package cz.ivosahlik.api.service.password;

import cz.ivosahlik.api.request.ChangePasswordRequest;

public interface IChangePasswordService {
    void changePassword(Long userId, ChangePasswordRequest request);
}
