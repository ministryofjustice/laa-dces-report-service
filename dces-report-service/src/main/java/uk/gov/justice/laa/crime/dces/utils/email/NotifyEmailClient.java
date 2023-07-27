package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@Slf4j
@NoArgsConstructor
@Component
final public class NotifyEmailClient implements EmailClient {

    @Autowired
    private NotificationClient client;

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
}
