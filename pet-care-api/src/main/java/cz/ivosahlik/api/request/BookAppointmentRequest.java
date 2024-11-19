package cz.ivosahlik.api.request;

import cz.ivosahlik.api.model.Appointment;
import cz.ivosahlik.api.model.Pet;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookAppointmentRequest {
    private Appointment appointment;
    private List<Pet> pets;
}
