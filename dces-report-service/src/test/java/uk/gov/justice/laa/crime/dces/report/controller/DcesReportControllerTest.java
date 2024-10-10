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
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesService;
import uk.gov.justice.laa.crime.dces.report.service.FdcFilesService;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.exception.EmailClientException;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static uk.gov.justice.laa.crime.dces.report.scheduler.DcesReportScheduler.ReportPeriod.Monthly;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class DcesReportControllerTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Autowired
    ContributionFilesService contributionsFileService;
    @Autowired
    FdcFilesService fdcFilesService;

    @Autowired
    DcesReportController controller;

    @MockBean
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
    void givenValidPeriod_whenGetContributionsReportIsInvoked_thenNoExceptionIsThrown() {
        assertDoesNotThrow(() -> controller.getContributionsReport(Monthly.getDescription()));
    }

    @Test
    void givenValidPeriod_whenGetFdcReportIsInvoked_thenNoExceptionIsThrown() {
        assertDoesNotThrow(() -> controller.getFdcReport(Monthly.getDescription()));
    }

    @Test
    void givenInvalidPeriod_whenGetFdcReportIsInvoked_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> controller.getFdcReport("Yearly"));
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
                        controller.getContributionsReport(Monthly.getDescription()))
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