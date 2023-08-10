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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.justice.laa.crime.dces.report.service.FdcFilesService;
import uk.gov.service.notify.NotificationClient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    private static final String MAAT_ID_EXPECTED = "5635978";

    @Autowired
    ContributionFilesService contributionsFileService;
    @Autowired
    FdcFilesService fdcFilesService;

    @Autowired
    DcesReportController controller;

    @MockBean
    NotificationClient notifyClient;

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
        assertDoesNotThrow(() -> controller.getContributionsReport(startPeriod, finishPeriod));
    }

    @Test
    void givenValidPeriod_whenGetFdcReportIsInvoked_thenFileWithExpectedContentIsReturned() {
        assertDoesNotThrow(() -> controller.getFdcReport(fdcReportDate, fdcReportDate));
    }

    @Test
    void givenDateWithNoData_whenGetContributionsReportIsInvoked_thenDcesReportSourceFilesDataNotFoundIsThrown() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        String expectedMessage = "NOT FOUND";

        // execute
        softly.assertThatThrownBy(() -> controller.getContributionsReport(testDate, testDate))
                .isInstanceOf(DcesReportSourceFilesDataNotFound.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenDateNotMappedOnStub_whenGetContributionsReportIsInvoked_then404WebClientResponseExceptionIsThrownAfter2Retries() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 30);
        String expectedMessage = "404 Not Found";

        // execute
        softly.assertThatThrownBy(() -> controller.getContributionsReport(testDate, testDate))
                .isInstanceOf(WebClientResponseException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenDateWithNoData_whenGetFdcReportIsInvoked_thenDcesReportSourceFilesDataNotFoundIsThrown() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        String expectedMessage = "NOT FOUND";

        // execute
        softly.assertThatThrownBy(() -> controller.getFdcReport(testDate, testDate))
                .isInstanceOf(DcesReportSourceFilesDataNotFound.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenDateNotMappedOnStub_whenGetFdcReportIsInvoked_then404WebClientResponseExceptionIsThrownAfter2Retries() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 30);
        String expectedMessage = "404 Not Found";

        // execute
        softly.assertThatThrownBy(() -> controller.getFdcReport(testDate, testDate))
                .isInstanceOf(WebClientResponseException.class)
                .hasMessageContaining(expectedMessage);
    }

    private boolean searchInFile(File file, String toSearchFor) throws IOException {
        System.out.println(FileUtils.readText(file));
        return Optional.ofNullable(FileUtils.readText(file))
                .orElse("")
                .contains(toSearchFor);
    }
}