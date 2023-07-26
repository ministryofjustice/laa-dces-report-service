package uk.gov.justice.laa.crime.dces.utils.email;

public interface EmailObject {

    String getEmailAddress();

    void validate() throws RuntimeException;
}
