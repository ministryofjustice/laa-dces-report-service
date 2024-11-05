package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.MaatApiClient;

public class MaatApiClientFactory {

    private MaatApiClientFactory(){
        throw new UnsupportedOperationException("Utility Class");
    }

    @Bean
    public static <T extends MaatApiClient> T maatApiClient(WebClient maatApiWebClient, Class<T> returnClass) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(maatApiWebClient))
                        .build();
        return httpServiceProxyFactory.createClient(returnClass);
    }
}
