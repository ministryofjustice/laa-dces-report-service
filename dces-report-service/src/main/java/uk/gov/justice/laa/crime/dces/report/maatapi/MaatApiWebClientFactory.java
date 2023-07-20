package uk.gov.justice.laa.crime.dces.report.maatapi;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.*;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.gov.justice.laa.crime.dces.report.maatapi.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Configuration
public class MaatApiWebClientFactory {
    private static final String LAA_TRANSACTION_ID = "LAA-TRANSACTION-ID";

    @Bean
    public WebClient maatApiWebClient(
            ServicesConfiguration servicesConfiguration,
            ClientRegistrationRepository clientRegistrations,  OAuth2AuthorizedClientRepository authorizedClients
    ) {

        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        WebClient.Builder clientBuilder = WebClient.builder()
            .baseUrl(servicesConfiguration.getMaatApi().getBaseUrl())
            .defaultHeader(LAA_TRANSACTION_ID, UUID.randomUUID().toString())
            .filter(errorResponse())
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create(provider)
                    .resolver(DefaultAddressResolverGroup.INSTANCE)
                    .compress(true)
                    .responseTimeout(Duration.ofSeconds(30))
                )
            )
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (servicesConfiguration.getMaatApi().isOAuthEnabled()) {
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
            oauth.setDefaultClientRegistrationId(servicesConfiguration.getMaatApi().getRegistrationId());

            clientBuilder.filter(oauth);
        }

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(
                convertMaxBufferSize(servicesConfiguration.getMaatApi().getMaxBufferSize())
                ))
            .build();
        clientBuilder.exchangeStrategies(strategies);

        return clientBuilder.build();
    }

    private ExchangeFilterFunction errorResponse() {
        return ExchangeFilterFunctions.statusError(
                HttpStatusCode::isError, clientResponse -> {
                    HttpStatus httpStatus =  HttpStatus.resolve(clientResponse.statusCode().value());
                    assert httpStatus != null;
                    String errorMessage = String.format("Received error %s due to %s",
                            clientResponse.statusCode(), httpStatus.getReasonPhrase()
                    );

                    if (httpStatus.is5xxServerError()) {
                        return new HttpServerErrorException(httpStatus, errorMessage);
                    }

                    if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
                        return WebClientResponseException.create(
                                httpStatus.value(), httpStatus.getReasonPhrase(),
                                null, null, null);
                    }

                    return new MaatApiClientException(errorMessage);
                }
        );
    }

    private static int convertMaxBufferSize(int megaBytes) {
        return megaBytes * 1024 * 1024;
    }
}