package email.userservice.service;
import email.userservice.exception.EmailNotFoundException;
import email.userservice.exception.EmailServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private  JavaMailSender mailSender;
    public void sendEmail(String to, String subject, String text) {    //Metodo per invio dell'email
      try{
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("golem@emminformatica.it");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
      }
      catch (MailSendException e) {
          throw new EmailNotFoundException("Indirizzo email inesistente o non valido: " + e.getMessage());
      } catch (Exception e) {
          throw new EmailServiceException("Errore durante l'invio dell'email", e);
      }
    }
    }



