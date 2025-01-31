package uk.gov.justice.laa.crime.dces.report.service;

import static org.mockito.Mockito.when;

import io.sentry.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionRepository;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class FailuresReportServiceTest {

  @InjectSoftAssertions
  private SoftAssertions softly;

  @Autowired
  private FailuresReportService failuresReportService;

  @Autowired
  private TestDataUtil testDataUtil;

  @MockBean
  private FeatureProperties feature;

  @Test
  void givenTestDataWithFailures_whenGenerateReport_thenOutputIsAsExpected() throws IOException {
    testDataUtil.createTestDataWithFailures();
    generateReportAndCheckOutput(
        """
            300,Contribution,400,false,1,2025-01-01T11:10,2025-01-01T11:10,false,0,null,null
            301,Contribution,401,false,2,2025-01-01T11:10,2025-01-02T11:10,false,0,null,null
            303,Contribution,403,true,1,2025-01-01T11:10,2025-01-01T11:10,false,2,2025-01-03T11:10,2025-01-04T11:10
            303,Fdc,500,false,0,null,null,false,0,null,null""");
  }

  @Test
  void givenTestDataWithNoFailuresAndEmptyReportsEnabled_whenGenerateReport_thenNoDataToReportOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(true);
    testDataUtil.createTestDataWithNoFailures();
    generateReportAndCheckOutput("### There is no data to report for the specified date range. ####");
  }

  @Test
  void givenTestDataWithNoFailuresAndEmptyReportsDisabled_whenGenerateReport_thenNullOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(false);
    testDataUtil.createTestDataWithNoFailures();
    generateReportAndCheckOutput(null);
  }

  @Test
  void givenTestDataWithMultipleContributionsPerMaatId_whenGenerateReport_thenExpectedOutput() throws IOException {
    testDataUtil.createTestDataWithMultipleContributionsPerMaatId();
    generateReportAndCheckOutput("300,Contribution,401,false,2,2025-01-01T11:10,2025-01-04T11:10,false,0,null,null");
  }

  private void generateReportAndCheckOutput(
      String expectedOutput
  ) throws IOException {
    String title = String.format("Test DCES DRC API Failures Report REPORTING DATE FROM: N/A | REPORTING DATE TO: %s | REPORTING PRODUCED ON: %s\n", LocalDate.now().minusDays(1), LocalDate.now());
    String header = "MAAT Id,ContributionType,ContributionId,sentToDrc,drcSendAttempts,firstDrcAttemptDate,lastDrcAttemptDate,updatedInMaat,maatUpdateAttempts,firstMaatAttemptDate,lastMaatAttemptDate\n";
    File failureReport = failuresReportService.generateReport("Test", LocalDate.now().minusDays(1));
    String csvOutput = FileUtils.readText(failureReport);
    if (expectedOutput == null) {
      softly.assertThat(csvOutput).isNull();
    } else {
      softly.assertThat(csvOutput).isEqualTo(title + header + expectedOutput);
    }
  }

}