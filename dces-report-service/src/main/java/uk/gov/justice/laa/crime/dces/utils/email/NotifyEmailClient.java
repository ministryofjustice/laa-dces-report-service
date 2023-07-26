package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@AllArgsConstructor
public class NotifyEmailClient implements EmailClient {

    NotificationClient client;

    @Value("${emailClient.notify.key}")
    private String key;

    @Override
    public void send(EmailObject emailObject) throws RuntimeException {
        NotifyEmailObject mail = (NotifyEmailObject) emailObject;
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
            throw new EmailClientException(e.getMessage());
        }
    }

    @Override
    public EmailClient getInstance() {
        return new NotifyEmailClient();
    }

    private NotifyEmailClient() {
        client = new NotificationClient(key);
    }
}
