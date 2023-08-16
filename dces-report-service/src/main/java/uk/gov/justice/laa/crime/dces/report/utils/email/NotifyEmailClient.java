package uk.gov.justice.laa.crime.dces.report.utils.email;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.utils.email.exceptions.EmailClientException;
import uk.gov.justice.laa.crime.dces.report.utils.email.exceptions.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@Slf4j
@NoArgsConstructor
@Component
public final class NotifyEmailClient implements EmailClient {

    @Autowired
    private NotificationClient client;

    @Override
    public void send(EmailObject emailObject) throws EmailClientException, EmailObjectInvalidException {
        NotifyEmailObject mail = (NotifyEmailObject) emailObject;
        log.info("attempt to send email...");
        try {
            mail.validate();

            for (String emailAddress : mail.getEmailAddresses()) {
                sendEmailWithSingleRecipient(mail, emailAddress);
            }
        } catch (NotificationClientException e) {
            log.error("sending email failed with error : {}", e.getMessage());
            throw new EmailClientException(e.getMessage());
        }
        log.info("email sent successfully");
    }

    private void sendEmailWithSingleRecipient(NotifyEmailObject mail, String recipient) throws NotificationClientException {
        client.sendEmail(
                mail.getTemplateId(),
                recipient,
                mail.getPersonalisation(),
                mail.getReference(),
                mail.getEmailReplyToId()
        );
    }
}
