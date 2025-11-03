package uk.gov.justice.laa.crime.dces.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.dto.CaseSubmissionErrorDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionErrorRepository;

@RequiredArgsConstructor
@Service
public class CaseSubmissionErrorService {

  private final CaseSubmissionErrorRepository caseSubmissionErrorRepository;

  public CaseSubmissionErrorDto getCaseSubmissionErrorEntity(Integer caseSubmissionErrorId) {
    Optional<CaseSubmissionErrorEntity> optionalEntity = caseSubmissionErrorRepository.findById(caseSubmissionErrorId);
    CaseSubmissionErrorDto dto = null;
    
    if (optionalEntity.isPresent()) {
      CaseSubmissionErrorEntity entity = optionalEntity.get();
      dto = CaseSubmissionErrorDto.builder()
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
    
    return dto;
  }

  public List<CaseSubmissionErrorDto> getCaseSubmissionErrors() {
    List<CaseSubmissionErrorEntity> entities = caseSubmissionErrorRepository.findAll();
    List<CaseSubmissionErrorDto> dtos = new ArrayList<>();
    
    for (CaseSubmissionErrorEntity entity : entities) {
      dtos.add(
          CaseSubmissionErrorDto.builder()
              .id(entity.getId())
              .maatId(entity.getMaatId())
              .concorContributionId(entity.getConcorContributionId())
              .fdcId(entity.getFdcId())
              .title(entity.getTitle())
              .status(entity.getStatus())
              .detail(entity.getDetail())
              .creationDate(entity.getCreationDate())
              .build()
      );
    }
    
    return dtos;
  }
}
