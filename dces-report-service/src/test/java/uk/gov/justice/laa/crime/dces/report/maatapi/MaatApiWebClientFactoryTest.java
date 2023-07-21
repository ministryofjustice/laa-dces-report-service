package uk.gov.justice.laa.crime.dces.report.maatapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class MaatApiWebClientFactoryTest {

    MaatApiWebClientFactory maatApiWebClientFactory;
    private static MockWebServer mockWebServer;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Qualifier("servicesConfiguration")
    @Autowired
    private ServicesConfiguration configuration;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepo;
    @MockBean
    private OAuth2AuthorizedClientRepository authorizationRepo;


    @BeforeAll
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        configuration.getMaatApi().setBaseUrl(String.format("http://localhost:%s", mockWebServer.getPort()));

        maatApiWebClientFactory = new MaatApiWebClientFactory();
    }

    @AfterAll
    void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void givenAnyParameters_whenMaatApiWebClientIsInvoked_thenTheCorrectWebClientShouldBeReturned() throws JsonProcessingException {
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel();
        expectedResponse.setId(1);
        expectedResponse.setTotalFiles(1);
        setupValidResponse(expectedResponse);

        WebClient actualWebClient = maatApiWebClientFactory.maatApiWebClient(configuration,
                clientRegistrationRepo, authorizationRepo
        );

        assertThat(actualWebClient).isNotNull();
        assertThat(actualWebClient).isInstanceOf(WebClient.class);

        MaatApiResponseModel response = mockWebClientRequest(actualWebClient);
        assert response != null;
        assertThat(response).isInstanceOf(MaatApiResponseModel.class);
        assertThat(response.getId()).isEqualTo(expectedResponse.getId());
    }

    private <T> void setupValidResponse(T returnBody) throws JsonProcessingException {
        String responseBody = OBJECT_MAPPER.writeValueAsString(returnBody);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseBody)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));
    }

    private MaatApiResponseModel mockWebClientRequest(WebClient webClient) {
        return webClient
                .get()
                .uri(configuration.getMaatApi().getBaseUrl())
                .retrieve()
                .bodyToMono(MaatApiResponseModel.class)
                .block();
    }
}
