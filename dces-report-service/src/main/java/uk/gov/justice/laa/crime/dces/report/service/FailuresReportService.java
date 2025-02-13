package uk.gov.justice.laa.crime.dces.report.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailuresReportService {

    private static final String REPORT_TYPE = "DCES-DRC API Failures";
    private static final String FILE_NAME_TEMPLATE = "DCES_DRC_API_FAILURES_%s";

    private final CaseSubmissionRepository caseSubmissionRepository;
    private final CSVFileService csvFileService;
    private final FeatureProperties feature;

    public FailureReportDto generateReport(LocalDate reportDate) throws IOException {
        List<CaseSubmissionEntity> failures = new ArrayList<>(findFailures("Contribution"));
        failures.addAll(findFailures("Fdc"));
        if (failures.isEmpty() && !feature.sendEmptyFailuresReport()) {
            log.info("No failures found and feature flag to send empty reports is absent/set to false, so not generating the failure report");
            return null;
        } else {
            log.info("{} repeat failures found, generating the failure report", failures.size());
            return csvFileService.writeFailuresToCsv(failures, getFileName(reportDate));
        }
    }

    public String getFileName(LocalDate reportDate) {
        return String.format(FILE_NAME_TEMPLATE, reportDate);
    }

    public String getType() {
        return REPORT_TYPE;
    }

    /**
     * Finds repeat failures for the given record type
     * A repeat failure is a MAAT ID that has failed in the latest batch and the previous batch
     * @param recordType record type (Contribution or Fdc)
     * @return list of case submissions that are repeat failures
     */
    private List<CaseSubmissionEntity> findFailures(String recordType) {
        log.info("Finding repeat failures for record type: {}", recordType);
        int latestBatchId = caseSubmissionRepository.findTopByRecordTypeAndBatchIdIsNotNullOrderByBatchIdDesc(recordType).getBatchId();
        log.info("Latest batch ID for record type {} is: {}", recordType, latestBatchId);
        int previousBatchId = caseSubmissionRepository.findTopByRecordTypeAndBatchIdIsNotNullAndBatchIdLessThanOrderByBatchIdDesc(recordType, latestBatchId).getBatchId();
        log.info("Previous batch ID for record type {} is: {}", recordType, previousBatchId);
        List<CaseSubmissionEntity> caseSubmissionsForLatestBatch = caseSubmissionRepository.findByBatchIdAndMaatIdIsNotNull(latestBatchId);
        List<CaseSubmissionEntity> failuresInlatestBatch = extractFailures(caseSubmissionsForLatestBatch);
        log.info("Failures in latest batch for record type {}: {}", recordType, failuresInlatestBatch.size());
        List<CaseSubmissionEntity> caseSubmissionsForPreviousBatch = caseSubmissionRepository.findByBatchIdAndMaatIdIsNotNull(previousBatchId);
        List<CaseSubmissionEntity> failuresInPreviousBatch = extractFailures(caseSubmissionsForPreviousBatch);
        log.info("Failures in previous batch for record type {}: {}", recordType, failuresInPreviousBatch.size());

        List<CaseSubmissionEntity> repeatFailures = failuresInlatestBatch.stream()
            .flatMap(latest -> failuresInPreviousBatch.stream()
                .filter(previous -> previous.getMaatId().equals(latest.getMaatId()) &&
                    previous.getRecordType().equals(latest.getRecordType()))
                .map(previous -> List.of(latest, previous)))
            .flatMap(List::stream)
            .filter(submission -> submission.getHttpStatus() != 200||
            (submission.getEventType() == 1 &&
                failuresInlatestBatch.stream()
                    .noneMatch(other -> other.getMaatId().equals(submission.getMaatId()) &&
                        other.getEventType() != 1)))
            .distinct()
            .sorted(Comparator.comparingLong(CaseSubmissionEntity::getMaatId)
                .thenComparingLong(CaseSubmissionEntity::getId))
            .toList();

        log.info("Repeat failures for record type {}: {}", recordType, repeatFailures.size());
        return repeatFailures;
    }

    /**
     * Extracts the failures from the given list of case submissions
     * A failure is a MAAT ID that does not have a successful response for event type 3
     * @param caseSubmissionsForBatch list of case submissions for a batch
     * @return  list of case submissions that are failures
     */
    private static List<CaseSubmissionEntity> extractFailures(
        List<CaseSubmissionEntity> caseSubmissionsForBatch) {
        return caseSubmissionsForBatch.stream()
            .collect(Collectors.groupingBy(CaseSubmissionEntity::getMaatId))
            .entrySet().stream()
            .filter(entry -> entry.getValue().stream().noneMatch(
                submission -> submission.getEventType() == 3 && submission.getHttpStatus() == 200))
            .flatMap(entry -> entry.getValue().stream())
            .toList();
    }

}
