package email.userservice.controller;

import email.userservice.model.EmailNotification;
import email.userservice.service.EmailNotificationService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailNotificationController {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationController.class);
    @Autowired
    private EmailNotificationService emailNotificationService;

    @PostMapping("/send")
    public String sendEmailNotification(@RequestBody EmailNotificationRequest request) {
       EmailNotification response= emailNotificationService.sendEmailNotification(request.getRecipient(), request.getSubject(), request.getBody());
        logger.info("Email inviata a {}", request.getRecipient());
      if(response.isSuccess())
        return "Email notification sent successfully";
      else
      {
          return "Email notification NOT sent successfully";
      }
    }
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }
    @GetMapping("/history/{recipient}")
    public List<EmailNotification> getEmailNotificationHistory(@PathVariable String recipient) {
        logger.info("Recupero delle notifiche per l'utente con email {}", recipient);
        return emailNotificationService.getEmailNotificationsByRecipient(recipient);
    }
    @GetMapping("/history")
    public List<EmailNotification> getAllEmailNotificationHistory() {
        logger.info("Recupero Tutto");
        return emailNotificationService.getAllEmailNotifications();
    }

    @Setter
    @Getter
    public static class EmailNotificationRequest {
        private String recipient;
        private String subject;
        private String body;

    }
}
