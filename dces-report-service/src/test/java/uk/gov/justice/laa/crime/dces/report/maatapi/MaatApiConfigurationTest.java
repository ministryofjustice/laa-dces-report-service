package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.MaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableConfigurationProperties(value = MaatApiConfiguration.class)
public class MaatApiConfigurationTest {

    @Autowired
    @Qualifier("test_configuration")
    private MaatApiConfiguration configuration;

    @Autowired
    Environment env;

    @Configuration
    public static class MaatApiConfigurationFactory {
        @Bean(name = "test_configuration")
        public MaatApiConfiguration getDefaultConfiguration() {
            return new MaatApiConfiguration();
        }
    }

    @Test
    public void givenDefinedBasedURL_whenGetBaseUrlIsInvoked_thenCorrectBaseURLIsReturned() {
        assertThat(configuration.getBaseUrl()).isEqualTo("http://localhost:8090");
    }
}
