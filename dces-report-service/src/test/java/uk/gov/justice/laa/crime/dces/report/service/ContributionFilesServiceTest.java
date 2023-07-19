package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class ContributionFilesServiceTest {

    @Autowired
    ContributionFilesService contributionFilesReportService;

    @Before
    void setup() {
        Locale.setDefault(new Locale("en", "UK"));
    }

    @Test
    void givenValidDateLimitParams_whenGetFilesIsInvoked_thenResponseDataModelIsReturned() throws WebClientResponseException {
        LocalDate startPeriod = LocalDate.of(2023, 1, 1);
        LocalDate finishPeriod = LocalDate.of(2023, 1, 31);
        ContributionFilesResponse result = contributionFilesReportService.getFiles(startPeriod, finishPeriod);

        assertNotNull(result);
        assertEquals(2, result.getFiles().size());
        assertTrue(result.getFiles().get(0).contains("id=\"222772044"));
        assertTrue(result.getFiles().get(1).contains("id=\"222772045"));
    }

    // TODO (DCES-55): Debug and find a fix to mapping errors in CircleCI and then re-enable this test by uncommenting @Test tag
//    @Test
    void givenInternalServerError_whenGetFilesIsInvoked_thenHttpServerErrorExceptionIsThrown() {
        // setup
        LocalDate date = LocalDate.of(5500, 5, 5);

        // execute
        Exception exception = assertThrows(HttpServerErrorException.class,
                () -> contributionFilesReportService.getFiles(date, date));

        String expectedMessage = "500 Received error 500";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // TODO (DCES-55): Debug and find a fix to mapping errors in CircleCI and then re-enable this test by uncommenting @Test tag
//    @Test
    void givenNotFoundServerError_whenGetFilesIsInvoked_thenWebClientResponseExceptionIsThrown() {
        // setup
        LocalDate date = LocalDate.of(4404, 4, 4);

        // execute
        Exception exception = assertThrows(WebClientResponseException.class,
                () -> contributionFilesReportService.getFiles(date, date)
        );

        String expectedMessage = "404 Not Found";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // TODO (DCES-55): Debug and find a fix to mapping errors in CircleCI and then re-enable this test by uncommenting @Test tag
//    @Test
    void givenServerError_whenGetFilesIsInvoked_thenMaatApiClientExceptionIsThrown() {
        // setup
        LocalDate date = LocalDate.of(4400, 4, 4);

        // execute
        Exception exception = assertThrows(MaatApiClientException.class,
                () -> contributionFilesReportService.getFiles(date, date)
        );

        String expectedMessage = "Received error 400";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
