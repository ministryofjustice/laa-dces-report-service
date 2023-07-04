package uk.gov.justice.laa.crime.dces.report.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.MaatApiClientFactory;


@RequiredArgsConstructor
@Component
public class ContributionFilesClientFactory {

    @Bean//("contributionFilesClient")
    public ContributionFilesClient getContributionFilesClient(WebClient maatApiWebClient) {
        return MaatApiClientFactory.maatApiClient(maatApiWebClient, ContributionFilesClient.class);
    }
}
