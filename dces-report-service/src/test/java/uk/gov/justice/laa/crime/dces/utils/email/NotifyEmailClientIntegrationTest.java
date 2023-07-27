package uk.gov.justice.laa.crime.dces.utils.email;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = {NotifyEmailClient.class, NotifyEmailObject.class})
final class NotifyEmailClientIntegrationTest {

    private static final String TEMPLATE_ID = "0f3438d7-78fd-4519-972e-4038084558c1";

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

        String emailAddress = "victor.olorunleye@digital.justice.gov.uk";
        testEmailObject = new NotifyEmailObject(
                TEMPLATE_ID,
                emailAddress,
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
    void testSendingEmail() throws RuntimeException {
        testNotifyEmailClient.send(testEmailObject);
    }
}