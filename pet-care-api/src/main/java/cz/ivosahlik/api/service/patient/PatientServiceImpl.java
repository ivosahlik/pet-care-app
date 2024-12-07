package cz.ivosahlik.api.service.patient;

import cz.ivosahlik.api.dto.EntityConverter;
import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.model.Patient;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final EntityConverter<User, UserDto> entityConverter;

    @Override
    public List<UserDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(po -> entityConverter.mapEntityToDto(po, UserDto.class))
                .toList();
    }
}
