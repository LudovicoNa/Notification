package email.userservice;

import email.userservice.exception.EmailNotFoundException;
import email.userservice.exception.EmailServiceException;
import email.userservice.model.EmailNotification;
import email.userservice.repository.EmailNotificationRepository;
import email.userservice.service.EmailNotificationService;
import email.userservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailNotificationServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EmailNotificationRepository emailNotificationRepository;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmailNotification_Success() {
        EmailNotification notification = new EmailNotification();
        notification.setRecipient("test@example.com");
        notification.setSubject("Test Subject");
        notification.setBody("Test Body");
        notification.setSentAt(LocalDateTime.now());
        notification.setSuccess(true);

        when(emailNotificationRepository.save(any(EmailNotification.class))).thenReturn(notification);

        EmailNotification result = emailNotificationService.sendEmailNotification("test@example.com", "Test Subject", "Test Body");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("test@example.com", result.getRecipient());
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
        verify(emailNotificationRepository).save(any(EmailNotification.class));
    }

    @Test
    public void testSendEmailNotification_EmailNotFoundException() {
        doThrow(new MailSendException("Invalid email")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        EmailNotFoundException exception = assertThrows(
                EmailNotFoundException.class,
                () -> emailNotificationService.sendEmailNotification("invalid@example.com", "Test Subject", "Test Body")
        );
        assertEquals("Indirizzo email inesistente o non valido: Invalid email", exception.getMessage());
    }

    @Test
    public void testSendEmailNotification_EmailServiceException() {
        doThrow(new RuntimeException("General error")).when(emailService).sendEmail(anyString(), anyString(), anyString());
        EmailServiceException exception = assertThrows(
                EmailServiceException.class,
                () -> emailNotificationService.sendEmailNotification("test@example.com", "Test Subject", "Test Body")
        );
        assertEquals("Errore durante l'invio dell'email", exception.getMessage());
    }

    @Test
    public void testGetEmailNotificationsByRecipient() {
        EmailNotification notification = new EmailNotification();
        notification.setRecipient("test@example.com");
        notification.setSubject("Test Subject");
        notification.setBody("Test Body");
        notification.setSentAt(LocalDateTime.now());
        notification.setSuccess(true);
        when(emailNotificationRepository.findByRecipient("test@example.com")).thenReturn(Collections.singletonList(notification));
        List<EmailNotification> notifications = emailNotificationService.getEmailNotificationsByRecipient("test@example.com");
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
        assertEquals("test@example.com", notifications.get(0).getRecipient());
    }
    @Test
    public void testSendEmailNotification_SubjectTooLong() {
        String longSubject = "a".repeat(255);
        EmailNotFoundException exception = assertThrows(
                EmailNotFoundException.class,
                () -> emailNotificationService.sendEmailNotification("test@example.com", longSubject, "Test Body")
        );
        assertEquals("l'oggetto della mail non deve superare i 254 caratteri", exception.getMessage());
    }
    @Test
    public void testGetAllEmail() {
        EmailNotification notification = new EmailNotification();
        notification.setRecipient("test@example.com");
        notification.setSubject("Test Subject");
        notification.setBody("Test Body");
        notification.setSentAt(LocalDateTime.now());
        notification.setSuccess(true);
    when(emailNotificationRepository.findAll()).thenReturn(Collections.singletonList(notification));
    List<EmailNotification> notifications = emailNotificationService.getAllEmailNotifications();
    assertNotNull(notifications);
    assertFalse(notifications.isEmpty());
    assertEquals(1, notifications.size());
    assertEquals("test@example.com", notifications.get(0).getRecipient());
}
}
