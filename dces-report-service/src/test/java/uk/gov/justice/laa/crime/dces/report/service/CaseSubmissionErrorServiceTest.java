package uk.gov.justice.laa.crime.dces.report.service;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.config.TestConfig;
import uk.gov.justice.laa.crime.dces.report.dto.CaseSubmissionErrorDto;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class CaseSubmissionErrorServiceTest {

  @Autowired
  private CaseSubmissionErrorService caseSubmissionErrorService;

  @InjectSoftAssertions
  private SoftAssertions softly;

  @Autowired
  private TestDataUtil testDataUtil;

  @Test
  public void givenCreationDate_whenGetCaseSubmissionErrors_thenReturnAllCaseSubmissionErrorDtosForGivenDate() {
    testDataUtil.createTestCaseSubmissionErrorData();
    LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 1, 2, 0, 0, 0);

    List<CaseSubmissionErrorDto> dtos = caseSubmissionErrorService.getCaseSubmissionErrorsForDate(startDate, endDate);

    softly.assertThat(dtos).hasSize(3);
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

}
