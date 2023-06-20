package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.RetryConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableConfigurationProperties(value = RetryConfiguration.class)
class RetryConfigurationTest {

    @Autowired
    @Qualifier("test_retry_configuration")
    private RetryConfiguration configuration;

    @Autowired
    Environment env;

    @Configuration
    public static class RetryConfigurationFactory {
        @Bean(name = "test_retry_configuration")
        public RetryConfiguration getDefaultConfiguration() {
            return new RetryConfiguration();
        }
    }

    @Test
    public void givenDefinedMaxRetries_whenGetMaxRetriesIsInvoked_thenCorrectValueIsReturned() {
        String expected = env.getProperty("maat-retry-config.max-retries");

        if (expected == null) expected = "2";

        assertThat(configuration.getMaxRetries())
                .isNotNull()
                .isEqualTo(Integer.valueOf(expected));
    }

    @Test
    public void givenDefinedBackOffPeriod_whenGetBackOffPeriodIsInvoked_thenCorrectValueIsReturned() {
        assertThat(configuration.getMinBackOffPeriod())
                .isEqualTo(Integer.valueOf(
                        env.getRequiredProperty ("maat-retry-config.min-back-off-period")));
    }

    @Test
    public void givenDefinedJitterValue_whenGetJitterValueIsInvoked_thenCorrectValueIsReturned() {
        assertThat(configuration.getJitterValue())
                .isEqualTo(Double.valueOf(
                        env.getRequiredProperty ("maat-retry-config.jitter-value")));
    }
}