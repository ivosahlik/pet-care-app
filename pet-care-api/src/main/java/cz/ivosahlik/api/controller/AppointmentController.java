package cz.ivosahlik.api.controller;

import cz.ivosahlik.api.event.AppointmentApprovedEvent;
import cz.ivosahlik.api.event.AppointmentBookedEvent;
import cz.ivosahlik.api.event.AppointmentDeclinedEvent;
import cz.ivosahlik.api.exception.AlreadyExistsException;
import cz.ivosahlik.api.exception.ResourceNotFoundException;
import cz.ivosahlik.api.model.Appointment;
import cz.ivosahlik.api.request.AppointmentUpdateRequest;
import cz.ivosahlik.api.request.BookAppointmentRequest;
import cz.ivosahlik.api.response.ApiResponse;
import cz.ivosahlik.api.service.appointment.AppointmentService;
import cz.ivosahlik.api.utils.FeedBackMessage;
import cz.ivosahlik.api.utils.UrlMapping;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.APPOINTMENTS)
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final ApplicationEventPublisher publisher;

    @GetMapping(UrlMapping.ALL_APPOINTMENT)
    public ResponseEntity<ApiResponse> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.status(FOUND).body(new ApiResponse(FeedBackMessage.APPOINTMENT_FOUND, appointments));

        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.BOOK_APPOINTMENT)
    public ResponseEntity<ApiResponse> bookAppointment(
            @RequestBody BookAppointmentRequest request,
            @RequestParam Long senderId,
            @RequestParam Long recipientId) {
        try {
            Appointment theAppointment = appointmentService.createAppointment(request, senderId, recipientId);
            publisher.publishEvent(new AppointmentBookedEvent(theAppointment));
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_BOOKED_SUCCESS, theAppointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_ID)
    public ResponseEntity<ApiResponse> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_BY_NO)
    public ResponseEntity<ApiResponse> getAppointmentByNo(@PathVariable String appointmentNo) {
        try {
            Appointment appointment = appointmentService.getAppointmentByNo(appointmentNo);
            return ResponseEntity.status(FOUND).body(new ApiResponse(FeedBackMessage.APPOINTMENT_FOUND, appointment));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_APPOINTMENT)
    public ResponseEntity<ApiResponse> deleteAppointmentById(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_APPOINTMENT)
    public ResponseEntity<ApiResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateRequest request) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_UPDATE_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.CANCEL_APPOINTMENT)
    public ResponseEntity<ApiResponse> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_CANCELLED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.APPROVE_APPOINTMENT)
    public ResponseEntity<ApiResponse> approveAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.approveAppointment(id);
            publisher.publishEvent(new AppointmentApprovedEvent(appointment));
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_APPROVED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.DECLINE_APPOINTMENT)
    public ResponseEntity<ApiResponse> declineAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.declineAppointment(id);
            publisher.publishEvent(new AppointmentDeclinedEvent(appointment));
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.APPOINTMENT_DECLINED_SUCCESS, appointment));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.COUNT_APPOINTMENT)
    public long countAppointments() {
        return appointmentService.countAppointment();
    }

    @GetMapping(UrlMapping.GET_APPOINTMENT_SUMMARY)
    public ResponseEntity<ApiResponse> getAppointmentSummary() {
        try {
            List<Map<String, Object>> summary = appointmentService.getAppointmentSummary();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.SUCCESS, summary));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR + e.getMessage(), null));
        }
    }

}
