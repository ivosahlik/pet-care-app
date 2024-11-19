package cz.ivosahlik.api.factory;

import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.request.RegistrationRequest;

public interface UserFactory {
    User createUser(RegistrationRequest registrationRequest);
}
