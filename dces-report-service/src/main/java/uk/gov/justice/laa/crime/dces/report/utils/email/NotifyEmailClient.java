package uk.gov.justice.laa.crime.dces.report.utils.email;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Arrays;

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
        mail.validate();
        try {
            sendToMultipleRecipients(mail);
        } catch (NotificationClientException e) {
            String message = e.getMessage().isEmpty() ? Arrays.stream(e.getSuppressed()).findFirst().toString() : e.getMessage();
            log.error("sending email failed with error : {}", message);
            throw new EmailClientException(message, e);
        }
        log.info("email sent successfully");
    }

    // As notify does not support sending to multiple recipients in a single api call
    // we iterate over the recipients instead.
    private void sendToMultipleRecipients(NotifyEmailObject mail) throws NotificationClientException {
        NotificationClientException exceptionStack = new NotificationClientException("");
        for (String emailAddress : mail.getEmailAddresses()) {
            try {
                client.sendEmail(
                        mail.getTemplateId(),
                        emailAddress,
                        mail.getPersonalisation(),
                        mail.getReference(),
                        mail.getEmailReplyToId()
                );
            } catch (NotificationClientException sendingException) {
                if (sendingException.getHttpResult() == HttpStatus.SC_BAD_REQUEST) {
                    exceptionStack.addSuppressed(sendingException);
                    continue;
                }
                throw sendingException;
            }
        }

        if (exceptionStack.getSuppressed().length > 0) {
            throw exceptionStack;
        }
    }
}
