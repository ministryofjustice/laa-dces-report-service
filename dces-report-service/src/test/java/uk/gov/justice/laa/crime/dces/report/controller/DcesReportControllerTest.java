package uk.gov.justice.laa.crime.dces.report.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.sentry.util.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.enums.ReportType;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.justice.laa.crime.dces.report.service.FdcFilesService;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class DcesReportControllerTest {
    @InjectSoftAssertions
    private SoftAssertions softly;
    private static final LocalDate startPeriod = LocalDate.of(2021, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2021, 1, 31);
    private static final LocalDate fdcReportDate = LocalDate.of(2023, 6, 10);

    @Autowired
    ContributionFilesService contributionsFileService;
    @Autowired
    FdcFilesService fdcFilesService;

    @Autowired
    DcesReportController controller;

    @MockitoBean
    NotifyEmailClient mockEmailClient;

    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @AfterAll
    void postTest() {
        softly.assertAll();
    }

    @Test
    void givenValidPeriod_whenGetContributionsReportIsInvoked() {
        assertDoesNotThrow(() -> controller.getContributionsReport("Test", startPeriod, finishPeriod));
    }

    @Test
    void givenDateWithNoData_whenGetContributionsReportIsInvoked_thenExceptionIsNotThrown() {
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        assertDoesNotThrow(() -> controller.getContributionsReport(ReportType.CONTRIBUTION.getDescription(), testDate, testDate));
    }

    @Test
    void givenValidPeriod_whenGetFdcReportIsInvoked_thenFileWithExpectedContentIsReturned() {
        assertDoesNotThrow(() -> controller.getFdcReport("Test", fdcReportDate, fdcReportDate));
    }

    @Test
    void givenDateWithNoData_whenGetFdcReportIsInvoked_thenExceptionIsNotThrown() {
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        assertDoesNotThrow(() -> controller.getFdcReport(ReportType.FDC.getDescription(), testDate, testDate));
    }


    @Test
    void givenDateNotMappedOnStub_whenGetContributionsReportIsInvoked_then404WebClientResponseExceptionIsThrownAfter2Retries() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 30);
        String expectedMessage = "404 Not Found";

        // execute
        softly.assertThatThrownBy(() -> controller.getContributionsReport("Test", testDate, testDate))
                .isInstanceOf(WebClientResponseException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenDateNotMappedOnStub_whenGetFdcReportIsInvoked_then404WebClientResponseExceptionIsThrownAfter2Retries() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 30);
        String expectedMessage = "404 Not Found";

        // execute
        softly.assertThatThrownBy(() -> controller.getFdcReport("Test", testDate, testDate))
                .isInstanceOf(WebClientResponseException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenInvalidEmailMock_whenGetContributionsReportIsInvoked_thenEmailClientExceptionIsThrown() {
        // setup
        String expectedMessage = "400 BAD REQUEST";
        doThrow(new EmailClientException(expectedMessage))
                .when(mockEmailClient)
                .send(any(EmailObject.class))
        ;

        // execute
        softly.assertThatThrownBy(() ->
                        controller.getContributionsReport("Test", startPeriod, finishPeriod))
                .isInstanceOf(EmailClientException.class)
                .hasMessageContaining(expectedMessage);
    }

    private boolean searchInFile(File file, String toSearchFor) throws IOException {
        System.out.println(FileUtils.readText(file));
        return Optional.ofNullable(FileUtils.readText(file))
                .orElse("")
                .contains(toSearchFor);
    }
}