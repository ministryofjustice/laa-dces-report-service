package uk.gov.justice.laa.crime.dces.report.maatapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class MaatApiExceptionResponseHandler {
    @ExceptionHandler(MaatApiClientException.class)
    public ResponseEntity<String> handleMaatApiClientException(
            MaatApiClientException exception
    ) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(
            WebClientResponseException exception
    ) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(
            HttpServerErrorException exception
    ) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }
}
