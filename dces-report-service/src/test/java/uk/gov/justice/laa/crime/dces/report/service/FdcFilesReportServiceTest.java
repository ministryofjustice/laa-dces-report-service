package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
public class FdcFilesReportServiceTest {

    @Autowired
    private FdcFilesReportService testService;

    @Test
    void canGetServiceInstance(){
        // setup
        LocalDate date = LocalDate.of(2023, 6, 10);

        // execute
        MaatApiResponseModel result = testService.getContributionFiles(date, date);

        // assert
        assertNotNull(result);
    }

    @Test
    void serviceThrowsExceptionWithInvalidDateRange(){
        // setup
        LocalDate startDate = LocalDate.of(2023, 6, 10);
        LocalDate endDate = LocalDate.of(2023, 6, 9);

        // execute
        Exception exception = assertThrows(RuntimeException.class, () -> {
            testService.getContributionFiles(startDate, endDate);
        });

        String expectedMessage = String.format("invalid time range {} is before {}", endDate, startDate);;
        String actualMessage = exception.getMessage();

        // asset
        assertTrue(actualMessage.contains(expectedMessage));

    }
}