package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.ServicesProperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableConfigurationProperties(value = ServicesProperties.class)
@ActiveProfiles("test")
class ServicePropertiesTest {
    @Autowired
    private ServicesProperties services;

    @Autowired
    Environment env;

    @Test
    void givenDefinedBasedURL_whenGetBaseUrlIsInvoked_thenCorrectBaseURLIsReturned() {
        assertThat(services.getMaatApi().getBaseUrl()).isEqualTo("http://localhost:1111");
    }
}
