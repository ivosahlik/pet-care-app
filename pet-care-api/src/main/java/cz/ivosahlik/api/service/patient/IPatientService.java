package cz.ivosahlik.api.service.patient;

import cz.ivosahlik.api.dto.UserDto;

import java.util.List;

public interface IPatientService {
    List<UserDto> getPatients();
}
