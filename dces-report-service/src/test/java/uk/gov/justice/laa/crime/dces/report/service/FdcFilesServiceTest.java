package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.enums.ReportPeriod;
import uk.gov.justice.laa.crime.dces.report.enums.ReportType;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class FdcFilesServiceTest {

    @Autowired
    private FdcFilesService testService;

    @InjectSoftAssertions
    private SoftAssertions softly;
  @Autowired
  private FdcFilesService fdcFilesService;

    @AfterEach
    void assertAll(){
        softly.assertAll();
    }

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
    void givenDateWithNoData_whenGetFilesIsInvoked_thenEmptyListIsReturned() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        List<String> resultFiles = testService.getFiles(testDate, testDate);

        softly.assertThat(resultFiles).isNotNull();
        softly.assertThat(resultFiles).isEmpty();
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

    @Test
    void givenEmptyFileList_whenProcessFilesIsInvoked_thenNoExceptionIsThrown() {
        // setup
        LocalDate testDate = LocalDate.now();
        List<String> testFiles = new ArrayList<>();

        assertDoesNotThrow(() -> fdcFilesService.processFiles(testFiles, "Test", testDate, testDate));
    }
}
