package uk.gov.justice.laa.crime.dces.utils.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;

@Configuration
public class EmailConfiguration {

    @Bean
    public NotificationClient getNotificationClient(@Value("${emailClient.notify.key}") String key) {
        return new NotificationClient(key);
    }
}
