package cz.ivosahlik.api.factory;

import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.UserRepository;
import cz.ivosahlik.api.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleUserFactory implements UserFactory {
    private final UserRepository userRepository;
    private final VeterinarianFactory veterinarianFactory;
    private final PatientFactory patientFactory;
    private final AdminFactory adminFactory;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new AlreadyExistsException("Oops! " + registrationRequest.getEmail() + " already exists!");
        }
        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        switch (registrationRequest.getUserType()) {
            case "VET" -> {
                return veterinarianFactory.createVeterinarian(registrationRequest);
            }
            case "PATIENT" -> {
                return patientFactory.createPatient(registrationRequest);
            }
            case "ADMIN" -> {
                return adminFactory.createAdmin(registrationRequest);
            }
            default -> {
                return null;
            }
        }
    }
}
