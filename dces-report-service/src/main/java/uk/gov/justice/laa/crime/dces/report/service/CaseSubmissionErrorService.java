package uk.gov.justice.laa.crime.dces.report.service;

import java.time.Instant;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Service
public class CaseSubmissionErrorService {

  private static final String FILE_NAME_TEMPLATE = "DCES-DRC_case_submission_error_%s";

  private static final String REPORT_TYPE = "DCES-DRC Case Submission error";

  private final CSVFileService csvFileService;
  private final DrcProcessingStatusRepository drcProcessingStatusRepository;

  private final FeatureProperties feature;

  public List<DrcProcessingStatusDto> getCaseSubmissionErrorsForDate(Instant startTimestamp, Instant endTimestamp) {

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
    Instant startTimestamp = reportDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant endTimestamp = reportDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
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
}
