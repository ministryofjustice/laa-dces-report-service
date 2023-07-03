package uk.gov.justice.laa.crime.dces.report.maatapi.client;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesClient;

import java.time.LocalDate;

@HttpExchange
public interface FdcFilesClient {

    @GetExchange("/fdc/{startDate}/{endDate}")
    MaatApiResponseModel getFileList(
            @PathVariable @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate endDate
    );
}
