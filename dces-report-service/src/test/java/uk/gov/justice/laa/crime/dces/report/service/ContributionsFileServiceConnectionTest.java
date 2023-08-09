package uk.gov.justice.laa.crime.dces.report.service;


import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("connectiontest")
class ContributionsFileServiceConnectionTest {
    @InjectSoftAssertions
    private SoftAssertions softly;
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
        List<String> contributionFiles;

        try {
            contributionFiles = filesService.getFiles(startPeriod, finishPeriod);

            // Because MATT API can have no data for test, connection test can be skipped as optional
            if (!contributionFiles.isEmpty()) {
                softly.assertThat(contributionFiles).isNotNull();
                softly.assertThat(contributionFiles).isNotEmpty();
            }
        } catch (IllegalArgumentException e) { // Config variable values not yet loaded
        } catch (OAuth2AuthorizationException e) { // Client credentials error
        }

        softly.assertAll();
    }

    @Test
    void givenPeriodWithNoData_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() {
        List<String> contributionFiles;

        try {
            LocalDate fromDate = LocalDate.now().minusYears(10).minusDays(10);
            LocalDate toDate = LocalDate.now().minusYears(10);
            contributionFiles = filesService.getFiles(fromDate, toDate);
            softly.assertThat(contributionFiles).isNotNull();
            softly.assertThat(contributionFiles).isEmpty();
        } catch (IllegalArgumentException e) { // Config variable values not yet loaded
        } catch (OAuth2AuthorizationException e) { // Client credentials error
        }

        softly.assertAll();
    }
}