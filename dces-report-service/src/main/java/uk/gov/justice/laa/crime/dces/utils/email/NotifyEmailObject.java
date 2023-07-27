package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;

import java.io.File;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class NotifyEmailObject implements EmailObject {

    private static String UPLOAD_KEY = "link_to_file";

    private String templateId;
    private String emailAddress;
    private Map<String, Object> personalisation;
    private String reference;
    private String emailReplyToId;

    @Override
    public void validate() throws RuntimeException {
        if (emailAddress.isEmpty()) {
            throw new EmailObjectInvalidException("email address cannot be empty on email object");
        }

        if (!personalisation.containsKey(UPLOAD_KEY)) {
            throw new EmailObjectInvalidException("file attachment cannot be empty on email object");
        }
    }

    @Override
    public void addAttachment(File file) throws Exception {
        byte[] fileContents = FileUtils.readFileToByteArray(file);
        personalisation.put(UPLOAD_KEY, NotificationClient.prepareUpload(fileContents, true));
    }
}
