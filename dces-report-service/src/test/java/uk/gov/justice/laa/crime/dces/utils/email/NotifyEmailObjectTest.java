package uk.gov.justice.laa.crime.dces.utils.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = NotifyEmailObject.class)
class NotifyEmailObjectTest {

    private NotifyEmailObject notifyEmailObject;

    @BeforeEach
    void setup() {
        Map<String, Object> personalisation = new HashMap<>();
        notifyEmailObject = new NotifyEmailObject("templateId", "emailAdd", personalisation, "ref", "");
    }

    @Test
    void emailObjectIsValid() {
        // setup
        notifyEmailObject.getPersonalisation().put("link_to_file", "linkMissing");

        // execute
        notifyEmailObject.validate();
    }

    @Test
    void testInitEmailObject() {
        assertEquals("emailAdd", notifyEmailObject.getEmailAddress());
        assertEquals("templateId", notifyEmailObject.getTemplateId());
        assertEquals("ref", notifyEmailObject.getReference());
        assertEquals("", notifyEmailObject.getEmailReplyToId());
    }

    @Test
    void emailObjectWithEmailAddressEmptyIsInValid() {
        // setup
        notifyEmailObject.getPersonalisation().put("link_to_file", "linkMissing");
        notifyEmailObject.setEmailAddress("");

        // execute
        Exception exception = assertThrows(EmailObjectInvalidException.class, () -> notifyEmailObject.validate());
        assertEquals("email address cannot be empty on email object", exception.getMessage());
    }

    @Test
    void emailObjectWithNoAttachmentIsInValid() {
        // execute
        Exception exception = assertThrows(EmailObjectInvalidException.class, () -> notifyEmailObject.validate());
        assertEquals("file attachment cannot be empty on email object", exception.getMessage());
    }

    @Test
    void addAttachmentSuccess() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testContributionReport.csv").getFile());

        assertDoesNotThrow(() -> notifyEmailObject.addAttachment(file));
    }
}
