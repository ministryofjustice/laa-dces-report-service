package uk.gov.justice.laa.crime.dces.report.utils.email;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailObjectInvalidException;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
@Component
public final class NotifyEmailObject implements EmailObject {

    private static String uploadKey = "link_to_file";
    private static String envKey = "link_to_file";

    private String templateId;
    private List<String> emailAddresses;
    private Map<String, Object> personalisation;
    private String reference;
    private String emailReplyToId;

    @Value(value = "${sentry.environment}")
    @Nullable
    private final String environment;

    @Override
    public void validate() throws EmailObjectInvalidException {
        if (emailAddresses.isEmpty()) {
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

    public static NotifyEmailObject createEmail(
            File file,
            String reportType,
            LocalDate fromDate,
            LocalDate toDate,
            String templateId,
            List<String> recipients) throws NotificationClientException, IOException {
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put("report_type", reportType);
        personalisation.put("from_date", fromDate.toString());
        personalisation.put("to_date", toDate.toString());

        NotifyEmailObject emailObject = new NotifyEmailObject();
        emailObject.setEmailAddresses(recipients);
        emailObject.setTemplateId(templateId);
        emailObject.setPersonalisation(personalisation);
        emailObject.getPersonalisation().put("env", emailObject.getEnvironment());
        emailObject.addAttachment(file);

        return emailObject;
    }
}
