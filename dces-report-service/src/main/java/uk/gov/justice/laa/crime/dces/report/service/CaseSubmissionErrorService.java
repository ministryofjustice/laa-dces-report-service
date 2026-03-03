package uk.gov.justice.laa.crime.dces.report.service;

import java.time.Instant;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.dto.DrcProcessingStatusDto;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.model.DrcProcessingStatusEntity;
import uk.gov.justice.laa.crime.dces.report.repository.DrcProcessingStatusRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class CaseSubmissionErrorService {

  private static final String FILE_NAME_TEMPLATE = "DCES-DRC_case_submission_error_%s";

  private static final String REPORT_TYPE = "DCES-DRC Case Submission error";

  private final CSVFileService csvFileService;
  private final DrcProcessingStatusRepository drcProcessingStatusRepository;

  private final FeatureProperties feature;

  private final int reportCutoffHour;

  public CaseSubmissionErrorService(CSVFileService csvFileService,
      DrcProcessingStatusRepository drcProcessingStatusRepository, FeatureProperties feature,
      @Value("${reports.caseSubmissionError.reportCutoffHour}")
      int reportCutoffHour) {
    this.csvFileService = csvFileService;
    this.drcProcessingStatusRepository = drcProcessingStatusRepository;
    this.feature = feature;
    this.reportCutoffHour = reportCutoffHour;
  }

  public List<DrcProcessingStatusDto> getCaseSubmissionErrorsForDate(Instant startTimestamp, Instant endTimestamp) {

    log.info("Finding case submission errors for date range: {} to {}", startTimestamp, endTimestamp);
    List<DrcProcessingStatusEntity> entities = drcProcessingStatusRepository.findErrorsCreatedWithinRange(startTimestamp, endTimestamp);

    return entities.stream().map(this::mapEntityToDto).toList();
  }

  private DrcProcessingStatusDto mapEntityToDto(DrcProcessingStatusEntity entity) {

    return DrcProcessingStatusDto.builder()
        .id(entity.getId())
        .maatId(entity.getMaatId())
        .concorContributionId(entity.getConcorContributionId())
        .fdcId(entity.getFdcId())
        .statusMessage(entity.getStatusMessage())
        .drcProcessingTimestamp(entity.getDrcProcessingTimestamp())
        .creationTimestamp(entity.getCreationTimestamp())
        .build();
  }

  public FailureReportDto generateReport(LocalDate reportDate) throws IOException {
    // Convert the reportDate into start and end timestamps
    // records will be selected where created >= startTimestamp and < endTimestamp
    Instant startTimestamp = getStartTimestamp(reportDate);
    Instant endTimestamp = getEndTimestamp(reportDate);
    List<DrcProcessingStatusDto> caseSubmissionErrors = getCaseSubmissionErrorsForDate(startTimestamp, endTimestamp);

    if (caseSubmissionErrors.isEmpty() && !feature.sendEmptyFailuresReport()) {
      log.info("No case submission errors found and feature flag to send empty reports is absent/set to false, so not generating the report");
      return null;
    } else {
      log.info("{} case submission errors found, generating the report", caseSubmissionErrors.size());
      return csvFileService.writeCaseSubmissionErrorsToCsv(caseSubmissionErrors, String.format(FILE_NAME_TEMPLATE, LocalDate.now()));
    }
  }

  public String getType() {
    return REPORT_TYPE;
  }

  private Instant getStartTimestamp(LocalDate reportDate) {
    // Subtract 1 day, reset time to 00:00, then add reportCutoffHour number of hours
    return reportDate.minusDays(1).atStartOfDay(ZoneOffset.UTC).plusHours(reportCutoffHour).toInstant();
  }

  private Instant getEndTimestamp(LocalDate reportDate) {
    // Reset time to 00:00, then add reportCutoffHour number of hours
    return reportDate.atStartOfDay(ZoneOffset.UTC).plusHours(reportCutoffHour).toInstant();
  }
}
