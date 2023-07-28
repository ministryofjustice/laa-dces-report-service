package uk.gov.justice.laa.crime.dces.utils.email;

import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;

public interface EmailObject {

    void addAttachment(File file) throws IOException, NotificationClientException;

    void validate() throws EmailObjectInvalidException;
}
