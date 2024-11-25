package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Pet;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.pet.IPetService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cz.ivosahlik.api.utils.FeedBackMessage.*;
import static cz.ivosahlik.api.utils.UrlMapping.DELETE_PET_BY_ID;
import static cz.ivosahlik.api.utils.UrlMapping.GET_PET_BREEDS;
import static cz.ivosahlik.api.utils.UrlMapping.GET_PET_BY_ID;
import static cz.ivosahlik.api.utils.UrlMapping.GET_PET_COLORS;
import static cz.ivosahlik.api.utils.UrlMapping.GET_PET_TYPES;
import static cz.ivosahlik.api.utils.UrlMapping.PETS;
import static cz.ivosahlik.api.utils.UrlMapping.SAVE_PETS_FOR_APPOINTMENT;
import static cz.ivosahlik.api.utils.UrlMapping.UPDATE_PET;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(PETS)
@RequiredArgsConstructor
public class PetController {
    private final IPetService petService;

    @PutMapping(SAVE_PETS_FOR_APPOINTMENT)
    public ResponseEntity<ApiResponse> savePets(@RequestParam Long appointmentId, @RequestBody List<Pet> pets) {
        try {
            List<Pet> savedPets = petService.savePetsForAppointment(appointmentId, pets);
            return ok(new ApiResponse(PET_ADDED_SUCCESS, savedPets));
        } catch (RuntimeException e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_PET_BY_ID)
    public ResponseEntity<ApiResponse> getPetById(@PathVariable Long petId) {
        try {
            Pet pet = petService.getPetById(petId);
            return ok(new ApiResponse(PET_FOUND, pet));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(DELETE_PET_BY_ID)
    public ResponseEntity<ApiResponse> deletePetById(@PathVariable Long petId) {
        try {
            petService.deletePet(petId);
            return ok(new ApiResponse(PET_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UPDATE_PET)
    public ResponseEntity<ApiResponse> updatePet(@PathVariable Long petId, @RequestBody Pet pet) {
        try {
            Pet thePet = petService.updatePet(pet, petId);
            return ok(new ApiResponse(PET_UPDATE_SUCCESS, thePet));
        } catch (ResourceNotFoundException e) {
            return status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_PET_TYPES)
    public ResponseEntity<ApiResponse> getAllPetTypes() {
        return ok(new ApiResponse(RESOURCE_FOUND, petService.getPetTypes()));
    }

    @GetMapping(GET_PET_COLORS)
    public ResponseEntity<ApiResponse> getAllPetColors() {
        return ok(new ApiResponse(RESOURCE_FOUND, petService.getPetColors()));
    }

    @GetMapping(GET_PET_BREEDS)
    public ResponseEntity<ApiResponse> getAllPetBreeds(@RequestParam String petType) {
        return ok(new ApiResponse(RESOURCE_FOUND, petService.getPetBreeds(petType)));
    }
}


