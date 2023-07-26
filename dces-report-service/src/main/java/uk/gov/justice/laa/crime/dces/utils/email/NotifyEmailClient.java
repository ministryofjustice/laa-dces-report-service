package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@AllArgsConstructor
@Slf4j
public class NotifyEmailClient implements EmailClient {

    NotificationClient client;

    @Value("${emailClient.notify.key}")
    private String key;

    @Override
    public void send(EmailObject emailObject) throws RuntimeException {
        NotifyEmailObject mail = (NotifyEmailObject) emailObject;
        log.info("attempt to send email...");
        try {
            mail.validate();
            client.sendEmail(
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

    public static EmailClient getInstance() {
        return new NotifyEmailClient();
    }

    private NotifyEmailClient() {
        client = new NotificationClient(key);
    }
}
