package uk.gov.justice.laa.crime.dces.report.utils.email.exceptions;

public class EmailObjectInvalidException extends RuntimeException {
    public EmailObjectInvalidException(String message) {
        super(message);
    }
}
