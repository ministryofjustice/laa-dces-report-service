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
//@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = MaatApiConfiguration.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
//@DirtiesContext
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
        System.out.println("Here");
        System.out.println(env.getProperty("means-assessment.security.issuer-uri"));
        System.out.println("There");
        System.out.println(env.getProperty("maatApi.assessments-domain"));
        System.out.println("Overhere");
        System.out.println(configuration.getBaseUrl());
        System.out.println("Overthere");
        MaatApiConfiguration conf2 = new MaatApiConfiguration();
        System.out.println(conf2.getBaseUrl());
        System.out.println("Bye");

//        assertThat(configuration.getBaseUrl()).isEqualTo("/api/internal/v1/assessment/");
    }

    private String buildUrl(String url) {
        return String.format("/api/internal/v1/assessment/%s", url);
    }
}
