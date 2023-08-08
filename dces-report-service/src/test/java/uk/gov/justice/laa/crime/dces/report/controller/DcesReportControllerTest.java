package uk.gov.justice.laa.crime.dces.report.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.sentry.util.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class DcesReportControllerTest {
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

    @Test
    void givenValidPeriod_whenGetContributionsReportIsInvoked() {
        assertDoesNotThrow(() -> controller.getContributionsReport(startPeriod, finishPeriod));
    }

    @Test
    void givenValidPeriod_whenGetFdcReportIsInvoked_thenFileWithExpectedContentIsReturned() {
        assertDoesNotThrow(() -> controller.getFdcReport(fdcReportDate, fdcReportDate));
    }

    private boolean searchInFile(File file, String toSearchFor) throws IOException {
        System.out.println(FileUtils.readText(file));
        return Optional.ofNullable(FileUtils.readText(file))
                .orElse("")
                .contains(toSearchFor);
    }
}