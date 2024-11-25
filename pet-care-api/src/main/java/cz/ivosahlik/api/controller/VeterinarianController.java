package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.veterinarian.IVeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static cz.ivosahlik.api.utils.FeedBackMessage.NO_VETS_AVAILABLE;
import static cz.ivosahlik.api.utils.FeedBackMessage.RESOURCE_FOUND;
import static cz.ivosahlik.api.utils.UrlMapping.GET_ALL_SPECIALIZATIONS;
import static cz.ivosahlik.api.utils.UrlMapping.GET_ALL_VETERINARIANS;
import static cz.ivosahlik.api.utils.UrlMapping.SEARCH_VETERINARIAN_FOR_APPOINTMENT;
import static cz.ivosahlik.api.utils.UrlMapping.VETERINARIANS;
import static cz.ivosahlik.api.utils.UrlMapping.VET_AGGREGATE_BY_SPECIALIZATION;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;


@RestController
@RequestMapping(VETERINARIANS)
@RequiredArgsConstructor
public class VeterinarianController {
    private final IVeterinarianService veterinarianService;

    @GetMapping(GET_ALL_VETERINARIANS)
    public ResponseEntity<ApiResponse> getAllVeterinarians(){
        List<UserDto> allVeterinarians = veterinarianService.getAllVeterinariansWithDetails();
        return ok(new ApiResponse(RESOURCE_FOUND,allVeterinarians));
    }

    @GetMapping(SEARCH_VETERINARIAN_FOR_APPOINTMENT)
    public ResponseEntity<ApiResponse> searchVeterinariansForAppointment(
            @RequestParam( value = "date", required = false) LocalDate date,
            @RequestParam(value = "time", required = false) LocalTime time,
            @RequestParam String specialization){
        try {
            List<UserDto> availableVeterinarians = veterinarianService.findAvailableVetsForAppointment(specialization, date, time);
            if(availableVeterinarians.isEmpty()){
                return status(NOT_FOUND).body(new ApiResponse(NO_VETS_AVAILABLE, null));
            }
            return ok(new ApiResponse(RESOURCE_FOUND,availableVeterinarians));
        } catch (ResourceNotFoundException e) {
           return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping(GET_ALL_SPECIALIZATIONS)
    public ResponseEntity<ApiResponse> getAllSpecializations() {
        try {
            List<String> specializations = veterinarianService.getSpecializations();
            return ok(new ApiResponse(RESOURCE_FOUND, specializations));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(VET_AGGREGATE_BY_SPECIALIZATION)
    public ResponseEntity<List<Map<String, Object>>> aggregateVetsBySpecialization(){
        return ok(veterinarianService.aggregateVetsBySpecialization());
    }

}
