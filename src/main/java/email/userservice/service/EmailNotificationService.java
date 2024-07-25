package email.userservice.service;

import email.userservice.exception.EmailNotFoundException;
import email.userservice.exception.EmailServiceException;
import email.userservice.model.EmailNotification;
import email.userservice.repository.EmailNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailNotificationService{
@Autowired
private email.userservice.service.EmailService emailService;
@Autowired
private EmailNotificationRepository emailNotificationRepository;


    public EmailNotification sendEmailNotification(String recipient, String subject, String body) {
        if(subject.length()>254)
            throw new EmailNotFoundException("l'oggetto della mail non deve superare i 254 caratteri");
        EmailNotification notification = new EmailNotification();
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setSentAt(LocalDateTime.now());
        try {
            emailService.sendEmail(recipient, subject, body);
            notification.setSuccess(true);
        } catch (MailSendException e) {
            throw new EmailNotFoundException("Indirizzo email inesistente o non valido: " + e.getMessage());
        } catch (Exception e) {
            throw new EmailServiceException("Errore durante l'invio dell'email", e);
        }
        emailNotificationRepository.save(notification);
        return notification;
    }
    public List<EmailNotification> getEmailNotificationsByRecipient(String recipient) {
        return emailNotificationRepository.findByRecipient(recipient);
    }
    public List<EmailNotification> getAllEmailNotifications() {
        return emailNotificationRepository.findAll();
    }

}
