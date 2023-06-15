package uk.gov.justice.laa.crime.dces.report.mattapi;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "maat-api")
public class MaatApiConfiguration {
    /**
     * The API's Base URL
     */
//    @NotNull
    private String baseUrl;

    /**
     * Specify whether oAuth authentication is enabled
     */
//    @NotNull
    private boolean oAuthEnabled;
}
