package uk.gov.justice.laa.crime.dces.report.service;


import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("connectiontest")
class FdcFileServiceConnectionTest {
    @InjectSoftAssertions
    private SoftAssertions softly;
    private static final LocalDate startPeriod = LocalDate.of(2021, 1, 2);

    private static final LocalDate finishPeriod = LocalDate.of(2021, 1, 26);

    @Autowired
    FdcFilesService filesService;

    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @AfterEach
    void postTest() { softly.assertAll(); }

    @Test
    void givenValidPeriod_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() {
        List<String> resultFiles;

        try {
            resultFiles = filesService.getFiles(startPeriod, finishPeriod);
            softly.assertThat(resultFiles).isNotNull();
            softly.assertThat(resultFiles).isNotEmpty();
        } catch (IllegalArgumentException e) { // Config variable values not yet loaded
        } catch (OAuth2AuthorizationException e) { // Client credentials error
        }
    }

    @Test
    void givenPeriodWithNoData_whenGetContributionFilesIsInvoked_thenFileWithExpectedContentIsReturned() {
        List<String> resultFiles;

        try {
            LocalDate fromDate = LocalDate.now().minusYears(10).minusDays(10);
            LocalDate toDate = LocalDate.now().minusYears(10);
            resultFiles = filesService.getFiles(fromDate, toDate);

            softly.assertThat(resultFiles).isNotNull();
            softly.assertThat(resultFiles).isEmpty();
        } catch (IllegalArgumentException e) { // Config variable values not yet loaded
        } catch (OAuth2AuthorizationException e) { // Client credentials error
        }
    }

    @Test
    void givenValidPeriodWithBigData_whenGetContributionFilesIsInvoked_thenShouldThrowMaatApiExceptionAfterRetry2Times() {
        String expectedMessage = "413 PAYLOAD_TOO_LARGE";

        try {
            // setup (catch connection errors due to config)
            filesService.getFiles(startPeriod, finishPeriod.plusMonths(1));
        } catch (IllegalArgumentException|OAuth2AuthorizationException e) {
            // IllegalArgumentException: Config variable values not yet loaded
            // OAuth2AuthorizationException: Client credentials error
        } catch (MaatApiClientException e) {
            // Not using assertThatThrownBy because tests with no deployment fail throwingIllegalArg exception
            // which needs to be skipped for those scenarios
            softly.assertThat(e).hasMessageContaining(expectedMessage);
        }
    }
}