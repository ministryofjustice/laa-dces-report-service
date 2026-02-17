package uk.gov.justice.laa.crime.dces.report.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil.toInstant;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.justice.laa.crime.dces.report.config.TestConfig;
import uk.gov.justice.laa.crime.dces.report.model.DrcProcessingStatusEntity;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class DrcProcessingStatusRepositoryTest {

  private static final Instant START_TIMESTAMP = toInstant(2025, 1, 1, 0, 0, 0, ZoneOffset.UTC);
  private static final Instant END_TIMESTAMP = toInstant(2025, 1, 2, 0, 0, 0, ZoneOffset.UTC);

  @Autowired
  private TestDataUtil testDataUtil;
  @Autowired
  private DrcProcessingStatusRepository repository;

  @Test
  void givenSampleRecords_whenFindErrorsCreatedWithinRangeIsInvoked_thenOnlyMatchingRecordsAreReturned() {
    // given
    givenRecord( 1L, 200, "error", START_TIMESTAMP.minusMillis(1));     // not selected - out of timestamp range
    givenRecord( 2L, 200, "error", START_TIMESTAMP);                    // selected
    givenRecord( 3L, 200, "Success", START_TIMESTAMP);                  // not selected - Success message
    givenRecord( 4L, 404, "error", START_TIMESTAMP);                    // not selected - not a 200 response
    givenRecord( 5L, 200, "error", END_TIMESTAMP.minusMillis(1));       // selected
    givenRecord( 6L, 200, "error", END_TIMESTAMP);                      // not selected - out of timestamp range
    // when
    List<DrcProcessingStatusEntity> list = repository.findErrorsCreatedWithinRange(START_TIMESTAMP, END_TIMESTAMP);
    // then
    assertThat(list).hasSize(2);
    assertThat(list.get(0).getMaatId()).isEqualTo(2L);
    assertThat(list.get(1).getMaatId()).isEqualTo(5L);
  }

  private void givenRecord(long id, int ackResponseStatus, String statusMessage, Instant creationTimestamp) {
    testDataUtil.saveDrcProcessingStatus(id, id, null, ackResponseStatus, statusMessage, creationTimestamp);
  }

}