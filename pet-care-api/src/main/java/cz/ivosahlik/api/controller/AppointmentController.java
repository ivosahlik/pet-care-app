package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.event.AppointmentApprovedEvent;
import cz.ivosahlik.api.event.AppointmentBookedEvent;
import cz.ivosahlik.api.event.AppointmentDeclinedEvent;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Appointment;
import cz.ivosahlik.api.request.AppointmentUpdateRequest;
import cz.ivosahlik.api.request.BookAppointmentRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.appointment.AppointmentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cz.ivosahlik.api.utils.FeedBackMessage.*;
import static cz.ivosahlik.api.utils.UrlMapping.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(APPOINTMENTS)
public class AppointmentController {
    private final AppointmentServiceImpl appointmentServiceImpl;
    private final ApplicationEventPublisher publisher;

    @GetMapping(ALL_APPOINTMENT)
    public ResponseEntity<ApiResponse> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentServiceImpl.getAllAppointments();
            return ResponseEntity.status(FOUND).body(new ApiResponse(APPOINTMENT_FOUND, appointments));

        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @PostMapping(BOOK_APPOINTMENT)
    public ResponseEntity<ApiResponse> bookAppointment(
            @RequestBody BookAppointmentRequest request,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment theAppointment = appointmentServiceImpl.createAppointment(request, senderId, recipientId);
            publisher.publishEvent(new AppointmentBookedEvent(theAppointment));
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_BOOKED_SUCCESS, theAppointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_APPOINTMENT_BY_ID)
    public ResponseEntity<ApiResponse> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentServiceImpl.getAppointmentById(id);
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(GET_APPOINTMENT_BY_NO)
    public ResponseEntity<ApiResponse> getAppointmentByNo(@PathVariable String appointmentNo) {
        try {
            Appointment appointment = appointmentServiceImpl.getAppointmentByNo(appointmentNo);
            return ResponseEntity.status(FOUND).body(new ApiResponse(APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(DELETE_APPOINTMENT)
    public ResponseEntity<ApiResponse> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentServiceImpl.deleteAppointment(id);
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UPDATE_APPOINTMENT)
    public ResponseEntity<ApiResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request) {
        try {
            Appointment appointment = appointmentServiceImpl.updateAppointment(id, request);
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_UPDATE_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(CANCEL_APPOINTMENT)
    public ResponseEntity<ApiResponse> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentServiceImpl.cancelAppointment(id);
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_CANCELLED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(APPROVE_APPOINTMENT)
    public ResponseEntity<ApiResponse> approveAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentServiceImpl.approveAppointment(id);
            publisher.publishEvent(new AppointmentApprovedEvent(appointment));
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_APPROVED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(DECLINE_APPOINTMENT)
    public ResponseEntity<ApiResponse> declineAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentServiceImpl.declineAppointment(id);
            publisher.publishEvent(new AppointmentDeclinedEvent(appointment));
            return ResponseEntity.ok(new ApiResponse(APPOINTMENT_DECLINED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(COUNT_APPOINTMENT)
    public long countAppointments() {
        return appointmentServiceImpl.countAppointment();
    }

    @GetMapping(GET_APPOINTMENT_SUMMARY)
    public ResponseEntity<ApiResponse> getAppointmentSummary() {
        try {
            List<Map<String, Object>> summary = appointmentServiceImpl.getAppointmentSummary();
            return ResponseEntity.ok(new ApiResponse(SUCCESS, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(ERROR + e.getMessage(), null));
        }
    }

}
