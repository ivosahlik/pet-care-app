package cz.ivosahlik.api.factory;

import cz.ivosahlik.api.model.Patient;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.PatientRepository;
import cz.ivosahlik.api.request.RegistrationRequest;
import cz.ivosahlik.api.service.role.IRoleService;
import cz.ivosahlik.api.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientFactory {
    private final PatientRepository patientRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    public Patient createPatient(RegistrationRequest request) {
        Patient patient = new Patient();
        patient.setRoles(roleService.setUserRole("PATIENT"));
        userAttributesMapper.setCommonAttributes(request, patient);
        return patientRepository.save(patient);
    }
}
