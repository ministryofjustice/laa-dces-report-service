package uk.gov.justice.laa.crime.dces.report.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

public interface FdcFilesClient {

    @GetExchange(url = "/fdc/{startDate}/{endDate}")
    ContributionFilesResponse getFileList(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate);
}
