package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class FdcFilesServiceTest {

    @Autowired
    private FdcFilesService testService;

    @Test
    void getsListOfFdcXmlWithValidDateParams() throws WebClientResponseException {
        // setup
        LocalDate date = LocalDate.of(2023, 6, 10);

        // execute
        List<String> result = testService.getFiles(date, date);

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("id=\"27783002"));
        assertTrue(result.get(0).contains("2525925"));
        assertTrue(result.get(1).contains("5635978"));
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
