package uk.gov.justice.laa.crime.dces.report.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.justice.laa.crime.dces.report.service.FdcFilesService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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


    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @Test
    void givenValidPeriod_whenGetContributionsReportIsInvoked_thenFileWithExpectedContentIsReturned() throws JAXBException, IOException {
        File report = controller.getContributionsReport(startPeriod, finishPeriod);

        assertThat(report).isNotNull().isNotEmpty().isFile();
        assertThat(report.getName())
            .matches("Contributions_.*csv")
            .contains(contributionsFileService.getFileName(startPeriod, finishPeriod));

        assertThat(searchInFile(report, MAAT_ID_EXPECTED)).isTrue();
        assertThat(searchInFile(report, "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,"))
                .isTrue();
    }

    @Test
    void givenValidPeriod_whenGetFdcReportIsInvoked_thenFileWithExpectedContentIsReturned() throws JAXBException, IOException {
        File report = controller.getFdcReport(fdcReportDate, fdcReportDate);

        assertThat(report).isNotNull().isNotEmpty().isFile();
        assertThat(report.getName())
                .matches("FDC_.*csv")
                .contains(fdcFilesService.getFileName(fdcReportDate, fdcReportDate));

        assertThat(searchInFile(report, MAAT_ID_EXPECTED)).isTrue();
        assertThat(searchInFile(report, "5635978,30/09/2016,22/12/2016,1774.4,1180.64,593.76"))
                .isTrue();
    }

    private boolean searchInFile(File file, String toSearchFor) throws IOException {
        System.out.println(FileUtils.readText(file));
        return Optional.ofNullable(FileUtils.readText(file))
            .orElse("")
            .contains(toSearchFor);
    }
}