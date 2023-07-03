package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.maatapi.client.FdcFilesClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FdcFilesReportService {
    private static final String SERVICE_NAME = "dcesReportFdc";

    private final FdcFilesClient fdcFilesClient;

    @Retry(name = SERVICE_NAME)
    public MaatApiResponseModel getContributionFiles(
            @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate start,
            @DateTimeFormat(pattern = ContributionFilesClient.DATE_FORMAT) LocalDate end) {

        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, end);
        return fdcFilesClient.getFileList(start, end);
    }
}
