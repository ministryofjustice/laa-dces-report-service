package uk.gov.justice.laa.crime.dces.utils.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.justice.laa.crime.dces.utils.email.exceptions.EmailObjectInvalidException;

import java.util.Map;

@AllArgsConstructor
@Data
public class NotifyEmailObject implements EmailObject {

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
    }

    public void addAttachment() {

    }
}
