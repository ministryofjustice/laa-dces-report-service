package uk.gov.justice.laa.crime.dces.report.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ContributionFilesClientTest {
    private static final LocalDate startPeriod = LocalDate.of(2023, 1, 1);
    private static final LocalDate finishPeriod = LocalDate.of(2023, 1, 31);

    @Mock
    ContributionFilesClient contributionFilesClient;


    @Test
    void givenValidDateLimitParams_whenContributionClientSendGetRequestIsInvoked_thenResponseDataModelIsReturned() {
        List<String> expectedResponse = getMockedMaatApiResponseModel();

        when(contributionFilesClient.getContributions(any(), any()))
                .thenReturn(expectedResponse);

        Assertions.assertDoesNotThrow(mockSendRequestGetContributionFiles());
        verify(contributionFilesClient, times(1)).getContributions(startPeriod, finishPeriod);
    }

    @Test
    void givenValidDateLimitParams_whenContributionClientSendGetRequestIsInvoked_thenExceptionIsThrown() {
        when(contributionFilesClient.getContributions(any(), any()))
                .thenThrow(Mockito.mock(MaatApiClientException.class));

        assertThrows(MaatApiClientException.class, mockSendRequestGetContributionFiles());
    }

    private List<String> getMockedMaatApiResponseModel() {
        return List.of("XML1", "XML2");
    }
    
    private Executable mockSendRequestGetContributionFiles() {
        return () -> contributionFilesClient.getContributions(startPeriod, finishPeriod);
    }
}
