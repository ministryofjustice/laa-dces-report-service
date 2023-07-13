package uk.gov.justice.laa.crime.dces.report.service;

import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

public interface DcesReportService {

    enum ReportFileType {
        FDC, // final-defence-cost
        CONTRIBUTION
    }

    ContributionFilesResponse getContributionsCollection(ReportFileType type, LocalDate start, LocalDate end);

}
