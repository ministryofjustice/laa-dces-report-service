package uk.gov.justice.laa.crime.dces.report.mattapi;

//import org.junit.Test;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;


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
        // TODO (DCES-25): update expected value to what it is supposed to be resolved
        assertThat(configuration.getBaseUrl()).isEqualTo("${MAAT_API_BASE_URL}");
    }
}
