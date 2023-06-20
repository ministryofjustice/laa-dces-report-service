package uk.gov.justice.laa.crime.dces.report.mattapi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@RunWith (MockitoJUnitRunner.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaatApiClientTest {

    @Mock
    private MaatApiClient maatApiClient;

    @MockBean
    private ClientRegistrationRepository registrationRepository;

    @MockBean
    private OAuth2AuthorizedClientRepository authoriationRepository;

    @BeforeAll
    public void setup() {
    }

    @BeforeEach
    public void prepareTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void myTest() {
        assertThat(maatApiClient).isInstanceOf(MaatApiClient.class);
    }

    @Test
    public void givenAnyParameters_whenWebClientIsInvoked_thenTheCorrectClientShouldBeReturned() {
        when(maatApiClient.webClient(any(ClientRegistrationRepository.class), any(OAuth2AuthorizedClientRepository.class)))
            .thenReturn(
                WebClient
                    .builder()
                    .baseUrl("http://localhost:1234")
                    .build()
            );

        WebClient actualWebClient2 = maatApiClient.webClient(registrationRepository, authoriationRepository);
        assertThat(actualWebClient2).isInstanceOf(WebClient.class);

    }
}