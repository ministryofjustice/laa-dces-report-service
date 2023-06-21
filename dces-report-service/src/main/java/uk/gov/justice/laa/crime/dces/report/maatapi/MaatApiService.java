package uk.gov.justice.laa.crime.dces.report.maatapi;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatApiService {
    @Qualifier("maatApiAuthorizationClient")
    private final WebClient mattApiWebClient;

    public <T> T sendApiRequestViaGET(Class<T> responseClass, String url, Map<String, String> headers, Object... urlVariables) {
        return mattApiWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build(urlVariables))
                .headers(httpHeaders -> httpHeaders.setAll(headers))
                .retrieve()
                .bodyToMono(responseClass)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorMap(this::handleClientError)
                .doOnError(Sentry::captureException).block();
    }

    private Throwable handleClientError(Throwable error) {
        if (error instanceof MaatApiClientException) {
            return error;
        }
        return new MaatApiClientException("Call to MAAT API failed, invalid response.", error);
    }

    // TODO (): Consider adding support for POST and PUT requests
}