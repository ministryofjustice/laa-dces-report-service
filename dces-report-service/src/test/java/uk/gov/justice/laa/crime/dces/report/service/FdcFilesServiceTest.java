package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class FdcFilesServiceTest {

    @Autowired
    private FdcFilesService testService;

    @Test
    void getsListOfContributionsXmlWithValidDateParams() throws WebClientResponseException {
        // setup
        LocalDate date = LocalDate.of(2023, 6, 10);

        // execute
    }

    @Test
    void serviceThrowsExceptionWithInvalidDateRange() {
        // setup
        LocalDate startDate = LocalDate.of(2023, 6, 10);
        LocalDate endDate = LocalDate.of(2023, 6, 9);

        // execute
        Exception exception = assertThrows(MaatApiClientException.class, () -> {
            testService.getFiles(startDate, endDate);
        });

        String expectedMessage = String.format("invalid time range %s is before %s", endDate, startDate);
        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
