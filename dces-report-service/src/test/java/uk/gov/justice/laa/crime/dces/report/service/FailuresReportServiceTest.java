package uk.gov.justice.laa.crime.dces.report.service;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.config.TestConfig;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
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

  @MockitoBean
  private FeatureProperties feature;

  @Test
  void givenTestDataWithFailures_whenGenerateReport_thenOutputIsAsExpected() throws IOException {
    testDataUtil.createTestDataWithFailures();
    generateReportAndCheckOutput(13,
        """
        101,Contribution,201,100,200,61,2025-01-01T11:10:01,2,SyncRequestResponseToDrc,418,,
        101,Contribution,201,102,200,77,2025-01-02T11:10:01,2,SyncRequestResponseToDrc,418,,
        103,Contribution,203,100,200,67,2025-01-01T11:10:02,3,SyncResponseLoggedToMAAT,418,,
        103,Contribution,203,102,200,80,2025-01-02T11:10:02,3,SyncResponseLoggedToMAAT,418,,
        104,Contribution,204,102,200,82,2025-01-02T11:10:01,2,SyncRequestResponseToDrc,502,,
        105,Contribution,205,100,200,69,2025-01-01T11:10,1,FetchedFromMAAT,500,No parse XML! :(,
        105,Contribution,206,102,200,83,2025-01-02T11:10,1,FetchedFromMAAT,500,,
        106,Contribution,200,100,200,71,2025-01-01T11:10:01,2,SyncRequestResponseToDrc,502,,
        106,Contribution,206,102,200,86,2025-01-02T11:10:02,3,SyncResponseLoggedToMAAT,502,,
        107,Contribution,207,100,200,72,2025-01-01T11:10,1,FetchedFromMAAT,200,,
        107,Contribution,207,102,200,87,2025-01-02T11:10,1,FetchedFromMAAT,200,,
        198,Fdc,198,97,200,46,2025-01-01T11:10,2,SyncRequestResponseToDrc,504,,
        198,Fdc,198,99,200,54,2025-01-02T11:10,2,SyncRequestResponseToDrc,504,,
        """);
  }

  @Test
  void givenTestDataWithNoRepeatFailuresAndEmptyReportsEnabled_whenGenerateReport_thenNoDataToReportOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(true);
    testDataUtil.createTestDataWithNoRepeatFailures();
    generateReportAndCheckOutput(0, "### There is no data to report for the specified date range. ####");
  }

  @Test
  void givenTestDataWithNoRepeatFailuresAndEmptyReportsDisabled_whenGenerateReport_thenNullOutput() throws IOException {
    when(feature.sendEmptyFailuresReport()).thenReturn(false);
    testDataUtil.createTestDataWithNoRepeatFailures();
    generateReportAndCheckOutput(0, null);
  }

  private void generateReportAndCheckOutput(
      int expectedCount,
      String expectedOutput
  ) throws IOException {
    String title = String.format(" failures found for Test DCES DRC API failures report produced on %s%n", LocalDate.now());
    String header = "MAAT Id,Contribution Type,Contribution Id,Batch No,Trace Id,Case Submission Id,Processed Date,Event Type Id,Event Type Desc,HTTP Status,Payload" + System.lineSeparator();
    FailureReportDto failureReport = failuresReportService.generateReport("Test", LocalDate.now().minusDays(1));
    if (expectedOutput == null) {
      softly.assertThat(failureReport).isNull();
    } else {
      String csvOutput = Files.readString(failureReport.getReportFile().toPath());
      softly.assertThat(csvOutput).isEqualTo(expectedCount + title + header + expectedOutput);
    }
  }

}