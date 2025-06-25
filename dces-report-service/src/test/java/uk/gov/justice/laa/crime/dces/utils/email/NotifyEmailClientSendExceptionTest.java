package uk.gov.justice.laa.crime.dces.utils.email;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ContextConfiguration(classes = NotifyEmailClient.class)
class NotifyEmailClientSendExceptionTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Autowired
    NotifyEmailClient testEmailClient;

    @Mock
    NotifyEmailObject mockEmailObject;

    @MockitoBean
    NotificationClient mockNotifyClient;

    @MockitoBean
    NotificationClientException mock403Exception;

    @BeforeEach
    void setup() {
        Map<String, Object> personalisation = new HashMap<>();
        given(mockEmailObject.getTemplateId()).willReturn("template_id");
        given(mockEmailObject.getEmailAddresses()).willReturn(List.of("email1", "email2", "email3"));
        given(mockEmailObject.getPersonalisation()).willReturn(personalisation);
        given(mockEmailObject.getReference()).willReturn("ref");
        given(mockEmailObject.getEmailReplyToId()).willReturn("replyTo_id");

        given(mock403Exception.getMessage()).willReturn("403 Error: Your system clock must be accurate to within 30 seconds");
        given(mock403Exception.getHttpResult()).willReturn(403);
    }


    @Test
    void sendEmailObjectIsSuccess() {
        assertDoesNotThrow(() -> testEmailClient.send(mockEmailObject));
    }

    @Test
    void emailSendingContinuesWith$400RuntimeException() throws NotificationClientException {
        // setup
        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddresses().get(0),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(new NotificationClientException("invalid request with invalid email1"));

        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddresses().get(1),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(new NotificationClientException("invalid request with invalid email2"));

        // assert / Assert
        softly.assertThatThrownBy(() -> testEmailClient.send(mockEmailObject))
                .isInstanceOf(EmailClientException.class)
                .hasMessageContaining("invalid request with invalid email1")
                .hasCauseInstanceOf(NotificationClientException.class);
        softly.assertAll();

        // asssert that the third email is still sent despite the first two failing
        Mockito.verify(mockNotifyClient, times(1)).sendEmail(mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddresses().get(2),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId());
    }

    @Test
    void emailSendFailsWithNonUserRequestErrorRuntimeException() throws NotificationClientException {
        // setup
        given(mockNotifyClient.sendEmail(
                mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddresses().get(0),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId()
        )).willThrow(mock403Exception);


        // assert / Assert
        softly.assertThatThrownBy(() -> testEmailClient.send(mockEmailObject))
                .isInstanceOf(EmailClientException.class)
                .hasMessageContaining("403 Error: Your system clock must be accurate to within 30 seconds")
                .hasCauseInstanceOf(NotificationClientException.class);
        softly.assertAll();

        // assert that sending emails stops with non 400 http status error
        Mockito.verify(mockNotifyClient, times(0)).sendEmail(mockEmailObject.getTemplateId(),
                mockEmailObject.getEmailAddresses().get(1),
                mockEmailObject.getPersonalisation(),
                mockEmailObject.getReference(),
                mockEmailObject.getEmailReplyToId());
    }
}
