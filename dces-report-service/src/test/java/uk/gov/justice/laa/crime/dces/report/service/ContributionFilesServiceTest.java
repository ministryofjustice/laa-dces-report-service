package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class ContributionFilesServiceTest {
//    @DateTimeFormat(pattern = MaatApiClient.DEFAULT_DATE_FORMAT)
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate dayNumberTestDate = LocalDate.of(2020, 1, 1);
//    @DateTimeFormat(pattern = MaatApiClient.DEFAULT_DATE_FORMAT)
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);

    @Autowired
    ContributionFilesService contributionFilesReportService;

    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @Test
    void givenValidDateLimitParams_whenGetFilesIsInvoked_thenResponseDataModelIsReturned()  throws WebClientResponseException {
        System.out.println(startPeriod);
        System.out.println(finishPeriod);
        List<String> result = contributionFilesReportService.getFiles(startPeriod, finishPeriod);
        assertNotNull(result);
        assertEquals((2*(startPeriod.datesUntil(finishPeriod).count()+1)), result.size()); // 2* as the file contains 2 contributions. +1 as datesuntil is exclusive.
        assertTrue(result.get(0).contains("id=\"222772044"));
        assertTrue(result.get(1).contains("id=\"222772045"));
    }

    @Test
    void given16DaysRange_whenGetFilesIsInvoked_thenCorrectNumberOfDaysTraversed()  throws WebClientResponseException {
        System.out.println(startPeriod);
        System.out.println(finishPeriod.minusDays(15));
        assertTrue(assertSizeCorrect(contributionFilesReportService.getFiles(dayNumberTestDate, dayNumberTestDate.plusDays(15)),15));
        assertTrue(assertSizeCorrect(contributionFilesReportService.getFiles(dayNumberTestDate, dayNumberTestDate.plusDays(8)),8));
        assertTrue(assertSizeCorrect(contributionFilesReportService.getFiles(dayNumberTestDate, dayNumberTestDate.plusDays(12)),12));

    }


    private boolean assertSizeCorrect(List<String> resultList, int daysAdded){
        assertNotNull(resultList);
        assertEquals((2*(daysAdded+1)), resultList.size()); // 2* as the file contains 2 contributions. 1->1+15 = 1->16
        return true;
    }

    @Test
    void givenInternalServerError_whenGetFilesIsInvoked_thenHttpServerErrorExceptionIsThrown(){
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

    @Test
    void givenNotFoundServerError_whenGetFilesIsInvoked_thenWebClientResponseExceptionIsThrown(){
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

    @Test
    void givenServerError_whenGetFilesIsInvoked_thenMaatApiClientExceptionIsThrown(){
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
