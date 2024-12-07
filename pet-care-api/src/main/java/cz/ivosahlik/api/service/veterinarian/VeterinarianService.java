package cz.ivosahlik.api.service.veterinarian;

import cz.ivosahlik.api.dto.UserDto;
import cz.ivosahlik.api.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface VeterinarianService {
    List<UserDto> getAllVeterinariansWithDetails();

    List<String> getSpecializations();

    List<UserDto> findAvailableVetsForAppointment(String specialization, LocalDate date, LocalTime time);

    List<Veterinarian> getVeterinariansBySpecialization(String specialization);

    List<Map<String, Object>> aggregateVetsBySpecialization();
}
