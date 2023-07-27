package uk.gov.justice.laa.crime.dces.utils.email;

import java.io.File;

public interface EmailObject {

    void addAttachment(File file) throws Exception;

    void validate() throws RuntimeException;
}
