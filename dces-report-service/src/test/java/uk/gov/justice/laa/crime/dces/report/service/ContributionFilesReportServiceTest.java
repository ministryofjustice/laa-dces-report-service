package uk.gov.justice.laa.crime.dces.report.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContributionFilesReportServiceTest {

    private static final int DEFAULT_ID = 1;
    private static final int DEFAULT_TOTAL = 0;
    private static final LocalDate startPeriod = LocalDate.parse("01-01-2023", DateTimeFormatter.ofPattern(ContributionFilesReportService.DATE_FORMAT));
    private static final LocalDate finishPeriod = LocalDate.parse("31-01-2023", DateTimeFormatter.ofPattern(ContributionFilesReportService.DATE_FORMAT));

    @Mock
    MaatApiContributionsEndpoint contributionsEndpoint;
    @InjectMocks
    ContributionFilesReportService contributionFilesReportService;


    @Test
    void givenValidDateLimitParams_whenEndpointSendGetRequestIsInvoked_thenResponseDataModelIsReturned() {
        MaatApiResponseModel expectedResponse = getMockedMaatApiResponseModel();

        when(contributionsEndpoint.sendGetRequest(any(), any()))
                .thenReturn(expectedResponse);

        Assertions.assertDoesNotThrow(mockSendRequestGetContributionFiles());
        verify(contributionsEndpoint, times(1)).sendGetRequest(startPeriod, finishPeriod);
    }

    @Test
    void givenValidDateLimitParams_whenGetContributionFilesIsInvoked_thenResponseDataModelIsReturned() {
        MaatApiResponseModel expectedResponse = getMockedMaatApiResponseModel();

        when(contributionFilesReportService.getContributionFiles(startPeriod, finishPeriod))
                .thenReturn(expectedResponse);

        MaatApiResponseModel actualResponse = contributionFilesReportService.getContributionFiles(startPeriod, finishPeriod);
        assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
        assertThat(actualResponse.getTotalFiles()).isEqualTo(expectedResponse.getTotalFiles());
    }

    @Test
    void givenValidDateLimitParams_whenGetContributionFilesIsInvoked_thenExceptionIsThrown() {
        when(contributionsEndpoint.sendGetRequest(any(), any()))
            .thenThrow(Mockito.mock(MaatApiClientException.class));

        assertThrows(MaatApiClientException.class, mockSendRequestGetContributionFiles());
    }

    private MaatApiResponseModel getMockedMaatApiResponseModel() {
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel();
        expectedResponse.setId(DEFAULT_ID);
        expectedResponse.setTotalFiles(DEFAULT_TOTAL);
        return expectedResponse;
    }

    private Executable mockSendRequestGetContributionFiles() {
        return new Executable() {
            @Override
            public void execute() throws Throwable {
                contributionsEndpoint.sendGetRequest(startPeriod, finishPeriod);
            }
        };
    }

    @Test
    void getContributionFiles() {
    }
}
