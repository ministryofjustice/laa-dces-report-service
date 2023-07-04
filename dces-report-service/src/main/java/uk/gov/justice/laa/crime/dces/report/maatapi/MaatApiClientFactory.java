package uk.gov.justice.laa.crime.dces.report.maatapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@RequiredArgsConstructor
public class MaatApiClientFactory {


    @Bean//("maatapiClient")
    public static <T> T maatApiClient( WebClient maatApiWebClient, Class<T> returnClass) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(maatApiWebClient))
                        .build();
        return httpServiceProxyFactory.createClient(returnClass);
    }
}
