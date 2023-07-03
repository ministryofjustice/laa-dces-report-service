package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class FdcFilesReportServiceTest {

    @Autowired
    private FdcFilesReportService testService;

    @Test
    void canGetServiceInstance(){
        // setup
        LocalDate date = LocalDate.of(2023, 6, 10);

        // execute
        ContributionFilesResponse result = testService.getContributionFiles(date, date);

        // assert
        assertNotNull(result);
        assertEquals(2, result.getFiles().size());
        assertTrue(result.getFiles().get(0).contains("id=\"222772044"));
        assertTrue(result.getFiles().get(1).contains("id=\"222772045"));
    }

    @Test
    void serviceThrowsExceptionWithInvalidDateRange(){
        // setup
        LocalDate startDate = LocalDate.of(2023, 6, 10);
        LocalDate endDate = LocalDate.of(2023, 6, 9);

        // execute
        Exception exception = assertThrows(MaatApiClientException.class, () -> {
            testService.getContributionFiles(startDate, endDate);
        });

        String expectedMessage = String.format("invalid time range %s is before %s", endDate, startDate);;
        String actualMessage = exception.getMessage();

        // assert
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
