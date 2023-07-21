package uk.gov.justice.laa.crime.dces.report.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.MaatApiClient;
import java.time.LocalDate;
import java.util.List;


@HttpExchange("/debt-collection-enforcement")
public interface ContributionFilesClient extends MaatApiClient {

    @GetExchange("/contributions?fromDate={startDate}&toDate={finishDate}")
    List<String> getContributions(
            @PathVariable @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) LocalDate finishDate);


    @Configuration
    class ContributionFilesClientFactory {

        @Bean
        public ContributionFilesClient getContributionFilesClient(WebClient maatApiWebClient) {
            return MaatApiClientFactory.maatApiClient(maatApiWebClient, ContributionFilesClient.class);
        }
    }
}
