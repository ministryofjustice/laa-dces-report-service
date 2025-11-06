package uk.gov.justice.laa.crime.dces.report.service;

import java.time.LocalDateTime;
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
    
    if (optionalEntity.isPresent()) {
      CaseSubmissionErrorEntity entity = optionalEntity.get();
      return mapEntityToDto(entity);
    }
    
    return null;
  }

  public List<CaseSubmissionErrorDto> getCaseSubmissionErrors() {
    List<CaseSubmissionErrorEntity> entities = caseSubmissionErrorRepository.findAll();

    return entities.stream().map(this::mapEntityToDto).toList();
  }

  public List<CaseSubmissionErrorDto> getCaseSubmissionErrorsForDate(LocalDateTime date) {

    LocalDateTime startDate = date.toLocalDate().atStartOfDay();
    LocalDateTime endDate = date.toLocalDate().plusDays(1).atStartOfDay();

    List<CaseSubmissionErrorEntity> entities = caseSubmissionErrorRepository.findByCreationDateBetween(startDate, endDate);

    return entities.stream().map(this::mapEntityToDto).toList();
  }

  public CaseSubmissionErrorDto saveCaseSubmissionError(CaseSubmissionErrorDto dto) {
    CaseSubmissionErrorEntity entity = mapDtoToEntity(dto);

    CaseSubmissionErrorEntity returnedEntity = caseSubmissionErrorRepository.save(entity);

    return mapEntityToDto(returnedEntity);
  }

  public List<CaseSubmissionErrorDto> saveCaseSubmissionErrors(List<CaseSubmissionErrorDto> dtos) {
    List<CaseSubmissionErrorEntity> entities = new ArrayList<>();
    for (CaseSubmissionErrorDto dto : dtos) {
      CaseSubmissionErrorEntity entity = mapDtoToEntity(dto);
      entities.add(entity);
    }

    List<CaseSubmissionErrorEntity> returnedEntities = caseSubmissionErrorRepository.saveAll(entities);

    return returnedEntities.stream().map(this::mapEntityToDto).toList();
  }

  private CaseSubmissionErrorEntity mapDtoToEntity(CaseSubmissionErrorDto dto) {

    CaseSubmissionErrorEntity entity = new CaseSubmissionErrorEntity();
    entity.setMaatId(dto.getMaatId());
    entity.setConcorContributionId(dto.getConcorContributionId());
    entity.setFdcId(dto.getFdcId());
    entity.setTitle(dto.getTitle());
    entity.setStatus(dto.getStatus());
    entity.setDetail(dto.getDetail());
    entity.setCreationDate(dto.getCreationDate());

    return entity;
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
