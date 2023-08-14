package uk.gov.justice.laa.crime.dces.report.service;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;

@Component
public final class MailerService {
    
    private MailerService() {
    }

    public static void sendEmail(EmailObject emailObject, EmailClient emailClient) {
        emailClient.send(emailObject);
    }
}
