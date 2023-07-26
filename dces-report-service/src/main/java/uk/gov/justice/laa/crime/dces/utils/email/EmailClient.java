package uk.gov.justice.laa.crime.dces.utils.email;

public interface EmailClient {
    void send(EmailObject emailObject) throws RuntimeException;

    EmailClient getInstance();
}
