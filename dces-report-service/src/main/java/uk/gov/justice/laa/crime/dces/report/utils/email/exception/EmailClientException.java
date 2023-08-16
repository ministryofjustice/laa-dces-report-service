package uk.gov.justice.laa.crime.dces.report.utils.email.exception;

public class EmailClientException extends RuntimeException {
    public EmailClientException(String message) {
        super(message);
    }
}
