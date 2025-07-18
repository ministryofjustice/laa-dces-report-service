package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.MaatApiClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class MaatApiClientFactoryTest {

    @MockitoBean
    WebClient maatApiWebClient;

    @Test
    void givenAnyParameters_whenMaatApiClientIsInvoked_thenTheCorrectClientShouldBeReturned() {
        MaatApiClient actualMaatApiClient = MaatApiClientFactory.maatApiClient(maatApiWebClient, MaatApiClient.class);
        assertThat(actualMaatApiClient).isNotNull();
        assertThat(actualMaatApiClient).isInstanceOf(MaatApiClient.class);
    }
}
