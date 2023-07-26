package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
@Data
@Service
public class NotifyEmailObject implements EmailObject {

    private static String UPLOAD_KEY = "link_to_file";

    String templateId;
    String emailAddress;
    Map<String, Object> personalisation;
    String reference;
    String emailReplyToId;

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public void validate() throws RuntimeException {
        if (emailAddress.isEmpty()) {
            throw new EmailObjectInvalidException("email address cannot be empty on email object");
        }

        if (!personalisation.containsKey(UPLOAD_KEY)) {
            throw new EmailObjectInvalidException("file attachment cannot be empty on email object");
        }
    }

    public void addAttachment(File file) throws IOException, NotificationClientException {
        byte[] fileContents = FileUtils.readFileToByteArray(file);
        personalisation.put(UPLOAD_KEY, NotificationClient.prepareUpload(fileContents, true));
    }
}
