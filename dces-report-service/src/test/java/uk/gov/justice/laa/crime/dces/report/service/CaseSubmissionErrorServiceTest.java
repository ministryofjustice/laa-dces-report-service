package uk.gov.justice.laa.crime.dces.report.service;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.config.TestConfig;
import uk.gov.justice.laa.crime.dces.report.dto.DrcProcessingStatusDto;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.repository.DrcProcessingStatusRepository;
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

  @Mock
  private CSVFileService csvFileService;
  @Autowired
  private DrcProcessingStatusRepository drcProcessingStatusRepository;

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
  void givenErrorRecordsMatchingTheReportDate_whenGenerateReportIsInvoked_shouldGenerateReportSuccessfully() {

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
  void givenNoErrorRecordsMatchTheReportDateAndSendEmptyFileFeatureFlagIsFalse_whenGenerateReportIsInvoked_shouldNotGenerateAReport()
      throws Exception {

    // Given - records that don't match the report date
    Instant createdTimestamp = LocalDateTime.now().minusDays(5).toInstant(ZoneOffset.UTC);
    testDataUtil.createDrcProcessingStatusData(createdTimestamp);
    // Given - feature flag to send an empty report is false
    FeatureProperties features = new FeatureProperties(false, false);

    // When the report is generated
    CaseSubmissionErrorService service = new CaseSubmissionErrorService(csvFileService, drcProcessingStatusRepository, features);
    FailureReportDto failureReportDto = service.generateReport(LocalDate.now().minusDays(1));

    // Then - no file is created
    softly.assertThat(failureReportDto).isNull();
    Mockito.verify(csvFileService, Mockito.never()).writeCaseSubmissionErrorsToCsv(Mockito.any(),  Mockito.any());
  }

  @Test
  void givenNoErrorRecordsMatchTheReportDateAndSendEmptyFileFeatureFlagIsTrue_whenGenerateReportIsInvoked_shouldGenerateAReport()
      throws Exception {

    // Given - records that don't match the report date
    Instant createdTimestamp = LocalDateTime.now().minusDays(5).toInstant(ZoneOffset.UTC);
    testDataUtil.createDrcProcessingStatusData(createdTimestamp);
    // Given - feature flag to send an empty report is false
    FeatureProperties features = new FeatureProperties(false, true);

    // Expect the CsvFileService to be called to write the file
    Mockito.when(csvFileService.writeCaseSubmissionErrorsToCsv(Mockito.any(),  Mockito.any()))
        .thenReturn(new FailureReportDto(new File("results.csv"), 1));

    // When the report is generated
    CaseSubmissionErrorService service = new CaseSubmissionErrorService(csvFileService, drcProcessingStatusRepository, features);
    service.generateReport(LocalDate.now().minusDays(1));

    // Then - verify the file would have been generated
    Mockito.verify(csvFileService).writeCaseSubmissionErrorsToCsv(Mockito.any(),  Mockito.any());
  }

}
