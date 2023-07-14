package uk.gov.justice.laa.crime.dces.report.service;

import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public interface DcesReportService {

    enum ReportFileType {
        FDC, // final-defence-cost
        CONTRIBUTION
    }

    ContributionFilesResponse getApiFiles(ReportFileType type, LocalDate start, LocalDate end);

    File processFiles(ReportFileType type, List<String> files);
}
