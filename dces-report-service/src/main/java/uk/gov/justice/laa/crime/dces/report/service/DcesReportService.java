package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DcesReportService {

    enum ReportFileType {
        FDC, // final-defence-cost
        CONTRIBUTION
    }

    ContributionFilesResponse getApiFiles(ReportFileType type, LocalDate start, LocalDate end);

    File processFiles(ReportFileType type, List<String> files, LocalDate start, LocalDate end, String fileName) throws JAXBException, IOException;
}
