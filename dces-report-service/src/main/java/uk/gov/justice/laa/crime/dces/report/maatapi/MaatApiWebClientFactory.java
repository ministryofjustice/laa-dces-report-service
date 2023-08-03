package uk.gov.justice.laa.crime.dces.report.maatapi;

// TODO (DCES-77): Json web token package is only used for authentication
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
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
            OAuth2AuthorizedClientManager authorizedClientManager
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
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

            oauth2Client.setDefaultClientRegistrationId(
                    servicesConfiguration.getMaatApi().getRegistrationId()
            );

            // TODO (DCES-77): This code seemed to be for authenticating access to DCES report application. Need confirmation on this
//            clientBuilder.defaultHeader(
//                    AUTHORIZATION,
//                    generateJWTForOAuth2MattApi(
//                            "client-secret",
//                            "client-id")
//            );

            clientBuilder.filter(oauth2Client);
        }

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(
                convertMaxBufferSize(servicesConfiguration.getMaatApi().getMaxBufferSize())
                ))
            .build();
        clientBuilder.exchangeStrategies(strategies);

        return clientBuilder.build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .refreshToken()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService
        );

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
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

    // TODO (DCES-77): This code seems to be for authenticating access to DCES report application. Need confirmation on this
//    private static String generateJWTForOAuth2MattApi(String clientSecret, String issuer) {
//        return "Bearer " + Jwts.builder()
//                .setIssuer(issuer)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_LIFETIME_DURATION))
//                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(clientSecret))
//                        , SignatureAlgorithm.HS256
//                )
//                .compact();
//    }
}