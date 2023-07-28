package uk.gov.justice.laa.crime.dces.utils.email.exceptions;

public class EmailObjectInvalidException extends RuntimeException {
    public EmailObjectInvalidException(String message) {
        super(message);
    }
}
