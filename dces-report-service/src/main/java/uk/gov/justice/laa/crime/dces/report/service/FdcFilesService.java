package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class FdcFilesService implements DcesReportFileService {
    private static final String SERVICE_NAME = "dcesReportFdc";
    private static final String FILE_NAME_TEMPLATE = "FDC_%s_%s";

    private final FdcFilesClient fdcFilesClient;

    private final FdcFileMapper fdcFileMapper;

    @Retry(name = SERVICE_NAME)
    public List<String> getFiles(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, end);
        return fdcFilesClient.getContributions(start, end);
    }

    public File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException {
        return null;
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }
}
