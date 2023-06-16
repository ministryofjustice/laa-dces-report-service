package uk.gov.justice.laa.crime.dces.report.mattapi;

public class MaatApiClientException extends RuntimeException {
    public MaatApiClientException() {
        super();
    }

    public MaatApiClientException(String message) {
        super(message);
    }

    public MaatApiClientException(Throwable rootCause) {
        super(rootCause);
    }

    public MaatApiClientException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
