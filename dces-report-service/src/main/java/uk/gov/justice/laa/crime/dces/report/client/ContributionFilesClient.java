package uk.gov.justice.laa.crime.dces.report.client;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.MaatApiClient;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;
import java.time.LocalDate;


public interface ContributionFilesClient extends MaatApiClient {
    String DATE_FORMAT = "dd-MM-yyyy";

    @GetExchange("/getContributions/{startDate}/{finishDate}")
    ContributionFilesResponse sendGetRequest(
            @PathVariable @DateTimeFormat(pattern = DATE_FORMAT) LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = DATE_FORMAT) LocalDate finishDate);
}
