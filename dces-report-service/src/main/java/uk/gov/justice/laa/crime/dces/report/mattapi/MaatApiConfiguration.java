package uk.gov.justice.laa.crime.dces.report.mattapi;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


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
