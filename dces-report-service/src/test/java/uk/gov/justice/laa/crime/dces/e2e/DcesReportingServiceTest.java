package uk.gov.justice.laa.crime.dces.e2e;

import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.DcesReportServiceApplication;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.client.FdcFilesClient;
import uk.gov.justice.laa.crime.dces.report.controller.DcesReportController;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.service.FailuresReportService;
import uk.gov.justice.laa.crime.dces.report.service.FdcFilesService;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ContextConfiguration(classes = DcesReportServiceApplication.class)
final class DcesReportingServiceTest {

    @Autowired
    private ContributionFilesService contributionFilesService;

    @Autowired
    private FdcFilesService fdcFilesService;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @SpyBean
    private DcesReportService spyReporting;

    @SpyBean
    private ContributionFilesClient spyContributionsClient;

    @SpyBean
    private FdcFilesClient spyFdcFilesClient;

    @SpyBean
    private FailuresReportService spyFailuresReportService;

    @SpyBean
    private NotifyEmailClient spyEmailClient;

    @Autowired
    private DcesReportController controller;

    @Autowired
    private NotifyConfiguration notifyConfiguration;

    private LocalDate start;

    private LocalDate end;

    @BeforeEach
    void setup() {
        start = LocalDate.of(2018, 8, 1);
        end = LocalDate.of(2018, 8, 30);
    }

    @Test
    void assertContributionsEndpointDataIsConsistent() {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        List<String> contributions = contributionFilesService.getFiles(start, end);

        // assert
        softly.assertThat(contributions.size()).isEqualTo(2);
    }

    @Test
    void assertFdcEndpointDataIsConsistent() {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        List<String> contributions = fdcFilesService.getFiles(start, end);

        // assert
        softly.assertThat(contributions.size()).isEqualTo(0);
    }

    @Test
    void confirmContributionsReportRequestFails() throws NotificationClientException, JAXBException, IOException {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        softly.assertThatThrownBy(() -> controller.getContributionsReport("AdHoc", start.minusDays(2), start.minusDays(2)))
                .isInstanceOf(DcesReportSourceFilesDataNotFound.class);

        // assert
        Mockito.verify(spyReporting, times(1)).sendContributionsReport("AdHoc", start.minusDays(2), start.minusDays(2));
        Mockito.verify(spyContributionsClient, times(1)).getContributions(any(), any());
        Mockito.verify(spyEmailClient, times(0)).send(any());
    }

    @Test
    void confirmContributionsReportRunsSuccessfully() throws NotificationClientException, JAXBException, IOException {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        controller.getContributionsReport("Daily", start, end);

        // assert
        Mockito.verify(spyReporting, times(1)).sendContributionsReport("Daily", start, end);
        Mockito.verify(spyContributionsClient, times(30)).getContributions(any(), any());
        Mockito.verify(spyEmailClient, times(1)).send(any());
    }

    @Test
    void fdcReportRequestFails() throws NotificationClientException, JAXBException, IOException {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        softly.assertThatThrownBy(() -> controller.getFdcReport("Daily", start, end))
                .isInstanceOf(DcesReportSourceFilesDataNotFound.class);

        // assert
        Mockito.verify(spyReporting, times(1)).sendFdcReport("Daily", start, end);
        Mockito.verify(spyFdcFilesClient, times(1)).getContributions(any(), any());
        Mockito.verify(spyEmailClient, times(0)).send(any());
    }

    @Test
    void fdcReportRunsSuccessfully() throws NotificationClientException, JAXBException, IOException {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        LocalDate date = LocalDate.of(2023, 7, 3);
        controller.getFdcReport("Daily", date, date);


        // assert
        Mockito.verify(spyReporting, times(1)).sendFdcReport("Daily", date, date);
        Mockito.verify(spyFdcFilesClient, times(1)).getContributions(any(), any());
        Mockito.verify(spyEmailClient, times(1)).send(any());
    }


    @Test
    void failuresReportRunsSuccessfully() throws NotificationClientException, IOException {
        // setup
        if (!notifyConfiguration.getEnvironment().equals("development")) {
            return;
        }

        // execute
        LocalDate date = LocalDate.of(2023, 7, 3);
        controller.getFailuresReport("Daily", date);


        // assert
        Mockito.verify(spyReporting, times(1)).sendFailuresReport("Daily", date);
        Mockito.verify(spyFailuresReportService, times(1)).generateReport(any(), any());
        Mockito.verify(spyEmailClient, times(1)).send(any());
    }

}
