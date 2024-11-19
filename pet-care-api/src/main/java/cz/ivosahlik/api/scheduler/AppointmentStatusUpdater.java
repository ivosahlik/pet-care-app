package cz.ivosahlik.api.scheduler;

import cz.ivosahlik.api.service.appointment.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentStatusUpdater {
    private final IAppointmentService appointmentService;

    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void automateAppointmentStatusUpdate() {
        List<Long> appointmentIds = appointmentService.getAppointmentIds();
        appointmentIds.forEach(appointmentService::setAppointmentStatus);
    }
}
