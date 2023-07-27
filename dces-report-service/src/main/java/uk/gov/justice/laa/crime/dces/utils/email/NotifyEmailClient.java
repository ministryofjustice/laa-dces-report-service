package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Component
final public class NotifyEmailClient implements EmailClient {

    @Value("${emailClient.notify.key}")
    private String key;

    @Override
    public void send(EmailObject emailObject) throws RuntimeException {
        NotifyEmailObject mail = (NotifyEmailObject) emailObject;
        log.info("attempt to send email...");
        try {
            mail.validate();
            getNotifyInstance().sendEmail(
                    mail.getTemplateId(),
                    mail.getEmailAddress(),
                    mail.getPersonalisation(),
                    mail.getReference(),
                    mail.getEmailReplyToId()
            );
        } catch (NotificationClientException e) {
            log.error("sending email failed with error : {}", e.getMessage());
            throw new EmailClientException(e.getMessage());
        }
        log.info("email sent successfully");
    }

    private NotificationClient getNotifyInstance() {
        return new NotificationClient(this.key);
    }
}
