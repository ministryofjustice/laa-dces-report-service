package uk.gov.justice.laa.crime.dces.report.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class ContributionsReportControllerTest {
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);
    private static final String MAAT_ID_EXPECTED = "5635978";

    @Autowired
    ContributionFilesService fileService;

    @Autowired
    ContributionsReportController controller;


    @Test
    void getContributionFiles() throws JAXBException, IOException {
        File report = controller.getContributionFiles(startPeriod, finishPeriod);

        assertThat(report).isNotNull().isNotEmpty().isFile();
        assertThat(report.getName())
            .matches("Contributions_.*csv")
            .contains(fileService.getFileName(startPeriod, finishPeriod));
        
        assertThat(fileService.searchInFile(report, MAAT_ID_EXPECTED)).isTrue();
    }
}