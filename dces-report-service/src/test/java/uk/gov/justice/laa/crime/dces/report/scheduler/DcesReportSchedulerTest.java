package uk.gov.justice.laa.crime.dces.report.scheduler;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;

import java.util.List;

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
    void givenDefaultDatePeriod_whenContributionsReport_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.contributionsReport());
    }

    @Test
    void givenDefaultDatePeriod_whenFdcReport_thenNoExceptionShouldBeThrown() {
        assertDoesNotThrow(() -> scheduler.fdcReport());
    }
}