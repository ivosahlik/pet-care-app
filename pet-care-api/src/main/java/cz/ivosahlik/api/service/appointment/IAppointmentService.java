package cz.ivosahlik.api.service.appointment;

import cz.ivosahlik.api.dto.AppointmentDto;
import cz.ivosahlik.api.model.Appointment;
import cz.ivosahlik.api.request.AppointmentUpdateRequest;
import cz.ivosahlik.api.request.BookAppointmentRequest;

import java.util.List;
import java.util.Map;

public interface IAppointmentService {
    Appointment createAppointment(BookAppointmentRequest appointment, Long sender, Long recipient);
    List<Appointment> getAllAppointments();
    Appointment updateAppointment(Long id, AppointmentUpdateRequest request);


    void deleteAppointment(Long id);
    Appointment getAppointmentById(Long id);
    Appointment getAppointmentByNo(String appointmentNo);
    List<AppointmentDto> getUserAppointments(Long userId);
    Appointment cancelAppointment(Long appointmentId);

    Appointment approveAppointment(Long appointmentId);

    Appointment declineAppointment(Long appointmentId);

    long countAppointment();

    List<Map<String, Object>> getAppointmentSummary();

    List<Long> getAppointmentIds();

    void setAppointmentStatus(Long appointmentId);
}
