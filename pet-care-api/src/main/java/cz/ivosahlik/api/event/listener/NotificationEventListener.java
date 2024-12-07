package cz.ivosahlik.api.event.listener;

import cz.ivosahlik.api.email.EmailService;
import cz.ivosahlik.api.event.AppointmentApprovedEvent;
import cz.ivosahlik.api.event.AppointmentBookedEvent;
import cz.ivosahlik.api.event.AppointmentDeclinedEvent;
import cz.ivosahlik.api.event.PasswordResetEvent;
import cz.ivosahlik.api.event.RegistrationCompleteEvent;
import cz.ivosahlik.api.model.Appointment;
import cz.ivosahlik.api.model.User;
import cz.ivosahlik.api.service.token.VerificationTokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final EmailService emailService;
    private final VerificationTokenService tokenService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    @EventListener
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        handleSendRegistrationVerificationEmail(event);
    }

    @EventListener
    public void onApplicationEvent(AppointmentBookedEvent event) {
        try {
            handleAppointmentBookedNotification(event);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onApplicationEvent(AppointmentApprovedEvent event) {
        try {
            handleAppointmentApprovedNotification(event);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onApplicationEvent(AppointmentDeclinedEvent event) {
        try {
            handleAppointmentDeclinedNotification(event);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onApplicationEvent(PasswordResetEvent event) {
        handlePasswordResetRequest(event);
    }

    private void handleSendRegistrationVerificationEmail(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String vToken = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(vToken, user);
        String verificationUrl = frontendBaseUrl + "/email-verification?token=" + vToken;
        try {
            sendRegistrationVerificationEmail(user, verificationUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRegistrationVerificationEmail(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify Your Email";
        String senderName = "Universal Pet Care";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us," +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email</a>" +
                "<p> Thank you <br> Universal Pet Care Email Verification Service";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

    private void handleAppointmentBookedNotification(AppointmentBookedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User vet = appointment.getVeterinarian();
        sendAppointmentBookedNotification(vet, frontendBaseUrl);
    }

    private void sendAppointmentBookedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Appointment Notification";
        String senderName = "Universal Pet Care";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>You have a new appointment schedule:</p>" +
                "<a href=\"" + url + "\">Please, check the clinic portal to view appointment details.</a> <br/>" +
                "<p> Best Regards.<br> Universal Pet Care Service";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

    private void handleAppointmentApprovedNotification(AppointmentApprovedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User patient = appointment.getPatient();
        sendAppointmentApprovedNotification(patient, frontendBaseUrl);
    }

    private void sendAppointmentApprovedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Appointment Approved";
        String senderName = "Universal Pet Care Notification Service";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Your appointment has been approved:</p>" +
                "<a href=\"" + url + "\">Please, check the clinic portal to view appointment details " +
                "and veterinarian information.</a> <br/>" +
                "<p> Best Regards.<br> Universal Pet Care";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

    private void handleAppointmentDeclinedNotification(AppointmentDeclinedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User patient = appointment.getPatient();
        sendAppointmentDeclinedNotification(patient, frontendBaseUrl);
    }

    private void sendAppointmentDeclinedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Appointment Not Approved";
        String senderName = "Universal Pet Care Notification Service";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>We are sorry, your appointment was not approved at this time,<br/> " +
                "Please, kindly make a reschedule for another date. Thanks</p>" +
                "<a href=\"" + url + "\">Please, check the clinic portal to view appointment details.</a> <br/>" +
                "<p> Best Regards.<br> Universal Pet Care";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

    private void handlePasswordResetRequest(PasswordResetEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(token, user);
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;
        try {
            sendPasswordResetEmail(user, resetUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private void sendPasswordResetEmail(User user, String resetUrl) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request";
        String senderName = "Universal Pet Care";
        String mailContent = "<p>Hi, " + user.getFirstName() + ",</p>" +
                "<p>You have requested to reset your password. Please click the link below to proceed:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a><br/>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best Regards.<br> Universal Pet Care</p>";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

}
