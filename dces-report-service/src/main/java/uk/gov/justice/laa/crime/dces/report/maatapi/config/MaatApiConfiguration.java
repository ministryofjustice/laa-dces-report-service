package uk.gov.justice.laa.crime.dces.report.maatapi.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


// TODO (DCES-25): make it possible to load configuration values from own config file
@Data
@Configuration
@ConfigurationProperties(prefix = "maat-api")
public class MaatApiConfiguration {
    /**
     * The API's Base URL
     */
    @NotNull
    private String baseUrl;

    /**
     * Specify whether oAuth authentication is enabled
     */
    private boolean oAuthEnabled;
}
