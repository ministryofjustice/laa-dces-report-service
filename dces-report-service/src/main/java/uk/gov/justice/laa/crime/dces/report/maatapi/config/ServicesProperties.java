package uk.gov.justice.laa.crime.dces.report.maatapi.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "services")
public class ServicesProperties {

    @NotNull
    private MaatApi maatApi;

    @Data
    public static class MaatApi {

        @NotNull
        private String baseUrl;

        private boolean oAuthEnabled;

        private int maxBufferSize = 1;
    }
}
