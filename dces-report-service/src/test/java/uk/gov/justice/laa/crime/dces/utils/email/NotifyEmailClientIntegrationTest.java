package uk.gov.justice.laa.crime.dces.utils.email;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = NotifyEmailClientIntegrationTest.class)
class NotifyEmailClientIntegrationTest {

    private static final String TEMPLATE_ID = "6b742f6c-6d02-4ef9-adb6-3d575528ac6b";

    EmailObject testEmailObject;

    EmailClient testNotifyEmailClient;

    @Before
    void setUp() throws IOException, NotificationClientException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testContributionReport.csv").getFile());
        byte[] fileContents = FileUtils.readFileToByteArray(file);

        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("link_to_file", NotificationClient.prepareUpload(fileContents, true));

        testEmailObject = new NotifyEmailObject(
                TEMPLATE_ID,
                "rahodav340@rc3s.com",
                personalisation,
                "voTest",
                ""
        );
    }

    @Test
    void testSendingEmail() throws RuntimeException {
        testNotifyEmailClient = NotifyEmailClient.getInstance();

        testNotifyEmailClient.send(testEmailObject);
    }
}