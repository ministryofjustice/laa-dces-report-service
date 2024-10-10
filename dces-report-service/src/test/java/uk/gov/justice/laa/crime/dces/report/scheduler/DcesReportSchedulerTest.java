package uk.gov.justice.laa.crime.dces.report.scheduler;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class DcesReportSchedulerTest {

    @MockBean
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

}