package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.patient.IPatientService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.PATIENTS)
public class PatientController {
    private final IPatientService patientService;

    @GetMapping(UrlMapping.GET_ALL_PATIENTS)
    public ResponseEntity<ApiResponse> getAllPatients() {
        List<UserDto> patients = patientService.getPatients();
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, patients));
    }
}
