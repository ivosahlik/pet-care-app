package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Pet;
import cz.ivosahlik.api.response.ApiResponse;

import cz.ivosahlik.api.service.pet.IPetService;
import cz.ivosahlik.api.service.pet.PetService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(UrlMapping.PETS)
@RequiredArgsConstructor
public class PetController {
    private final IPetService petService;

    @PutMapping(UrlMapping.SAVE_PETS_FOR_APPOINTMENT)
    public ResponseEntity<ApiResponse> savePets(@RequestParam Long appointmentId, @RequestBody List<Pet> pets) {
        try {
            List<Pet> savedPets = petService.savePetsForAppointment(appointmentId, pets);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PET_ADDED_SUCCESS, savedPets));
        } catch (RuntimeException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_PET_BY_ID)
    public ResponseEntity<ApiResponse> getPetById(@PathVariable Long petId) {
        try {
            Pet pet = petService.getPetById(petId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PET_FOUND, pet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_PET_BY_ID)
    public ResponseEntity<ApiResponse> deletePetById(@PathVariable Long petId) {
        try {
            petService.deletePet(petId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PET_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_PET)
    public ResponseEntity<ApiResponse> updatePet(@PathVariable Long petId, @RequestBody Pet pet) {
        try {
            Pet thePet = petService.updatePet(pet, petId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PET_UPDATE_SUCCESS, thePet));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_PET_TYPES)
    public ResponseEntity<ApiResponse> getAllPetTypes() {
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetTypes()));
    }

    @GetMapping(UrlMapping.GET_PET_COLORS)
    public ResponseEntity<ApiResponse> getAllPetColors() {
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetColors()));
    }

    @GetMapping(UrlMapping.GET_PET_BREEDS)
    public ResponseEntity<ApiResponse> getAllPetBreeds(@RequestParam String petType) {
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, petService.getPetBreeds(petType)));
    }
}


