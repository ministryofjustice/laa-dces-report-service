package uk.gov.justice.laa.crime.dces.e2e;

import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.dces.report.DcesReportServiceApplication;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.controller.DcesReportController;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


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

    @SpyBean
    private DcesReportService spyReporting;

    @SpyBean
    private ContributionFilesClient spyContributionsClient;

    @SpyBean
    private NotifyEmailClient spyEmailClient;

    @Autowired
    private DcesReportController controller;


    private LocalDate start;

    private LocalDate end;

    @BeforeEach
    void setup() {
        start = LocalDate.of(2023, 7, 1);
        end = LocalDate.of(2023, 7, 4);
    }

    @Test
    void confirmRequestWithSuccess() throws NotificationClientException, JAXBException, IOException {
        // setup
        // execute
        controller.getContributionsReport(start, end);

        // assert
        Mockito.verify(spyReporting, times(1)).sendContributionsReport(start, end);
        Mockito.verify(spyContributionsClient, times(4)).getContributions(any(), any());
        Mockito.verify(spyEmailClient, times(1)).send(any());
    }

}
