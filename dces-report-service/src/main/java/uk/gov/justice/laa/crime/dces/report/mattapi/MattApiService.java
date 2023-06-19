package uk.gov.justice.laa.crime.dces.report.mattapi;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MattApiService {
    // TODO (add support for Authorization)
    @Qualifier("maatApiAuthorizationClient")
    private final WebClient mattApiWebClient;

    public <T> T getApiResponseViaGET(Class<T> responseClass, String url, Map<String, String> headers, Object... urlVariables) {
        return mattApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorMap(this::handleClientError)
                .doOnError(Sentry::captureException)
                .block();
    }

    private Throwable handleClientError(Throwable error) {
        if (error instanceof MaatApiClientException) {
            return error;
        }
        return new MaatApiClientException("Call to Court Data API failed, invalid response.", error);
    }

    // TODO (): Add support for POST requests
//    public <T, R> R getApiResponseViaPOST(T requestBody, Class<R> responseClass, String url, Map<String, String> headers) {
//        return getApiResponse(requestBody, responseClass, url, headers, HttpMethod.POST);
//    }

    // TODO (): Add support for PUT requests
//    public <T, R> R getApiResponseViaPUT(T requestBody, Class<R> responseClass, String url, Map<String, String> headers) {
//        return getApiResponse(requestBody, responseClass, url, headers, HttpMethod.PUT);
//    }

    // TODO (): Add support for POST requests
//    <T, R> R getApiResponse(T requestBody,
//                            Class<R> responseClass,
//                            String url, Map<String, String> headers,
//                            HttpMethod requestMethod) {
//
//        return webClient
//                .method(requestMethod)
//                .uri(url)
//                .headers(httpHeaders -> httpHeaders.setAll(headers))
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(requestBody))
//                .retrieve()
//                .bodyToMono(responseClass)
//                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
//                .onErrorMap(this::handleError)
//                .doOnError(Sentry::captureException)
//                .block();
//    }
}