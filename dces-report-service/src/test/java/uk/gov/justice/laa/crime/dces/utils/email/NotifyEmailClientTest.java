package uk.gov.justice.laa.crime.dces.utils.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = NotifyEmailClient.class)
class NotifyEmailClientTest {


    @Autowired
    NotifyEmailClient testEmailClient;

    @Mock
    NotifyEmailObject mockEmailObject;

    @MockBean
    NotificationClient mockNotifyClient;

    @BeforeEach
    void setup() {
        Map<String, Object> personalisation = new HashMap<>();
        given(mockEmailObject.getTemplateId()).willReturn("template_id");
        given(mockEmailObject.getEmailAddress()).willReturn("email");
        given(mockEmailObject.getPersonalisation()).willReturn(personalisation);
        given(mockEmailObject.getReference()).willReturn("ref");
        given(mockEmailObject.getEmailReplyToId()).willReturn("replyTo_id");
    }


    @Test
    void sendEmailObjectIsSuccess() {
        testEmailClient.send(mockEmailObject);
    }

    @Test
    void emailSendFailsWithRuntimeException() throws NotificationClientException {
        // setup
        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddress(),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(new NotificationClientException("invalid request"));

        // execute
        assertThrows(EmailClientException.class, () -> testEmailClient.send(mockEmailObject));
    }
}
