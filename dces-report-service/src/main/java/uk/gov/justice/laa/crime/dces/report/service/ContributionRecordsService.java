package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionRecordsService implements MaatApiRecords {
    private static final String SERVICE_NAME = "dcesReportContributions";

    private final ContributionFilesClient contributionFilesClientEndpoint;

    @Override
    @Retry(name = SERVICE_NAME)
    public ContributionFilesResponse getFiles(LocalDate start, LocalDate finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        return contributionFilesClientEndpoint.getContributions(start, finish);
    }

    @Override
    public String processFiles(String xmlString) {
        return null;
    }
}
