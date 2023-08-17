package uk.gov.justice.laa.crime.dces.report.maatapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@ControllerAdvice
public class MaatApiExceptionResponseHandler {
    @ExceptionHandler(MaatApiClientException.class)
    public ResponseEntity<String> handleMaatApiClientException(
            MaatApiClientException exception
    ) {
        log.error("{} :: {}", exception.getStatusCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(
            WebClientResponseException exception
    ) {
        log.error("{} :: {}", exception.getStatusCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(
            HttpServerErrorException exception
    ) {
        log.error("{} :: {}", exception.getStatusCode(), exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }
}
