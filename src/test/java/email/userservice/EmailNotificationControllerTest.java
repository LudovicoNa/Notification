package email.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import email.userservice.controller.EmailNotificationController;
import email.userservice.model.EmailNotification;
import email.userservice.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmailNotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private EmailNotificationController emailNotificationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(emailNotificationController).build();
    }

    @Test
    public void testSendEmailNotification() throws Exception {
        EmailNotificationController.EmailNotificationRequest request = new EmailNotificationController.EmailNotificationRequest();
        request.setRecipient("recipient@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        EmailNotification response = new EmailNotification();
        response.setSuccess(true);

        when(emailNotificationService.sendEmailNotification(any(String.class), any(String.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email notification sent successfully"));
    }

    @Test
    public void testSendEmailNotificationFailure() throws Exception {
        EmailNotificationController.EmailNotificationRequest request = new EmailNotificationController.EmailNotificationRequest();
        request.setRecipient("recipient@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        EmailNotification response = new EmailNotification();
        response.setSuccess(false);

        when(emailNotificationService.sendEmailNotification(any(String.class), any(String.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email notification NOT sent successfully"));
    }

    @Test
    public void testGetEmailNotificationHistory() throws Exception {
        String recipient = "recipient@example.com";
        EmailNotification notification1 = new EmailNotification();
        EmailNotification notification2 = new EmailNotification();
        notification1.setRecipient(recipient); notification1.setSubject("Subject1"); notification1.setBody("Body1");
        notification2.setRecipient(recipient); notification2.setSubject("Subject2"); notification2.setBody("Body2");

        List<EmailNotification> notifications = Arrays.asList(notification1, notification2);

        when(emailNotificationService.getEmailNotificationsByRecipient(recipient))
                .thenReturn(notifications);

        mockMvc.perform(get("/email/history/{recipient}", recipient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value(recipient))
                .andExpect(jsonPath("$[0].subject").value("Subject1"))
                .andExpect(jsonPath("$[0].body").value("Body1"))
                .andExpect(jsonPath("$[1].recipient").value(recipient))
                .andExpect(jsonPath("$[1].subject").value("Subject2"))
                .andExpect(jsonPath("$[1].body").value("Body2"));
    }

    @Test
    public void testSayHello() throws Exception {
        mockMvc.perform(get("/email"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"));
    }

    // Utility method to convert object to JSON string
    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
