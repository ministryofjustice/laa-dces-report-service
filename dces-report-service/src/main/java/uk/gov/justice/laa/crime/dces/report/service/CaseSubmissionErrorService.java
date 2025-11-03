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

    List<CaseSubmissionErrorDto> returnDtos = new ArrayList<>();
    for (CaseSubmissionErrorEntity entity : returnedEntities) {
      CaseSubmissionErrorDto dto = mapEntityToDto(entity);
      returnDtos.add(dto);
    }

    return returnDtos;
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
