package uk.gov.justice.laa.crime.dces.report.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.MaatApiClient;

import java.time.LocalDate;
import java.util.List;


public interface FdcFilesClient extends MaatApiClient {

    @GetExchange(url = "/fdc/{startDate}/{endDate}")
    List<String> getContributions(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate);

    @Configuration
    class FdcFilesClientFactory {

        @Bean
        public FdcFilesClient getFdcFilesClient(WebClient maatApiWebClient) {
            return MaatApiClientFactory.maatApiClient(maatApiWebClient, FdcFilesClient.class);
        }
    }
}
