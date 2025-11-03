package uk.gov.justice.laa.crime.dces.report.service;

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

  @BeforeEach
  void setUp() {
    entityList = List.of(
        new CaseSubmissionErrorEntity(1, 1, 1, 1, "error title 1", 1, "error detail 1", LocalDateTime.of(2025, 1, 1, 11, 10, 0)),
        new CaseSubmissionErrorEntity(2, 2, 2, 2, "error title 2", 2, "error detail 2", LocalDateTime.of(2025, 2, 2, 11, 10, 0)),
        new CaseSubmissionErrorEntity(3, 3, 3, 3, "error title 3", 3, "error detail 3", LocalDateTime.of(2025, 3, 3, 11, 10, 0))
    );
  }

  @Test
  public void givenCaseSubmissionErrorEntities_whenGetCaseSubmissionErrorEntityWithEntityId_thenReturnCorrectCaseSubmissionErrorDto() {
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
  public void givenCaseSubmissionErrorEntities_whenGetCaseSubmissionErrors_thenReturnAllCaseSubmissionErrorDtos() {
    when(caseSubmissionErrorRepository.findAll()).thenReturn(entityList);

    List<CaseSubmissionErrorDto> dtos = caseSubmissionErrorService.getCaseSubmissionErrors();

    softly.assertThat(dtos).hasSize(3);
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

}
