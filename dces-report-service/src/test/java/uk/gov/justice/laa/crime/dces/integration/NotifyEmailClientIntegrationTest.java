package uk.gov.justice.laa.crime.dces.integration;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.config.EmailConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ContextConfiguration(classes = {NotifyEmailClient.class, NotifyEmailObject.class, EmailConfiguration.class})
final class NotifyEmailClientIntegrationTest {

    // test template may need to be
    private static final String TEMPLATE_ID = "7008e285-0ef0-4f29-bc95-8f59840810a7";

    // UPDATE RECIPIENT EMAIL ADDRESS BEFORE RUNNING TEST
    private static final String TEST_RECIPIENT = "rahodav340@rc3s.com";

    private NotifyEmailObject testEmailObject;

    @Autowired
    private EmailClient testNotifyEmailClient;

    @BeforeEach
    void setup() throws IOException, NotificationClientException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testContributionReport.csv").getFile());
        byte[] fileContents = FileUtils.readFileToByteArray(file);

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("link_to_file", NotificationClient.prepareUpload(fileContents, true));

        testEmailObject = new NotifyEmailObject(
                TEMPLATE_ID,
                TEST_RECIPIENT,
                personalisation,
                "voTest",
                ""
        );
        // setup template fields
        testEmailObject.getPersonalisation().put("report_type", "contribution");
        testEmailObject.getPersonalisation().put("from_date", "25-07-2023");
        testEmailObject.getPersonalisation().put("to_date", "25-07-2023");
    }

    @Test
    void testSendingEmail() {
        assertDoesNotThrow(() -> testNotifyEmailClient.send(testEmailObject));
    }
}
