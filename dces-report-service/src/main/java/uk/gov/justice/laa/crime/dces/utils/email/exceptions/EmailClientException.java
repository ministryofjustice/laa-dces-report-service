package uk.gov.justice.laa.crime.dces.utils.email.exceptions;

public class EmailClientException extends RuntimeException {
    public EmailClientException(String message) {
        super(message);
    }
}
