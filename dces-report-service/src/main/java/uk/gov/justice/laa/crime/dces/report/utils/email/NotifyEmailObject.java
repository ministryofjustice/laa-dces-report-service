package uk.gov.justice.laa.crime.dces.report.utils.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.utils.email.exceptions.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public final class NotifyEmailObject implements EmailObject {

    private static String uploadKey = "link_to_file";

    private String templateId;
    private String emailAddress;
    private Map<String, Object> personalisation;
    private String reference;
    private String emailReplyToId;

    @Override
    public void validate() throws EmailObjectInvalidException {
        if (emailAddress.isEmpty()) {
            throw new EmailObjectInvalidException("email address cannot be empty on email object");
        }

        if (!personalisation.containsKey(uploadKey)) {
            throw new EmailObjectInvalidException("file attachment cannot be empty on email object");
        }
    }

    @Override
    public void addAttachment(File file) throws IOException, NotificationClientException {
        byte[] fileContents = readFileToByteArray(file);
        personalisation.put(uploadKey, NotificationClient.prepareUpload(fileContents, true));
    }
}
