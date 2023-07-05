package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionFilesReportService {
    private static final String SERVICE_NAME = "dcesReportContributions";

    private final ContributionFilesClient contributionFilesClientEndpoint;

    public static final String DATE_FORMAT = ContributionFilesClient.DATE_FORMAT;

    @Retry(name = SERVICE_NAME)
    public ContributionFilesResponse getContributionFiles(
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDate start,
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDate finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        return contributionFilesClientEndpoint.sendGetRequest(start, finish);
    }
}
