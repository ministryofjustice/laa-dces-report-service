package uk.gov.justice.laa.crime.dces.report.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ContributionFilesServiceTest {
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);

    @Mock
    ContributionFilesClient contributionFilesClient;
    @InjectMocks
    ContributionFilesService contributionFilesService;


    @Test
    void givenValidDateLimitParams_whenEndpointSendGetRequestIsInvoked_thenResponseDataModelIsReturned() {
        ContributionFilesResponse expectedResponse = getMockedMaatApiResponseModel();

        when(contributionFilesClient.getContributions(any(), any()))
                .thenReturn(expectedResponse);

        Assertions.assertDoesNotThrow(mockSendRequestGetContributionFiles());
        verify(contributionFilesClient, times(1)).getContributions(startPeriod, finishPeriod);
    }

    @Test
    void givenValidDateLimitParams_whenGetFilesIsInvoked_thenResponseDataModelIsReturned() {
        ContributionFilesResponse expectedResponse = getMockedMaatApiResponseModel();

        when(contributionFilesService.getFiles(startPeriod, finishPeriod))
                .thenReturn(expectedResponse);

        ContributionFilesResponse actualResponse = contributionFilesService.getFiles(startPeriod, finishPeriod);
        assertThat(actualResponse.getFiles().size()).isPositive();
        assertThat(actualResponse.getFiles()).hasSameClassAs(expectedResponse.getFiles());
        assertThat(actualResponse.getFiles().get(0)).isEqualTo(expectedResponse.getFiles().get(0));
    }

    @Test
    void givenValidDateLimitParams_whenGetFilesIsInvoked_thenExceptionIsThrown() {
        when(contributionFilesClient.getContributions(any(), any()))
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
                contributionFilesClient.getContributions(startPeriod, finishPeriod);
            }
        };
    }
}
