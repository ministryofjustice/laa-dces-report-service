package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FdcFilesReportService {
    private static final String SERVICE_NAME = "dcesReportFdc";

    private final MaatApiFdcEndpoint maatApiFdcEndpoint;

    @Retry(name = SERVICE_NAME)
    public MaatApiResponseModel getContributionFiles(
            @DateTimeFormat(pattern = MaatApiContributionsEndpoint.DATE_FORMAT) LocalDate start,
            @DateTimeFormat(pattern = MaatApiContributionsEndpoint.DATE_FORMAT) LocalDate finish) {
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, finish);
        return maatApiFdcEndpoint.sendGetRequest(start, finish);
    }
}
