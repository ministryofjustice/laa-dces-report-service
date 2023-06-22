package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class MaatApiClientFactory {

    @Bean
    MaatApiClient maatApiClient(WebClient maatApiWebClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(maatApiWebClient))
                        .build();
        return httpServiceProxyFactory.createClient(MaatApiClient.class);
    }
}
