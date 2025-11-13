package uk.gov.justice.laa.crime.dces.report.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.dto.CaseSubmissionErrorDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionErrorRepository;

@RequiredArgsConstructor
@Service
public class CaseSubmissionErrorService {

  private final CaseSubmissionErrorRepository caseSubmissionErrorRepository;

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
}
