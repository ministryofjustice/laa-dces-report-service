package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.FdcFilesClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.mapper.FdcFileMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


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
        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, end);
        return fdcFilesClient.getContributions(start, end);
    }

    @Timed("laa_dces_report_service_fdc_process_file")
    public File processFiles(List<String> files, LocalDate start, LocalDate finish) throws JAXBException, IOException {
        return fdcFileMapper.processRequest(files.toArray(new String[0]), getFileName(start, finish));
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }

    @Override
    public String getType() {
        return REPORT_TYPE;
    }
}
