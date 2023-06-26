package uk.gov.justice.laa.crime.dces.report.service;

import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.time.LocalDate;

@HttpExchange()
public interface MaatApiContributionsEndpoint extends MaatApiClient {
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    @GetExchange("/getContributions/{startDate}/{finishDate}")
    MaatApiResponseModel sendGetRequest(
            @PathVariable @DateTimeFormat(pattern = DATE_FORMAT) LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = DATE_FORMAT) LocalDate finishDate);

    @Component
    class MaatApiContributionsEndpointFactory {
        WebClient maatApiWebClient;

        @Bean
        public MaatApiContributionsEndpoint getMaatApiContributionsEndpoint() {
            return MaatApiClientFactory.maatApiClient(maatApiWebClient, MaatApiContributionsEndpoint.class);
        }
    }
}
