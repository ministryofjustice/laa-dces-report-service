package uk.gov.justice.laa.crime.dces.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static uk.gov.justice.laa.crime.dces.report.service.MailerService.sendEmail;

@SpringBootTest
@ContextConfiguration(classes = {NotifyEmailClient.class, NotifyEmailObject.class, NotifyConfiguration.class})
final class NotifyEmailClientIntegrationTest {

    // test template may need to be updated prior to running test
    private static final String TEMPLATE_ID = "bd605f8d-3cc5-423f-95fb-6465535a452a";

    // UPDATE RECIPIENT EMAIL ADDRESS BEFORE RUNNING TEST
    private static final List<String> TEST_RECIPIENT = List.of("rahodav340@rc3s.com");

    private NotifyEmailObject testEmailObject;

    @Autowired
    private NotifyConfiguration notifyConfiguration;

    @Autowired
    private EmailClient testNotifyEmailClient;

    @BeforeEach
    void setup() throws IOException, NotificationClientException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testContributionReport.csv").getFile());

        testEmailObject = notifyConfiguration.createEmail(
                file,
                "Contribution",
                "Test",
                LocalDate.of(2023, 8, 10),
                LocalDate.now(),
                TEMPLATE_ID,
                TEST_RECIPIENT
        );
    }

    @Test
    void testSendingEmail() {
        assertDoesNotThrow(() -> sendEmail(testEmailObject, testNotifyEmailClient));
    }
}
