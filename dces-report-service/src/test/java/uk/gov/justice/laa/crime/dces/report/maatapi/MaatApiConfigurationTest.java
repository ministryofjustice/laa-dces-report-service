package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.utils.maatapi.config.ServicesConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableConfigurationProperties(value = ServicesConfiguration.class)
@ActiveProfiles("test")
class MaatApiConfigurationTest {

    @Autowired
    @Qualifier("servicesConfiguration")
    private ServicesConfiguration configuration;

    @Autowired
    Environment env;

    @Test
    void givenDefinedBasedURL_whenGetBaseUrlIsInvoked_thenCorrectBaseURLIsReturned() {
        assertThat(configuration.getMaatApi().getBaseUrl()).isEqualTo("http://localhost:1111");
    }
}
