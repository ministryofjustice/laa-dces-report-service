package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.dto.CaseSubmissionErrorDto;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionErrorRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaseSubmissionErrorService {

  private static final String FILE_NAME_TEMPLATE = "DCES-DRC_case_submission_error_%s";

  private static final String REPORT_TYPE = "DCES-DRC Case Submission error";

  private static final String SUCCESS = "Success";

  private final CSVFileService csvFileService;
  private final CaseSubmissionErrorRepository caseSubmissionErrorRepository;

  private final FeatureProperties feature;

  public List<CaseSubmissionErrorDto> getCaseSubmissionErrorsForDate(LocalDateTime startDate, LocalDateTime endDate) {

    List<CaseSubmissionErrorEntity> entities = caseSubmissionErrorRepository.findByCreationDateBetween(startDate, endDate);

    return entities.stream().map(this::mapEntityToDto).toList();
  }

  private CaseSubmissionErrorDto mapEntityToDto(CaseSubmissionErrorEntity entity) {

    return CaseSubmissionErrorDto.builder()
        .id(entity.getId())
        .maatId(entity.getMaatId())
        .concorContributionId(entity.getConcorContributionId())
        .fdcId(entity.getFdcId())
        .title(entity.getTitle())
        .status(entity.getStatus())
        .detail(entity.getDetail())
        .creationDate(entity.getCreationDate())
        .build();
  }

  public FailureReportDto generateReport(LocalDateTime reportDate) throws IOException {

    List<CaseSubmissionErrorDto> caseSubmissionErrors = getCaseSubmissionErrorsForDate(reportDate, LocalDateTime.now());

    caseSubmissionErrors = Optional.ofNullable(caseSubmissionErrors)
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .filter(error -> StringUtils.isNotBlank(error.getTitle()))
            .filter(error -> !SUCCESS.equals(error.getTitle())).toList();


    if (caseSubmissionErrors.isEmpty() && !feature.sendEmptyFailuresReport()) {
      log.info("No case submission error found and feature flag to send empty reports is absent/set to false, so not generating the report");
      return null;
    } else {
      log.info("{} repeat case submission error and generating the case submission report", caseSubmissionErrors.size());
      return csvFileService.writeCaseSubmissionErrorToCsv(caseSubmissionErrors, String.format(FILE_NAME_TEMPLATE, LocalDate.now()));
    }
  }

  public String getType() {
    return REPORT_TYPE;
  }
}
