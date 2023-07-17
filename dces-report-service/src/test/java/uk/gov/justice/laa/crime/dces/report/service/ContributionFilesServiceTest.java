package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class ContributionFilesServiceTest {
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);

    @Autowired
    ContributionFilesReportService contributionFilesReportService;


    @Test
    void givenValidDateLimitParams_whenGetContributionFilesIsInvoked_thenResponseDataModelIsReturned()  throws WebClientResponseException {
        ContributionFilesResponse result = contributionFilesReportService.getContributionFiles(startPeriod, finishPeriod);

        assertNotNull(result);
        assertEquals(2, result.getFiles().size());
        assertTrue(result.getFiles().get(0).contains("id=\"222772044"));
        assertTrue(result.getFiles().get(1).contains("id=\"222772045"));
    }

    // TODO (DCES-40): enable stub mappings and reactivate test for 500 status error response
//    @Test
    void givenInternalServerError_whenGetContributionFilesIsInvoked_thenHttpServerErrorExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(5500, 5, 5);

        // execute
        Exception exception = assertThrows(HttpServerErrorException.class,
                () -> contributionFilesReportService.getContributionFiles(date, date));

        String expectedMessage = "500 Received error 500";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // TODO (DCES-40): enable stub mappings and reactivate test for 404 status error response
//    @Test
    void givenNotFoundServerError_whenGetContributionFilesIsInvoked_thenWebClientResponseExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(4404, 4, 4);

        // execute
        Exception exception = assertThrows(WebClientResponseException.class,
                () -> contributionFilesReportService.getContributionFiles(date, date)
        );

        String expectedMessage = "404 Not Found";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // TODO (DCES-40): enable stub mappings and reactivate test for 400 status error response
//    @Test
    void givenServerError_whenGetContributionFilesIsInvoked_thenMaatApiClientExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(4400, 4, 4);

        // execute
        Exception exception = assertThrows(MaatApiClientException.class,
                () -> contributionFilesReportService.getContributionFiles(date, date)
        );

        String expectedMessage = "Received error 400";
        String actualMessage = exception.getMessage();
        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
