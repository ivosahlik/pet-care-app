package cz.ivosahlik.api.service.pet;

import cz.ivosahlik.api.model.Pet;

import java.util.List;

public interface PetService {
    List<Pet> savePetsForAppointment(List<Pet> pets);
    List<Pet> savePetsForAppointment(Long appointmentId, List<Pet> pets);
    Pet updatePet(Pet pet, Long id);
    void deletePet(Long id);
    Pet getPetById(Long id);
    List<String> getPetTypes();
    List<String> getPetColors();
    List<String> getPetBreeds(String petType);
}
