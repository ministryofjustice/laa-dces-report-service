package uk.gov.justice.laa.crime.dces.report.maatapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.MaatApiConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaatApiClientFactoryTest {

    MaatApiClientFactory maatApiClientFactory;

    @MockBean
    WebClient maatApiWebClient;

    private static MockWebServer mockWebServer;

    @Mock
    private MaatApiConfiguration configuration;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepo;
    @MockBean
    private OAuth2AuthorizedClientRepository authorizationRepo;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeAll
    public void setup() {
        maatApiClientFactory = new MaatApiClientFactory();
    }

    @Test
    public void givenAnyParameters_whenMaatApiClientIsInvoked_thenTheCorrectClientShouldBeReturned() throws JsonProcessingException {
        MaatApiClient actualMaatApiClient = maatApiClientFactory.maatApiClient(maatApiWebClient, MaatApiClient.class);
        assertThat(actualMaatApiClient).isNotNull();
        assertThat(actualMaatApiClient).isInstanceOf(MaatApiClient.class);
    }



}
