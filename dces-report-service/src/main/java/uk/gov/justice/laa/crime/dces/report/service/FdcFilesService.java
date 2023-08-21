package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.FdcFilesClient;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.mapper.FdcFileMapper;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class FdcFilesService implements DcesReportFileService {

    private static final String REPORT_TYPE = "Final Defence Cost";

    private static final String SERVICE_NAME = "dcesReportFdc";
    private static final String FILE_NAME_TEMPLATE = "FDC_%s_%s";

    private final FdcFilesClient fdcFilesClient;

    private final FdcFileMapper fdcFileMapper;

    @Timed("laa_dces_report_service_fdc_get_file")
    @Retry(name = SERVICE_NAME)
    public List<String> getFiles(LocalDate start, LocalDate end) {
        log.info("Request Contribution XML files for report {} - {} ",
                start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }

        List<String> resultList = new ArrayList<>();
        resultList = fdcFilesClient.getContributions(start, end)
            .stream()
            .filter(Objects::nonNull)
            .toList()
        ;
        log.info("Received {} records with XML files for the period between {} and {}",
                resultList.size(), start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));
        return resultList;
    }

    @Timed("laa_dces_report_service_fdc_process_file")
    public File processFiles(List<String> files, LocalDate start, LocalDate finish)
            throws JAXBException, IOException, DcesReportSourceFilesDataNotFound {
        if (files.isEmpty()) {
            throw new DcesReportSourceFilesDataNotFound(
                    String.format("NOT FOUND: No FDC Files data between %s and %s", start, finish)
            );
        }

        File file = fdcFileMapper.processRequest(files.toArray(new String[0]), getFileName(start, finish));
        log.info("CSV file generated for FDC Report between {} and {}",
                start.format(DateUtils.dateFormatter), finish.format(DateUtils.dateFormatter));
        return file;
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }

    @Override
    public String getType() {
        return REPORT_TYPE;
    }
}
