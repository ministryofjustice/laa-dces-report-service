package uk.gov.justice.laa.crime.dces.report.utils.email.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "email-client.notify")
public class NotifyConfiguration {


    @NotNull
    private String key;

    @NotNull
    private String templateId;

    @NotNull
    private String environment;

    public NotifyEmailObject createEmail(
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
        personalisation.put("env", environment);

        NotifyEmailObject emailObject = new NotifyEmailObject();
        emailObject.setEmailAddresses(recipients);
        emailObject.setTemplateId(templateId);
        emailObject.setPersonalisation(personalisation);
        emailObject.addAttachment(file);

        return emailObject;
    }

    @Bean
    public NotificationClient getNotificationClient() {
        return new NotificationClient(key);
    }
}
