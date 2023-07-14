package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.mapper.ContributionsFileMapper;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionFilesService implements MaatApiFilesService {
    private static final String SERVICE_NAME = "dcesReportContributions";

    private final ContributionFilesClient contributionFilesClientEndpoint;

    @Autowired
    private ContributionsFileMapper contributionsFileMapper;

    @Override
    @Retry(name = SERVICE_NAME)
    public ContributionFilesResponse getFiles(LocalDate start, LocalDate finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        return contributionFilesClientEndpoint.getContributions(start, finish);
    }

    @Override
    public File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException {
        return contributionsFileMapper.processRequest(files.toArray(new String[0]), start, finish, fileName);
    }
}
