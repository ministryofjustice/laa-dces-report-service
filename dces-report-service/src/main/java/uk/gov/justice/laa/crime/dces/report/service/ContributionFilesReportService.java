package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionFilesReportService {
    private static final String SERVICE_NAME = "dcesReportContributions";

    private final MaatApiContributionsEndpoint maatApiContributionsEndpoint;


    @Retry(name = SERVICE_NAME)
    public MaatApiResponseModel getContributionFiles(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        return maatApiContributionsEndpoint.sendGetRequest(start, finish);
    }
}
