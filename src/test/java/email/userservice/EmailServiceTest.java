package email.userservice;
import email.userservice.exception.EmailNotFoundException;
import email.userservice.exception.EmailServiceException;
import email.userservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmailSuccess() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Text";

        emailService.sendEmail(to, subject, text);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmailThrowsEmailNotFoundException() {
        doThrow(new MailSendException("Invalid address")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(EmailNotFoundException.class, () -> {
            emailService.sendEmail("invalid@example.com", "Test Subject", "Test Text");
        });
    }

    @Test
    public void testSendEmailThrowsEmailServiceException() {
        doThrow(new RuntimeException("Generic error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(EmailServiceException.class, () -> {
            emailService.sendEmail("test@example.com", "Test Subject", "Test Text");
        });
    }
}