package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.client.FdcFilesClient;
import uk.gov.justice.laa.crime.dces.report.maatapi.exception.MaatApiClientException;
import uk.gov.justice.laa.crime.dces.report.mapper.FdcFileMapper;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FdcFilesService implements MaatApiFilesService {
    private static final String SERVICE_NAME = "dcesReportFdc";

    private final FdcFilesClient fdcFilesClient;

    private FdcFileMapper fdcFileMapper;

    @Override
    @Retry(name = SERVICE_NAME)
    public ContributionFilesResponse getFiles(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, end);
        return fdcFilesClient.getContributions(start, end);
    }

    @Override
    public File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException {
        return null;
    }
}
