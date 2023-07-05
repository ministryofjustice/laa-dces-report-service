package uk.gov.justice.laa.crime.dces.report.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;
import java.time.LocalDate;


public interface FdcFilesClient {

    @GetExchange(url = "/fdc/{startDate}/{endDate}")
    ContributionFilesResponse getFileList(
            @PathVariable @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate endDate
    );

    @Configuration
    class FdcFilesClientFactory {

        @Bean
        public FdcFilesClient getFdcFilesClient(WebClient maatApiWebClient) {
            return MaatApiClientFactory.maatApiClient(maatApiWebClient, FdcFilesClient.class);
        }
    }
}
