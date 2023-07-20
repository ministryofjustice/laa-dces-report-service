package uk.gov.justice.laa.crime.dces.report.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("connectiontest")
class ContributionsFileServiceConnectionTest {
    private static final LocalDate startPeriod = LocalDate.of(2021, 1, 2);

    private static final LocalDate finishPeriod = LocalDate.of(2021, 1, 27);
    private static final String MAAT_ID_EXPECTED = "5635978";

    @Autowired
    ContributionFilesService filesService;

    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @Test
    void givenValidPeriod_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() {
        List<String> contributionFiles = filesService.getFiles(startPeriod, finishPeriod);
        System.out.println(String.format("Total files: [%d]", contributionFiles.size()));
    }
}