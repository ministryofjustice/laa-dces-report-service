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
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContributionFilesClientReportServiceTest {

    private static final int DEFAULT_ID = 1;
    private static final int DEFAULT_TOTAL = 0;
    private static final LocalDate startPeriod = LocalDate.parse("01-01-2023", DateTimeFormatter.ofPattern(ContributionFilesReportService.DATE_FORMAT));
    private static final LocalDate finishPeriod = LocalDate.parse("31-01-2023", DateTimeFormatter.ofPattern(ContributionFilesReportService.DATE_FORMAT));

    @Mock
    ContributionFilesClient contributionsEndpoint;
    @InjectMocks
    ContributionFilesReportService contributionFilesReportService;


    @Test
    void givenValidDateLimitParams_whenEndpointSendGetRequestIsInvoked_thenResponseDataModelIsReturned() {
        ContributionFilesResponse expectedResponse = getMockedMaatApiResponseModel();

        when(contributionsEndpoint.sendGetRequest(any(), any()))
                .thenReturn(expectedResponse);

        Assertions.assertDoesNotThrow(mockSendRequestGetContributionFiles());
        verify(contributionsEndpoint, times(1)).sendGetRequest(startPeriod, finishPeriod);
    }

    @Test
    void givenValidDateLimitParams_whenGetContributionFilesIsInvoked_thenResponseDataModelIsReturned() {
        ContributionFilesResponse expectedResponse = getMockedMaatApiResponseModel();

        when(contributionFilesReportService.getContributionFiles(startPeriod, finishPeriod))
                .thenReturn(expectedResponse);

        ContributionFilesResponse actualResponse = contributionFilesReportService.getContributionFiles(startPeriod, finishPeriod);
        assertThat(actualResponse.getFiles().size()).isGreaterThan(0);
        assertThat(actualResponse.getFiles().size()).isEqualTo(expectedResponse.getFiles().size());
        assertThat(actualResponse.getFiles().get(0)).isEqualTo(expectedResponse.getFiles().get(0));
    }

    @Test
    void givenValidDateLimitParams_whenGetContributionFilesIsInvoked_thenExceptionIsThrown() {
        when(contributionsEndpoint.sendGetRequest(any(), any()))
            .thenThrow(Mockito.mock(MaatApiClientException.class));

        assertThrows(MaatApiClientException.class, mockSendRequestGetContributionFiles());
    }

    private ContributionFilesResponse getMockedMaatApiResponseModel() {
        return new ContributionFilesResponse(List.of("XML1", "XML2"));
    }

    private Executable mockSendRequestGetContributionFiles() {
        return new Executable() {
            @Override
            public void execute() throws Throwable {
                contributionsEndpoint.sendGetRequest(startPeriod, finishPeriod);
            }
        };
    }
}
