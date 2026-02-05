package uk.gov.justice.laa.crime.dces.report.service;

import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
import uk.gov.justice.laa.crime.dces.report.dto.DrcProcessingStatusDto;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

import java.time.LocalDateTime;
import java.util.List;

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

  @Autowired
  private CSVFileService csvFileService;

  private static final String CASE_SUBMISSION_ERROR_COLUMNS_HEADER = "MAAT Id,Concor Contribution Id,Fdc Id,Error Type,Processed Date" + System.lineSeparator();


  @Test
  public void givenCreationDate_whenGetCaseSubmissionErrors_thenReturnAllCaseSubmissionErrorDtosForGivenDate() {


    testDataUtil.createTestCaseSubmissionErrorData();
    Instant startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC);
    Instant endDate = LocalDateTime.of(2025, 1, 2, 0, 0, 0).toInstant(ZoneOffset.UTC);

    List<DrcProcessingStatusDto> dtos = caseSubmissionErrorService.getCaseSubmissionErrorsForDate(startDate, endDate);

    softly.assertThat(dtos).hasSize(3);
    softly.assertThat(dtos.getFirst().getMaatId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getConcorContributionId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getFdcId()).isEqualTo(1);
    softly.assertThat(dtos.getFirst().getStatusMessage()).isEqualTo("error title 1");
    softly.assertThat(dtos.getFirst().getDrcProcessingTimestamp()).isBefore(dtos.getFirst().getCreationTimestamp());
    softly.assertThat(dtos.getFirst().getCreationTimestamp()).isEqualTo(TestDataUtil.toInstant(2025, 1, 1, 11, 10, 0, ZoneOffset.UTC));

    softly.assertThat(dtos.get(1).getMaatId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getConcorContributionId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getFdcId()).isEqualTo(2);
    softly.assertThat(dtos.get(1).getStatusMessage()).isEqualTo("error title 2");
    softly.assertThat(dtos.get(1).getDrcProcessingTimestamp()).isBefore(dtos.get(1).getCreationTimestamp());
    softly.assertThat(dtos.get(1).getCreationTimestamp()).isEqualTo(TestDataUtil.toInstant(2025, 1, 1, 11, 10, 0, ZoneOffset.UTC));

    softly.assertThat(dtos.get(2).getMaatId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getConcorContributionId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getFdcId()).isEqualTo(3);
    softly.assertThat(dtos.get(2).getStatusMessage()).isEqualTo("error title 3");
    softly.assertThat(dtos.get(2).getDrcProcessingTimestamp()).isBefore(dtos.get(2).getCreationTimestamp());
    softly.assertThat(dtos.get(2).getCreationTimestamp()).isEqualTo(TestDataUtil.toInstant(2025, 1, 1, 23, 59, 59, ZoneOffset.UTC).plusMillis(999));

    softly.assertAll();
  }


  @Test
  void givenDrcProcessingStatusData_whenGenerateReportIsInvoked_shouldGenerateReportSuccessfully() {

    Instant createdTimestamp = TestDataUtil.toInstant(2025, 1, 1, 11, 10, 5, ZoneOffset.UTC).plusMillis(123);
    LocalDate reportDate = LocalDate.of(2025, 1, 1);

    String expectedData = CASE_SUBMISSION_ERROR_COLUMNS_HEADER + "1234,1,1,Invalid Outcome,2025-01-01T11:10:05Z";

    testDataUtil.createDrcProcessingStatusData(createdTimestamp);
    try {
      FailureReportDto failureReport = caseSubmissionErrorService.generateReport(reportDate);
      String output = Files.readString(failureReport.getReportFile().toPath());
      softly.assertThat(output).contains(expectedData);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void givenAEmptyCaseSubmissionError_whenWriteCaseSubmissionErrorToCsvIsInvoked_shouldGenerateFileWithHeader() {

    Instant createdTimestamp = LocalDateTime.now().minusDays(5).toInstant(ZoneOffset.UTC);
    testDataUtil.createDrcProcessingStatusData(createdTimestamp);

    try {
      FailureReportDto failureReportDto = caseSubmissionErrorService.generateReport(LocalDate.now().minusDays(1));
      softly.assertThat(failureReportDto).isNull();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
