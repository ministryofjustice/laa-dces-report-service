package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.ServicesConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableConfigurationProperties(value = ServicesConfiguration.class)
public class MaatApiConfigurationTest {

    @Autowired
    @Qualifier("test_configuration")
    private ServicesConfiguration configuration;

    @Autowired
    Environment env;

    @Configuration
    public static class MaatApiConfigurationFactory {
        @Bean(name = "test_configuration")
        public ServicesConfiguration getDefaultConfiguration() {
            return new ServicesConfiguration();
        }
    }

    @Test
    public void givenDefinedBasedURL_whenGetBaseUrlIsInvoked_thenCorrectBaseURLIsReturned() {
        assertThat(configuration.getEformApi().getBaseUrl()).isEqualTo("http://localhost:8090");
    }
}
