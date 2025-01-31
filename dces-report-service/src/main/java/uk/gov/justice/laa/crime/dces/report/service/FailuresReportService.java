package uk.gov.justice.laa.crime.dces.report.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReport;
import uk.gov.justice.laa.crime.dces.report.repository.FailureReportRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailuresReportService {

    private static final String REPORT_TYPE = "DCES-DRC API Failures";
    private static final String FILE_NAME_TEMPLATE = "DCES_DRC_API_FAILURES_%s";

    private final FailureReportRepository failureReportRepository;
    private final CSVFileService csvFileService;
    private final FeatureProperties feature;

    public File generateReport(String reportTitle, LocalDate reportDate) throws IOException {
        List<FailureReport> failures = failureReportRepository.findFailures();
        if (failures.isEmpty() && !feature.sendEmptyFailuresReport()) {
            log.info("No failures found and feature flag to send empty reports is absent/set to false, so not generating the failure report");
            return null;
        } else {
            return csvFileService.writeFailuresToCsv(failures, getFileName(reportDate), reportTitle, reportDate);
        }
    }

    public String getFileName(LocalDate reportDate) {
        return String.format(FILE_NAME_TEMPLATE, reportDate);
    }

    public String getType() {
        return REPORT_TYPE;
    }

}
