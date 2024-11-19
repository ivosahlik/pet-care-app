package cz.ivosahlik.api.data;

import cz.ivosahlik.api.model.Admin;
import cz.ivosahlik.api.model.Patient;
import cz.ivosahlik.api.model.Role;
import cz.ivosahlik.api.model.Veterinarian;
import cz.ivosahlik.api.repository.*;
import cz.ivosahlik.api.service.role.IRoleService;
import cz.ivosahlik.api.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class DefaultDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final RoleService roleService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<String> defaultRoles = Set.of("ROLE_ADMIN", "ROLE_PATIENT", "ROLE_VET");
        createDefaultRoleIfNotExits(defaultRoles);

        createDefaultAdminIfNotExists();
        createDefaultVetIfNotExits();
        createDefaultPatientIfNotExits();
    }

    private void createDefaultVetIfNotExits() {
        Role vetRole = roleService.getRoleByName("ROLE_VET");
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String defaultEmail = "vet" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                return;
            }
            Veterinarian vet = new Veterinarian();
            vet.setFirstName("Vet");
            vet.setLastName("Number" + i);
            vet.setGender("Not Specified");
            vet.setPhoneNumber("1234567890");
            vet.setEmail(defaultEmail);
            vet.setPassword(passwordEncoder.encode("password" + i));
            vet.setUserType("VET");
            vet.setRoles(new HashSet<>(Collections.singletonList(vetRole)));
            vet.setSpecialization("Dermatologist");
            Veterinarian theVet = veterinarianRepository.save(vet);
            theVet.setEnabled(true);
            log.info("Default vet user {} created successfully.", i);
        });
    }

    private void createDefaultPatientIfNotExits() {
        Role patientRole = roleService.getRoleByName("ROLE_PATIENT");
        for (int i = 1; i <= 10; i++) {
            String defaultEmail = "pat" + i + "@gmail.com";
            if (userRepository.existsByEmail(defaultEmail)) {
                continue;
            }
            Patient pat = new Patient();
            pat.setFirstName("Pat");
            pat.setLastName("Patient" + i);
            pat.setGender("Not Specified");
            pat.setPhoneNumber("1234567890");
            pat.setEmail(defaultEmail);
            pat.setPassword(passwordEncoder.encode("password" + i));
            pat.setUserType("PATIENT");
            pat.setRoles(new HashSet<>(Collections.singletonList(patientRole)));
            Patient thePatient = patientRepository.save(pat);
            thePatient.setEnabled(true);
            log.info("Default vet user {} created successfully.", i);
        }
    }

    private void createDefaultAdminIfNotExists() {
        Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
        final String defaultAdminEmail = "admin@email.com";
        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            return;
        }

        Admin admin = new Admin();
        admin.setFirstName("UPC");
        admin.setLastName("Admin 2");
        admin.setGender("Female");
        admin.setPhoneNumber("22222222");
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(passwordEncoder.encode("00220033"));
        admin.setUserType("ADMIN");
        admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
        Admin theAdmin = adminRepository.save(admin);
        theAdmin.setEnabled(true);
        log.info("Default admin user created successfully.");
    }

    private void createDefaultRoleIfNotExits(Set<String> roles) {
        roles.stream()
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(Role::new).forEach(roleRepository::save);

    }
}
