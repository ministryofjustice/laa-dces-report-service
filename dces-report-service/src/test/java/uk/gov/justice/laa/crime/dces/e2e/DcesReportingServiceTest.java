package uk.gov.justice.laa.crime.dces.e2e;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.dces.report.DcesReportServiceApplication;

import java.time.LocalDate;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("temp")// MUST UPDATE AND DELETE BEFORE DEPLOY
@ContextConfiguration(classes = {DcesReportServiceApplication.class})
public class DcesReportingServiceTest {

    @LocalServerPort
    private int port;

    private static final String REQUEST_PATH = "/api/internal/v1/dces/report/contributions/%s/%s";

    @Autowired
    private TestRestTemplate restTemplate;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeEach
    void setup() {
        // init setup
    }

    @Test
    void confirmRequestWithSuccess() {
        // setup
        String path = String.format(REQUEST_PATH, LocalDate.now(), LocalDate.now());

        // execute
        String response = restTemplate.getForObject("http://localhost:" + port + path, String.class);

        // assert
        softly.assertThat(response).isNull();
    }
}
