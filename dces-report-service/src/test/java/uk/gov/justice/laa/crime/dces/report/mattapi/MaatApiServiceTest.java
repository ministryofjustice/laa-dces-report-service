package uk.gov.justice.laa.crime.dces.report.mattapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@RunWith(MockitoJUnitRunner.class)
class MaatApiServiceTest {

//    @Mock
    MaatApiService maatApiService;

    @Mock
    MaatApiClient maatApiClient;

    @MockBean
    MaatApiConfiguration maatConfig;

    @MockBean
    RetryConfiguration retryConfig;

    @MockBean
    ClientRegistrationRepository registrationRepo;

    @MockBean
    OAuth2AuthorizedClientRepository clientRepo;

    @Mock
    private ExchangeFunction mockedRequestCall;

    private final String LAA_TRANSACTION_ID = "laaMockTransactionId";

    private final Integer REPORT_ID = 20230701;

    private static final String GET_MOCK_URL = "maatapi/mock/get/";

    private static final String GET_ERROR_RESPONSE_BODY = "ERROR";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeAll
    public void setupTests() {
        MockitoAnnotations.openMocks(this);
        WebClient maatApiWebClient = WebClient
                .builder()
                .baseUrl("http://localhost:1234")
                .filter(ExchangeFilterFunctions.statusError(
                                HttpStatusCode::is4xxClientError,
                                r -> {
                                    HttpStatus status = HttpStatus.valueOf(r.statusCode().value());

                                    return WebClientResponseException.create(
                                            status.value(),
                                            status.getReasonPhrase(),
                                            null,
                                            null,
                                            null
                                    );
                                }
                        )
                )
                .exchangeFunction(mockedRequestCall)
                .build();

        maatApiClient = Mockito.spy(new MaatApiClient(maatConfig, retryConfig));

        when(maatApiClient.webClient(
                any(ClientRegistrationRepository.class),
                any(OAuth2AuthorizedClientRepository.class)))
                .thenReturn(maatApiWebClient);

        maatApiService = new MaatApiService(maatApiClient.webClient(registrationRepo, clientRepo));
    }

//    @BeforeEach
    public void setupTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAnySuccessRequest_whenSendApiRequestViaGETIsInvoked_thenTheMethodShouldSuccess() throws JsonProcessingException {
        MaatApiResponseModel expectedResponse = new MaatApiResponseModel();
        expectedResponse.setId(1);
        expectedResponse.setTotalFiles(0);
        setupValidGetResponse(expectedResponse);

        MaatApiResponseModel actualResponse = maatApiService.sendApiRequestViaGET(
                MaatApiResponseModel.class,
                GET_MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                REPORT_ID);

        System.out.println("here");
        System.out.println(expectedResponse);
        System.out.println("there");
        System.out.println(actualResponse.toString());
        System.out.println("bye");
    }

    @Test
    void givenAnyRequestWithNoData_whenSendApiRequestViaGETIsInvoked_thenTheMethodShouldError() throws JsonProcessingException {
        setupInvalidGetResponse();
        MaatApiResponseModel actualResponse = maatApiService.sendApiRequestViaGET(
                MaatApiResponseModel.class,
                GET_MOCK_URL,
                Map.of("LAA_TRANSACTION_ID", LAA_TRANSACTION_ID),
                REPORT_ID);

        assertThat(actualResponse).isNull();
    }

    private void setupInvalidGetResponse() {
        when(mockedRequestCall.exchange(any()))
                .thenReturn(
                        Mono.just(ClientResponse
                                .create(HttpStatus.NOT_FOUND)
                                .body(GET_ERROR_RESPONSE_BODY)
                                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .build()
                        )
                );
    }

    private <T> void setupValidGetResponse(T returnBody) throws JsonProcessingException {
        String responseBody = OBJECT_MAPPER.writeValueAsString(returnBody);

        when(mockedRequestCall.exchange(any()))
                .thenReturn(
                        Mono.just(ClientResponse
                                .create(HttpStatus.OK)
                                .body(responseBody)
                                .build()
                        )
                );
    }
}
