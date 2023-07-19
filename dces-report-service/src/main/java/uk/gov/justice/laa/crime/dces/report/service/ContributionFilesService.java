package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.mapper.ContributionsFileMapper;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionFilesService implements DcesReportFileService {
    private static final String SERVICE_NAME = "dcesReportContributions";
    private static final String FILE_NAME_TEMPLATE = "Contributions_%s_%s";

    private final ContributionFilesClient contributionFilesClient;

    private final ContributionsFileMapper contributionFilesMapper;

    @Retry(name = SERVICE_NAME)
    public ContributionFilesResponse getFiles(LocalDate start, LocalDate finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        return contributionFilesClient.getContributions(start, finish);
    }

    public File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException {
        return contributionFilesMapper.processRequest(files.toArray(new String[0]), start, finish, fileName);
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }

    // TODO (DCES-57): Remove override method once we are happy with test results, or move  this implementation to default if it happens to be more efficient
    @Override
    public boolean searchInFile(File file, String toSearchFor) throws IOException {
        boolean isFound = false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                isFound |= searchInLine(scanner.nextLine(), toSearchFor);
            }
        }

        return isFound;
    }

    // TODO (DCES-57): Remove override method once we are happy with test results, or move  this implementation to default if it happens to be more efficient
    @Override
    public boolean searchInLine(String line, String toSearchFor) {
        log.info("CSV line: [ {} ] ", line);
        return DcesReportFileService.super.searchInLine(line, toSearchFor);
    }

}
