package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.patient.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cz.ivosahlik.api.utils.FeedBackMessage.RESOURCE_FOUND;
import static cz.ivosahlik.api.utils.UrlMapping.GET_ALL_PATIENTS;
import static cz.ivosahlik.api.utils.UrlMapping.PATIENTS;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(PATIENTS)
public class PatientController {
    private final IPatientService patientService;

    @GetMapping(GET_ALL_PATIENTS)
    public ResponseEntity<ApiResponse> getAllPatients() {
        List<UserDto> patients = patientService.getPatients();
        return ok(new ApiResponse(RESOURCE_FOUND, patients));
    }
}
