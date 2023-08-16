package uk.gov.justice.laa.crime.dces.report.utils.email;

import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailObjectInvalidException;

public interface EmailClient {
    void send(EmailObject emailObject) throws EmailClientException, EmailObjectInvalidException;
}
