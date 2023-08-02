package uk.gov.justice.laa.crime.dces.report.service;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
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

    @Timed("FDC.getFiles")
    @Retry(name = SERVICE_NAME)
    public List<String> getFiles(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            String message = String.format("invalid time range %s is before %s", end, start);
            throw new MaatApiClientException(message);
        }
        log.info("Start - call MAAT API to collect FDC files, between {} and {}", start, end);
//        return fdcFilesClient.getContributions(start, end);
        return List.of("<?xml version=\"1.0\"?><fdc_file>    <header file_id=\"222637370\">        <filename>FDC_201807251354.xml</filename>        <dateGenerated>2018-07-25</dateGenerated>        <recordCount>6260</recordCount>    </header>    <fdc_list>        <fdc id=\"27783002\">            <maat_id>2525925</maat_id>            <sentenceDate>2016-09-30</sentenceDate>            <calculationDate>2016-12-22</calculationDate>            <final_cost>1774.4</final_cost>            <lgfs_total>1180.64</lgfs_total>            <agfs_total>593.76</agfs_total>        </fdc>    </fdc_list></fdc_file>");
    }
    @Timed("FDC.processFiles")
    public File processFiles(List<String> files, LocalDate start, LocalDate finish) throws JAXBException, IOException {
        return fdcFileMapper.processRequest(files.toArray(new String[0]), getFileName(start, finish));
    }

    public String getFileName(LocalDate start, LocalDate finish) {
        return String.format(FILE_NAME_TEMPLATE, start, finish);
    }
}
