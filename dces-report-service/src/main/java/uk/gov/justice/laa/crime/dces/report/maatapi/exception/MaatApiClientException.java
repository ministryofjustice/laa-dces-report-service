package uk.gov.justice.laa.crime.dces.report.maatapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class MaatApiClientException extends ResponseStatusException {

    public MaatApiClientException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public MaatApiClientException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
    }
}
