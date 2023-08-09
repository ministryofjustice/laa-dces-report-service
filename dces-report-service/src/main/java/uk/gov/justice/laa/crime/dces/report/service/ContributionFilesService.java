package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.mapper.ContributionsFileMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionFilesService implements DcesReportFileService {

    private static final String REPORT_TYPE = "Contributions";

    private static final String SERVICE_NAME = "dcesReportContributions";
    public static final String FILE_NAME_TEMPLATE = "Contributions_%s_%s";

    private final ContributionFilesClient contributionFilesClient;

    private final ContributionsFileMapper contributionFilesMapper;

    @Timed("laa_dces_report_service_contributions_get_file")
    @Retry(name = SERVICE_NAME)
    public List<String> getFiles(LocalDate start, LocalDate finish) {
        log.info("Start - call MAAT API to collect contribution files date between {} and {}", start.toString(), finish.toString());
        LocalDate currentDate = LocalDate.parse(start.toString());
        List<String> resultList = new ArrayList<>();
        while (!currentDate.isAfter(finish)){
            resultList.addAll(contributionFilesClient.getContributions(currentDate, currentDate));
            currentDate = currentDate.plusDays(1);
        }
        return resultList;
    }
    @Timed("laa_dces_report_service_contributions_process_file")
    public File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException {
        return contributionFilesMapper.processRequest(files.toArray(new String[0]), start, finish, fileName);
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }

    @Override
    public String getType() {
        return REPORT_TYPE;
    }
}
