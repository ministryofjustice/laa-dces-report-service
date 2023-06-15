package uk.gov.justice.laa.crime.dces.report.mattapi;

public class MattApiClientException  extends RuntimeException {
    public MattApiClientException() {
        super();
    }

    public MattApiClientException(String message) {
        super(message);
    }

    public MattApiClientException(Throwable rootCause) {
        super(rootCause);
    }

    public MattApiClientException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
