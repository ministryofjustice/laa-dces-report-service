package uk.gov.justice.laa.crime.dces.report.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.dces.report.dto.CaseSubmissionErrorDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionErrorRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class CaseSubmissionErrorServiceTest {

  @Mock
  private CaseSubmissionErrorRepository caseSubmissionErrorRepository;

  @InjectMocks
  private CaseSubmissionErrorService caseSubmissionErrorService;

  @InjectSoftAssertions
  private SoftAssertions softly;

  private List<CaseSubmissionErrorEntity> entityList;
  private List<CaseSubmissionErrorDto> dtoList;

  @BeforeEach
  void setUp() {
    dtoList = List.of(
        CaseSubmissionErrorDto.builder().maatId(1).concorContributionId(1).fdcId(1).title("error title 1").status(1).detail("error detail 1").creationDate(LocalDateTime.of(2025, 1, 1, 11, 10, 0)).build(),
        CaseSubmissionErrorDto.builder().maatId(2).concorContributionId(2).fdcId(2).title("error title 2").status(2).detail("error detail 2").creationDate(LocalDateTime.of(2025, 1, 1, 11, 10, 0)).build(),
        CaseSubmissionErrorDto.builder().maatId(3).concorContributionId(3).fdcId(3).title("error title 3").status(3).detail("error detail 3").creationDate(LocalDateTime.of(2025, 3, 3, 11, 10, 0)).build(),
        CaseSubmissionErrorDto.builder().maatId(4).concorContributionId(4).fdcId(4).title("error title 4").status(4).detail("error detail 4").creationDate(LocalDateTime.of(2025, 4, 4, 11, 10, 0)).build(),
        CaseSubmissionErrorDto.builder().maatId(5).concorContributionId(5).fdcId(5).title("error title 5").status(5).detail("error detail 5").creationDate(LocalDateTime.of(2025, 5, 5, 11, 10, 0)).build()
    );

    entityList = List.of(
        new CaseSubmissionErrorEntity(1, 1, 1, 1, "error title 1", 1, "error detail 1", LocalDateTime.of(2025, 1, 1, 11, 10, 0)),
        new CaseSubmissionErrorEntity(2, 2, 2, 2, "error title 2", 2, "error detail 2", LocalDateTime.of(2025, 1, 1, 11, 10, 0)),
        new CaseSubmissionErrorEntity(3, 3, 3, 3, "error title 3", 3, "error detail 3", LocalDateTime.of(2025, 3, 3, 11, 10, 0)),
        new CaseSubmissionErrorEntity(4, 4, 4, 4, "error title 4", 4, "error detail 4", LocalDateTime.of(2025, 4, 4, 11, 10, 0)),
        new CaseSubmissionErrorEntity(5, 5, 5, 5, "error title 5", 5, "error detail 5", LocalDateTime.of(2025, 5, 5, 11, 10, 0))
    );
  }

  @Test
  public void givenValidEntityId_whenGetCaseSubmissionErrorEntity_thenReturnCorrectCaseSubmissionErrorDto() {
    when(caseSubmissionErrorRepository.findById(1)).thenReturn(Optional.of(entityList.getFirst()));

    CaseSubmissionErrorDto dto = caseSubmissionErrorService.getCaseSubmissionErrorEntity(1);

    softly.assertThat(dto.getId()).isEqualTo(1);
    softly.assertThat(dto.getMaatId()).isEqualTo(1);
    softly.assertThat(dto.getConcorContributionId()).isEqualTo(1);
    softly.assertThat(dto.getFdcId()).isEqualTo(1);
    softly.assertThat(dto.getTitle()).isEqualTo("error title 1");
    softly.assertThat(dto.getStatus()).isEqualTo(1);
    softly.assertThat(dto.getDetail()).isEqualTo("error detail 1");
    softly.assertThat(dto.getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));
  }

  @Test
  public void whenGetCaseSubmissionErrors_thenReturnAllCaseSubmissionErrorDtos() {
    when(caseSubmissionErrorRepository.findAll()).thenReturn(entityList);

    List<CaseSubmissionErrorDto> dtos = caseSubmissionErrorService.getCaseSubmissionErrors();

    softly.assertThat(dtos).hasSize(5);
    softly.assertThat(dtos.getFirst().getId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getMaatId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getConcorContributionId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getFdcId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getTitle()).isEqualTo("error title 1");
    softly.assertThat(dtos.getFirst().getStatus()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getDetail()).isEqualTo("error detail 1");
    softly.assertThat(dtos.getFirst().getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));

    softly.assertThat(dtos.get(2).getId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getMaatId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getConcorContributionId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getFdcId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getTitle()).isEqualTo("error title 3");
    softly.assertThat(dtos.get(2).getStatus()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getDetail()).isEqualTo("error detail 3");
    softly.assertThat(dtos.get(2).getCreationDate()).isEqualTo(LocalDateTime.of(2025, 3, 3, 11, 10, 0));
  }

  @Test
  public void givenCreationDate_whenGetCaseSubmissionErrors_thenReturnAllCaseSubmissionErrorDtosForGivenDate() {
    LocalDateTime creationDate = LocalDateTime.of(2025, 1, 1, 11, 10, 0);
    when(caseSubmissionErrorRepository.findByCreationDateBetween(any(), any())).thenReturn(entityList.subList(0, 2));

    List<CaseSubmissionErrorDto> dtos = caseSubmissionErrorService.getCaseSubmissionErrorsForDate(creationDate);

    softly.assertThat(dtos).hasSize(2);
    softly.assertThat(dtos.getFirst().getId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getMaatId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getConcorContributionId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getFdcId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getTitle()).isEqualTo("error title 1");
    softly.assertThat(dtos.getFirst().getStatus()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getDetail()).isEqualTo("error detail 1");
    softly.assertThat(dtos.getFirst().getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));

    softly.assertThat(dtos.get(1).getId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getMaatId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getConcorContributionId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getFdcId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getTitle()).isEqualTo("error title 2");
    softly.assertThat(dtos.get(1).getStatus()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getDetail()).isEqualTo("error detail 2");
    softly.assertThat(dtos.get(1).getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));
  }

  @Test
  public void givenCaseSubmissionErrorDto_whenSaveCaseSubmissionError_thenReturnCorrespondingSavedCaseSubmissionErrorDto() {
    when(caseSubmissionErrorRepository.save(any())).thenReturn(entityList.getLast());

    CaseSubmissionErrorDto dto = caseSubmissionErrorService.saveCaseSubmissionError(dtoList.getLast());

    softly.assertThat(dto.getId()).isEqualTo(5);
    softly.assertThat(dto.getMaatId()).isEqualTo(5);
    softly.assertThat(dto.getConcorContributionId()).isEqualTo(5);
    softly.assertThat(dto.getFdcId()).isEqualTo(5);
    softly.assertThat(dto.getTitle()).isEqualTo("error title 5");
    softly.assertThat(dto.getStatus()).isEqualTo(5);
    softly.assertThat(dto.getDetail()).isEqualTo("error detail 5");
    softly.assertThat(dto.getCreationDate()).isEqualTo(LocalDateTime.of(2025, 5, 5, 11, 10, 0));
  }

  @Test
  public void givenCaseSubmissionErrorDtos_whenSaveCaseSubmissionErrors_thenReturnCorrespondingSavedCaseSubmissionErrorDtos() {
    when(caseSubmissionErrorRepository.saveAll(anyIterable())).thenReturn(entityList);

    List<CaseSubmissionErrorDto> dtos = caseSubmissionErrorService.saveCaseSubmissionErrors(dtoList);

    softly.assertThat(dtos).hasSize(5);
    softly.assertThat(dtos.getFirst().getId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getMaatId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getConcorContributionId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getFdcId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getTitle()).isEqualTo("error title 1");
    softly.assertThat(dtos.getFirst().getStatus()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getDetail()).isEqualTo("error detail 1");
    softly.assertThat(dtos.getFirst().getCreationDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));

    softly.assertThat(dtos.get(4).getId()).isEqualTo(5);
    softly.assertThat(dtos.get(4).getMaatId()).isEqualTo(5);
    softly.assertThat(dtos.get(4).getConcorContributionId()).isEqualTo(5);
    softly.assertThat(dtos.get(4).getFdcId()).isEqualTo(5);
    softly.assertThat(dtos.get(4).getTitle()).isEqualTo("error title 5");
    softly.assertThat(dtos.get(4).getStatus()).isEqualTo(5);
    softly.assertThat(dtos.get(4).getDetail()).isEqualTo("error detail 5");
    softly.assertThat(dtos.get(4).getCreationDate()).isEqualTo(LocalDateTime.of(2025, 5, 5, 11, 10, 0));
  }

}
