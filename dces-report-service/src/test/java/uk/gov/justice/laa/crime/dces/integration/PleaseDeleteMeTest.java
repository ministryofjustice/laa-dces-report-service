package uk.gov.justice.laa.crime.dces.integration;

import io.sentry.Sentry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = PleaseDeleteMeTest.class)
public class PleaseDeleteMeTest {

    @Test
    public void dsaf() throws Throwable {
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
