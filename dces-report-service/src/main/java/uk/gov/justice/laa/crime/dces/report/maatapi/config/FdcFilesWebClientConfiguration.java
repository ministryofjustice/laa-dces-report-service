package uk.gov.justice.laa.crime.dces.report.maatapi.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.FdcFilesClient;

import java.time.Duration;
import java.util.UUID;

@Configuration
@AllArgsConstructor
@Slf4j
public class FdcFilesWebClientConfiguration {
    @Bean
    WebClient FdcFilesWebClient(ServicesConfiguration servicesConfiguration, ClientRegistrationRepository clientRegistrations,
                                OAuth2AuthorizedClientRepository authorizedClients) {
        ConnectionProvider provider =
                ConnectionProvider.builder("custom")
                        .maxConnections(500)
                        .maxIdleTime(Duration.ofSeconds(20))
                        .maxLifeTime(Duration.ofSeconds(60))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .evictInBackground(Duration.ofSeconds(120))
                        .build();

        WebClient.Builder clientBuilder = WebClient.builder()
                .baseUrl(servicesConfiguration.getEformApi().getBaseUrl())
                .defaultHeader("LAA-TRANSACTION-ID", UUID.randomUUID().toString())
                .clientConnector(new ReactorClientHttpConnector(
                                HttpClient.create(provider)
                                        .resolver(DefaultAddressResolverGroup.INSTANCE)
                                        .compress(true)
                                        .responseTimeout(Duration.ofSeconds(30))
                        )
                );
        if (servicesConfiguration.getEformApi().isOAuthEnabled()) {
            ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                            clientRegistrations, authorizedClients
                    );
            oauth.setDefaultClientRegistrationId(servicesConfiguration.getEformApi().getRegistrationId());
            clientBuilder.filter(oauth);
        };
                return clientBuilder.build();
    }

    @Bean
    FdcFilesClient fdcFilesClient(@Qualifier("FdcFilesWebClient") WebClient client) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(client)).build();

        return httpServiceProxyFactory.createClient(FdcFilesClient.class);
    }
}
