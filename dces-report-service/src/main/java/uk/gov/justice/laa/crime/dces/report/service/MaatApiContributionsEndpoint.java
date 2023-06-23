package uk.gov.justice.laa.crime.dces.report.service;

import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.util.Date;

@HttpExchange()
public interface MaatApiContributionsEndpoint extends MaatApiClient {

    @GetExchange("/getContributions/{startDate}/{finishDate}")
    MaatApiResponseModel sendGetRequest(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date finishDate);

    @Component
    class MaatApiContributionsEndpointFactory {
        WebClient maatApiWebClient;

        @Bean
        public MaatApiContributionsEndpoint getMaatApiContributionsEndpoint() {
            return MaatApiClientFactory.maatApiClient(maatApiWebClient, MaatApiContributionsEndpoint.class);
        }
    }
}
