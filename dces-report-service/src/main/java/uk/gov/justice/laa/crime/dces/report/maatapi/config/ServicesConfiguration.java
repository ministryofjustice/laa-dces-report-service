package uk.gov.justice.laa.crime.dces.report.maatapi.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    private static final String REGISTERED_ID = "maatapi";

    @NotNull
    private MaatApi maatApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;

        private boolean oAuthEnabled;
    }
}
