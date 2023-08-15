package uk.gov.justice.laa.crime.dces.utils.email;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = NotifyEmailClient.class)
class NotifyEmailClientTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

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
        assertDoesNotThrow(() -> testEmailClient.send(mockEmailObject));
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

    @Test
    void givenInvalidEmailAddress_whenSendIsInvoked_thenNotificationClientExceptionIsThrown() throws NotificationClientException {
        // setup
        mockEmailObject.setEmailAddress("mock1@email.com,mock2@email.com");
        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddress(),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(new EmailClientException("400 BAD REQUEST - email_address Not a valid email address"));

        String expectedMessage = "BAD REQUEST";

        // execute
        softly.assertThatThrownBy(() -> testEmailClient.send(mockEmailObject))
                .isInstanceOf(EmailClientException.class)
                .hasMessageContaining(expectedMessage);
        softly.assertAll();
    }

    @Test
    void givenInvalidEmailAddress_whenSendIsInvokedAndNotificationClientExceptionIsThrown_thenEmailClientExceptionIsReThrown() throws NotificationClientException {
        // setup
        mockEmailObject.setEmailAddress("mock1@email.com,mock2@email.com");
        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddress(),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(new NotificationClientException("400 BAD REQUEST - email_address Not a valid email address"));

        String expectedMessage = "BAD REQUEST";

        // execute
        softly.assertThatThrownBy(() -> testEmailClient.send(mockEmailObject))
                .isInstanceOf(EmailClientException.class)
                .hasMessageContaining(expectedMessage);
        softly.assertAll();
    }
}
