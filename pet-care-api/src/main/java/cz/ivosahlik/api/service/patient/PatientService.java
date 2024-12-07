package cz.ivosahlik.api.service.patient;

import cz.ivosahlik.api.dto.UserDto;

import java.util.List;

public interface PatientService {
    List<UserDto> getPatients();
}
