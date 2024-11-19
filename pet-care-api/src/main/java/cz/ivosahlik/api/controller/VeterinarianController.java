package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.appointment.AppointmentService;
import cz.ivosahlik.api.service.appointment.IAppointmentService;
import cz.ivosahlik.api.service.veterinarian.IVeterinarianService;
import cz.ivosahlik.api.service.veterinarian.VeterinarianService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
@RequestMapping(UrlMapping.VETERINARIANS)
@RequiredArgsConstructor
public class VeterinarianController {
    private final IVeterinarianService veterinarianService;

    @GetMapping(UrlMapping.GET_ALL_VETERINARIANS)
    public ResponseEntity<ApiResponse> getAllVeterinarians(){
        List<UserDto> allVeterinarians = veterinarianService.getAllVeterinariansWithDetails();
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND,allVeterinarians));

    }

    @GetMapping(UrlMapping.SEARCH_VETERINARIAN_FOR_APPOINTMENT)
    public ResponseEntity<ApiResponse> searchVeterinariansForAppointment(
            @RequestParam( value = "date", required = false) LocalDate date,
            @RequestParam(value = "time", required = false) LocalTime time,
            @RequestParam String specialization){
        try {
            List<UserDto> availableVeterinarians = veterinarianService.findAvailableVetsForAppointment(specialization, date, time);
            if(availableVeterinarians.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(FeedBackMessage.NO_VETS_AVAILABLE, null));
            }
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND,availableVeterinarians));
        } catch (ResourceNotFoundException e) {
           return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping(UrlMapping.GET_ALL_SPECIALIZATIONS)
    public ResponseEntity<ApiResponse> getAllSpecializations() {
        try {
            List<String> specializations = veterinarianService.getSpecializations();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, specializations));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.VET_AGGREGATE_BY_SPECIALIZATION)
    public ResponseEntity<List<Map<String, Object>>> aggregateVetsBySpecialization(){
        List<Map<String, Object>> aggregatedVets = veterinarianService.aggregateVetsBySpecialization();
        return ResponseEntity.ok(aggregatedVets);
    }

}
