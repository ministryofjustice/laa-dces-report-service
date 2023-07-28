package uk.gov.justice.laa.crime.dces.utils.email;

import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailClientException;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;

public interface EmailClient {
    void send(EmailObject emailObject) throws EmailClientException, EmailObjectInvalidException;
}
