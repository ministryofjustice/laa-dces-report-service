package uk.gov.justice.laa.crime.dces.report.repository;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReport;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class FailureReportRepositoryTest {

  @Autowired
  private FailureReportRepository failureReportRepository;

  @Autowired
  private TestDataUtil testDataUtil;

  @Test
  public void testFindFailures() {
    testDataUtil.createTestDataWithFailures();

    // Execute the query
    List<FailureReport> failures = failureReportRepository.findFailures();

    assertThat(failures).isNotEmpty();
    assertThat(failures.size()).isEqualTo(4);

    // Check that the first row is the single failure to DRC
    assertThat(failures.get(0).getMaatId()).isEqualTo(300L);
    assertThat(failures.get(0).getContributionType()).isEqualTo("Contribution");
    assertThat(failures.get(0).getContributionId()).isEqualTo(400L);
    assertThat(failures.get(0).getSentToDrc()).isFalse();
    assertThat(failures.get(0).getDrcSendAttempts()).isEqualTo(1);
    assertThat(failures.get(0).getFirstDrcAttemptDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));
    assertThat(failures.get(0).getLastDrcAttemptDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));
    assertThat(failures.get(0).getUpdatedInMaat()).isFalse();
    assertThat(failures.get(0).getMaatUpdateAttempts()).isEqualTo(0);

    // Check that the second row is the double failure in DRC
    assertThat(failures.get(1).getMaatId()).isEqualTo(301L);
    assertThat(failures.get(1).getContributionType()).isEqualTo("Contribution");
    assertThat(failures.get(1).getContributionId()).isEqualTo(401L);
    assertThat(failures.get(1).getSentToDrc()).isFalse();
    assertThat(failures.get(1).getDrcSendAttempts()).isEqualTo(2);
    assertThat(failures.get(1).getFirstDrcAttemptDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 11, 10, 0));
    assertThat(failures.get(1).getLastDrcAttemptDate()).isEqualTo(LocalDateTime.of(2025, 1, 2, 11, 10, 0));
    assertThat(failures.get(1).getUpdatedInMaat()).isFalse();
    assertThat(failures.get(1).getMaatUpdateAttempts()).isEqualTo(0);


    // Check that the successful submission is not in the results
    assertThat(failures)
        .extracting(FailureReport::getMaatId)
        .doesNotContain(302L);

  }

}