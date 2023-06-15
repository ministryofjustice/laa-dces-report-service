package uk.gov.justice.laa.crime.dces.report.mattapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfiguration {
//    @NotNull
    private Integer maxRetries;

//    @NotNull
    private Integer minBackOffPeriod;

//    @NotNull
    private Double jitterValue;
}
