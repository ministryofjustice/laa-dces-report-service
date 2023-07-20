package uk.gov.justice.laa.crime.dces.report.service;

import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.controller.ContributionsReportController;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("connectiontest")
class ContributionsFileServiceConnectionTest {
    private static final LocalDate startPeriod = LocalDate.of(2021, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2021, 1, 31);
    private static final String MAAT_ID_EXPECTED = "5635978";

    @Autowired
    ContributionFilesService filesService;

    @Test
    void givenValidPeriod_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() {
        ContributionFilesResponse contributionFiles = filesService.getFiles(startPeriod, finishPeriod);

    }
}