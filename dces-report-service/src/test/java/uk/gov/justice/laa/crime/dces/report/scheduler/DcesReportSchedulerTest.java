package uk.gov.justice.laa.crime.dces.report.scheduler;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class DcesReportSchedulerTest {

    @MockitoBean
    DcesReportService dcesReportService;

    @Autowired
    DcesReportScheduler scheduler;


    @Test
    void givenDefaultDatePeriod_whenContributionsReportMonthly_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.contributionsReportMonthly());
    }

    @Test
    void givenDefaultDatePeriod_whenContributionsReportDaily_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.contributionsReportDaily());
    }

    @Test
    void givenDefaultDatePeriod_whenFdcReportMonthly_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.fdcReportMonthly());
    }

    @Test
    void givenDefaultDatePeriod_whenFdcReportDaily_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.fdcReportDaily());
    }

    @Test
    void givenDefaultDate_whenFailuresReport_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.failuresReportDaily());
    }

    @Test
    void givenAValidCronJob_whenCaseSubmissionErrorReportDailyIsInvoked_shouldReportGenerated()
            throws NotificationClientException, JAXBException, IOException {
        scheduler.caseSubmissionErrorReportDaily();
        verify(dcesReportService).sendCaseSubmissionErrorReport(anyString(),any(LocalDateTime.class));
    }

}