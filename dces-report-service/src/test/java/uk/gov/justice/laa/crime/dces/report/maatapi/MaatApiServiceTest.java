package uk.gov.justice.laa.crime.dces.report.maatapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.RetryConfiguration;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaatApiServiceTest {

    MaatApiService maatApiService;
    private static MockWebServer mockWebServer;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mock
    private MaatApiConfiguration configuration;
    @MockBean
    private RetryConfiguration retryConfiguration;
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepo;
    @MockBean
    private OAuth2AuthorizedClientRepository authorizationRepo;

    private final String MOCK_TRANSACTION_ID = "MockedTransactionId";
    private final Integer MOCK_REPORT_ID = 20230701;
    private static final String MOCK_ENDPOINT_URL_GET = "mock/api/get/";
    private static final String MOCK_RESPONSE_BODY_ERROR = "ERROR";


    @BeforeAll
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MaatApiClient maatApiClient = maatApiClientMockFactory(mockWebServer.getPort());
        maatApiService = new MaatApiService(
                maatApiClient.webClient(clientRegistrationRepo, authorizationRepo)
        );
    }

    @AfterAll
    void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    private MaatApiClient maatApiClientMockFactory(Integer port) {
        configuration = new MaatApiConfiguration();
        configuration.setBaseUrl(String.format("http://localhost:%s", port));
        configuration.setOAuthEnabled(false);
        return new MaatApiClient(configuration, retryConfiguration);
    }

    @Test
    void givenAnySuccessRequest_whenSendApiRequestViaGETIsInvoked_thenTheMethodShouldSuccess() throws JsonProcessingException {
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel();
        expectedResponse.setId(1);
        expectedResponse.setTotalFiles(1);
        setupValidGetResponse(expectedResponse);

        MaatApiResponseModel actualResponse = maatApiService.sendApiRequestViaGET(
                MaatApiResponseModel.class,
                MOCK_ENDPOINT_URL_GET,
                Map.of("LAA_TRANSACTION_ID", MOCK_TRANSACTION_ID),
                MOCK_REPORT_ID);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
        assertThat(actualResponse.getTotalFiles()).isEqualTo(expectedResponse.getTotalFiles());
    }

    @Test
    void givenAnyRequestWithNoData_whenSendApiRequestViaGETIsInvoked_thenTheMethodShouldError() {
        setupNotFoundGetResponse();
        MaatApiResponseModel actualResponse = maatApiService.sendApiRequestViaGET(
                MaatApiResponseModel.class,
                MOCK_ENDPOINT_URL_GET,
                Map.of("LAA_TRANSACTION_ID", MOCK_TRANSACTION_ID),
                MOCK_REPORT_ID);

        assertThat(actualResponse).isNull();
    }

    @Test
    public void givenAnInvalidResponse_whenSendApiRequestViaGETIsInvoked_thenAnAppropriateErrorShouldBeThrown() {
        setupExceptionGetResponse();
        assertThatThrownBy(
                () -> maatApiService.sendApiRequestViaGET(
                        MaatApiResponseModel.class,
                        MOCK_ENDPOINT_URL_GET,
                        Map.of("LAA_TRANSACTION_ID", MOCK_TRANSACTION_ID),
                        MOCK_REPORT_ID
                )
        ).isInstanceOf(MaatApiClientException.class).cause().isInstanceOf(WebClientResponseException.class);
    }

    private <T> void setupValidGetResponse(T returnBody) throws JsonProcessingException {
        String responseBody = OBJECT_MAPPER.writeValueAsString(returnBody);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseBody)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));
    }

    private void setupNotFoundGetResponse() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.NOT_FOUND.value())
            .setBody(MOCK_RESPONSE_BODY_ERROR)
            .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));
    }

    private void setupExceptionGetResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("Invalid Response"));
    }
}