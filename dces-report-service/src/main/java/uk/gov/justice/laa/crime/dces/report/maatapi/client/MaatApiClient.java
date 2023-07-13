package uk.gov.justice.laa.crime.dces.report.maatapi.client;

import org.springframework.web.service.annotation.GetExchange;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;


public interface MaatApiClient {

    @GetExchange("/get")
    ContributionFilesResponse getContributions();
}