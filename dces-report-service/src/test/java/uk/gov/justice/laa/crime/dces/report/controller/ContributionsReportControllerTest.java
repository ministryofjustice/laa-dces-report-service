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
class ContributionsReportControllerTest {
    private static final LocalDate startPeriod = LocalDate.of(2021, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2021, 1, 31);
    private static final String MAAT_ID_EXPECTED = "5635978";

    @Autowired
    ContributionFilesService fileService;

    @Autowired
    ContributionsReportController controller;


    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @Test
    void givenValidPeriod_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() throws JAXBException, IOException {
        File report = controller.getContributionFiles(startPeriod, finishPeriod);

        assertThat(report).isNotNull().isNotEmpty().isFile();
        assertThat(report.getName())
            .matches("Contributions_.*csv")
            .contains(fileService.getFileName(startPeriod, finishPeriod));

        assertThat(searchInFile(report, MAAT_ID_EXPECTED)).isTrue();
        assertThat(searchInFile(report, "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,"))
                .isTrue();
    }

    private boolean searchInFile(File file, String toSearchFor) throws IOException {
        return Optional.ofNullable(FileUtils.readText(file))
            .orElse("")
            .contains(toSearchFor);
    }
}