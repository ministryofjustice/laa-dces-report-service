package uk.gov.justice.laa.crime.dces.report.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.enums.ReportType;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WireMockTest(httpPort = 1111)
class ContributionFilesServiceTest {
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate dayNumberTestDate = LocalDate.of(2020, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);

    @Autowired
    ContributionFilesService contributionFilesReportService;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeAll
    void setup() {
        Locale.setDefault(new Locale("en", "GB"));
    }

    @AfterEach
    void assertAll(){
        softly.assertAll();
    }

    @Test
    void givenValidDateLimitParams_whenGetFilesIsInvoked_thenResponseDataModelIsReturned()  throws WebClientResponseException {
        System.out.println(startPeriod);
        System.out.println(finishPeriod);
        List<String> result = contributionFilesReportService.getFiles(startPeriod, finishPeriod);
        softly.assertThat(assertSizeCorrect(result,(int)startPeriod.datesUntil(finishPeriod).count())).isTrue();
        softly.assertThat(result.get(0)).contains("id=\"222772044");
        softly.assertThat(result.get(1)).contains("id=\"222772045");
    }

    @Test
    void givenDaysRange_whenGetFilesIsInvoked_thenCorrectNumberOfDaysTraversed()  throws WebClientResponseException {
        softly.assertThat(testDateFunctionality(15)).isTrue();
        softly.assertThat(testDateFunctionality(21)).isTrue();
        softly.assertThat(testDateFunctionality(8)).isTrue();
        softly.assertThat(testDateFunctionality(0)).isTrue();
        softly.assertThat(testDateFunctionality(1)).isTrue();
    }


    private boolean assertSizeCorrect(List<String> resultList, int daysAdded){
        softly.assertThat(resultList).isNotNull();
        softly.assertThat(resultList.size()).isEqualTo(2*(daysAdded+1));
        return true;
    }

    private boolean testDateFunctionality( int numberOfDays){
        return assertSizeCorrect(contributionFilesReportService.getFiles(dayNumberTestDate, dayNumberTestDate.plusDays(numberOfDays)),numberOfDays);
    }

    @Test
    void givenInternalServerError_whenGetFilesIsInvoked_thenHttpServerErrorExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(5500, 5, 5);

        // execute
        String expectedMessage = "500 Received error 500";

        softly.assertThatThrownBy(() -> contributionFilesReportService.getFiles(date, date))
                .isInstanceOf(HttpServerErrorException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenNotFoundServerError_whenGetFilesIsInvoked_thenWebClientResponseExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(4404, 4, 4);

        String expectedMessage = "404 Not Found";

        softly.assertThatThrownBy(() -> contributionFilesReportService.getFiles(date, date))
                .isInstanceOf(WebClientResponseException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenServerError_whenGetFilesIsInvoked_thenMaatApiClientExceptionIsThrown(){
        // setup
        LocalDate date = LocalDate.of(4400, 4, 4);

        String expectedMessage = "Received error 400";

        softly.assertThatThrownBy(() -> contributionFilesReportService.getFiles(date, date))
                .isInstanceOf(MaatApiClientException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void givenDateWithNoData_whenGetFilesIsInvoked_thenEmptyListIsReturned() {
        // setup
        LocalDate testDate = LocalDate.of(2474, 10, 3);
        List<String> resultFiles = contributionFilesReportService.getFiles(testDate, testDate);

        softly.assertThat(resultFiles).isNotNull();
        softly.assertThat(resultFiles).isEmpty();
    }

    @Test
    void givenEmptyFileList_whenProcessFilesIsInvoked_thenNoExceptionIsThrown() {
        // setup
        LocalDate testDate = LocalDate.now();
        List<String> testFiles = new ArrayList<>();
        String expectedMessage = "NOT FOUND";

        assertDoesNotThrow(() -> contributionFilesReportService.processFiles(testFiles, "Test", testDate, testDate));
    }
}
