package uk.gov.justice.laa.crime.dces.report.service;

import static org.mockito.Mockito.when;

import io.sentry.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.config.TestConfig;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
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
        101,Contribution,201,100,200,21,2025-01-01T11:10:01,2,SyncRequestResponseToDrc,418,null
        101,Contribution,201,102,200,37,2025-01-02T11:10:01,2,SyncRequestResponseToDrc,418,null
        103,Contribution,203,100,200,27,2025-01-01T11:10:02,3,SyncResponseLoggedToMAAT,418,null
        103,Contribution,203,102,200,40,2025-01-02T11:10:02,3,SyncResponseLoggedToMAAT,418,null
        104,Contribution,204,102,200,42,2025-01-02T11:10:01,2,SyncRequestResponseToDrc,502,null
        105,Contribution,205,100,200,29,2025-01-01T11:10,1,FetchedFromMAAT,500,No parse XML! :(
        105,Contribution,206,102,200,43,2025-01-02T11:10,1,FetchedFromMAAT,500,null
        106,Contribution,200,100,200,31,2025-01-01T11:10:01,2,SyncRequestResponseToDrc,502,null
        106,Contribution,206,102,200,46,2025-01-02T11:10:02,3,SyncResponseLoggedToMAAT,502,null
        107,Contribution,207,100,200,32,2025-01-01T11:10,1,FetchedFromMAAT,200,null
        107,Contribution,207,102,200,47,2025-01-02T11:10,1,FetchedFromMAAT,200,null
        198,Fdc,198,97,200,6,2025-01-01T11:10,2,SyncRequestResponseToDrc,504,null
        198,Fdc,198,99,200,14,2025-01-02T11:10,2,SyncRequestResponseToDrc,504,null""");
  }

  @Test
  void givenTestDataWithNoRepeatFailuresAndEmptyReportsEnabled_whenGenerateReport_thenNoDataToReportOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(true);
    testDataUtil.createTestDataWithNoRepeatFailures();
    generateReportAndCheckOutput("### There is no data to report for the specified date range. ####");
  }

  @Test
  void givenTestDataWithNoRepeatFailuresAndEmptyReportsDisabled_whenGenerateReport_thenNullOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(false);
    testDataUtil.createTestDataWithNoRepeatFailures();
    generateReportAndCheckOutput(null);
  }

  private void generateReportAndCheckOutput(
      String expectedOutput
  ) throws IOException {
    String title = String.format("Test DCES DRC API Failures Report REPORTING DATE FROM: N/A | REPORTING DATE TO: %s | REPORTING PRODUCED ON: %s\n", LocalDate.now().minusDays(1), LocalDate.now());
    String header = "MAAT Id,Contribution Type,Contribution Id,Batch No,Trace Id,Case Submission Id,Processed Date,Event Type Id,Event Type Desc,HTTP Status,Payload\n";
    File failureReport = failuresReportService.generateReport("Test", LocalDate.now().minusDays(1));
    String csvOutput = FileUtils.readText(failureReport);
    if (expectedOutput == null) {
      softly.assertThat(csvOutput).isNull();
    } else {
      softly.assertThat(csvOutput).isEqualTo(title + header + expectedOutput);
    }
  }

}