package uk.gov.justice.laa.crime.dces.report.utils;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionEntity;
import uk.gov.justice.laa.crime.dces.report.model.EventTypeEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionRepository;
import uk.gov.justice.laa.crime.dces.report.repository.EventTypeRepository;

@Component
public class TestDataUtil {

  @Autowired
  private CaseSubmissionRepository caseSubmissionRepository;

  @Autowired
  private EventTypeRepository eventTypeRepository;

  private Integer batchId = 100;
  private Integer traceId = 200;
  private String recordType = "Contribution";

  private void resetTestData() {
    batchId = 100;
    traceId = 200;
    caseSubmissionRepository.deleteAll();
    eventTypeRepository.deleteAll();
    createEventTypeData();
  }

  private void createEventTypeData() {
    // Create event types
     eventTypeRepository.save(new EventTypeEntity(1, "FetchedFromMAAT"));
     eventTypeRepository.save(new EventTypeEntity(2, "SyncRequestResponseToDrc"));
     eventTypeRepository.save(new EventTypeEntity(3, "SyncResponseLoggedToMAAT"));
     eventTypeRepository.flush();
  }

  private void saveCaseSubmission(Integer maatId, Integer messageId, Integer eventType, int httpStatus, LocalDateTime processedDate, String payload) {
    CaseSubmissionEntity caseSubmission = new CaseSubmissionEntity(null, batchId, traceId, maatId,
        recordType.equals("Fdc")?null:messageId, recordType.equals("Fdc")?messageId:null,
        recordType, processedDate, eventType, httpStatus, payload);
    caseSubmissionRepository.saveAndFlush(caseSubmission);
  }

  public void createTestDataWithFailures() {
    resetTestData();

    recordType = "Fdc";

    //Older batches
    batchId = 95;

    // Insert case submission that fails in DRC
    saveCaseSubmission(95, 95, 1, 200, LocalDateTime.of(2024, 12, 1, 11, 10, 0), null);
    saveCaseSubmission(95, 95, 2, 500, LocalDateTime.of(2024, 12, 1, 11, 10, 1), null);

    //Second last batch
    batchId = 97;
    // Insert case submission that fails in DRC with one contr ID but will succeed in the next batch with a new contr ID
    saveCaseSubmission(197, 197, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(197, 197, 2, 504, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    // Insert case submission that fails in DRC
    saveCaseSubmission(198, 198, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(198, 198, 2, 504, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    batchId = 99;
    // Insert case submission that succeeds
    saveCaseSubmission(99, 99, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(99, 99, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(99, 99, 3, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    // Insert case submission that failed in DRC with one contr ID but succeeds in the this batch with a new contr ID
    saveCaseSubmission(197, 199, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(197, 199, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(197, 199, 3, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    // Insert case submission that fails again in DRC
    saveCaseSubmission(198, 198, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(198, 198, 2, 504, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    //Create test data for Concor Contributions
    recordType = "Contribution";

    //Older batches
    batchId = 98;

    // Insert case submission that succeeds
    saveCaseSubmission(98, 98, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(98, 98, 2, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(98, 98, 3, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    // "second-last" batch
    batchId = 100;

    // Insert case submission that fails in DRC
    saveCaseSubmission(100, 200, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(100, 200, 2, 418, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);

    // Insert another case submission that fails in DRC
    saveCaseSubmission(101, 201, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(101, 201, 2, 418, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);

    // Insert case submission that succeeds after failing once in DRC
    saveCaseSubmission(102, 202, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(102, 202, 2, 418, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);
    saveCaseSubmission(102, 202, 2, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 2), null);

    // Insert case submission that succeeds in DRC but fails in MAAT
    saveCaseSubmission(103, 203, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(103, 203, 2, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);
    saveCaseSubmission(103, 203, 3, 418, LocalDateTime.of(2025, 1, 1, 11, 10, 2), null);

    // Insert case submission that has no DRC or MAAT responses in this batch but will have a failure response in the next batch
    saveCaseSubmission(104, 204, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    // Insert case submission that fails in the first call
    saveCaseSubmission(105, 205, 1, 500, LocalDateTime.of(2025, 1, 1, 11, 10, 0), "No parse XML! :(");

    // Insert case submission that fails in DRC, will fail in MAAT in the next batch
    saveCaseSubmission(106, 200, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(106, 200, 2, 502, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);

    // Insert case submission that has no DRC or MAAT responses in this batch or the next batch
    saveCaseSubmission(107, 207, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    // Latest batch
    batchId = 102;
    // Insert case submission that failed in previous batch but succeeds now
    saveCaseSubmission(100, 206, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(100, 206, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);
    saveCaseSubmission(100, 206, 3, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);

    // Insert case submission that fails in DRC again
    saveCaseSubmission(101, 201, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(101, 201, 2, 418, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);

    // Insert case submission that succeeds in DRC but fails in MAAT again
    saveCaseSubmission(103, 203, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(103, 203, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);
    saveCaseSubmission(103, 203, 3, 418, LocalDateTime.of(2025, 1, 2, 11, 10, 2), null);

    // Insert case submission that fails again in DRC
    saveCaseSubmission(104, 204, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(104, 204, 2, 502, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);

    // Insert case submission that fails again in the first call
    saveCaseSubmission(105, 206, 1, 500, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    // Insert case submission that failed in DRC previously and now fails in MAAT
    saveCaseSubmission(106, 206, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(106, 206, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 1), null);
    saveCaseSubmission(106, 206, 3, 502, LocalDateTime.of(2025, 1, 2, 11, 10, 2), null);

    // Insert case submission that has no DRC or MAAT responses in this batch or the previous batch
    saveCaseSubmission(107, 207, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    // Insert header case submission with null maatId and concorId
    CaseSubmissionEntity caseSubmission = new CaseSubmissionEntity(null, batchId, traceId, null, null, null, "Contribution",
        LocalDateTime.of(2025, 1, 2, 11, 10, 0), 1, 200, "Fetched:3");
    caseSubmissionRepository.saveAndFlush(caseSubmission);
  }

  public void createTestDataWithNoRepeatFailures() {
    resetTestData();
    recordType = "Fdc";

    //Older batches
    batchId = 95;
    // Insert case submission that fails in DRC
    saveCaseSubmission(95, 95, 1, 200, LocalDateTime.of(2024, 12, 1, 11, 10, 0), null);
    saveCaseSubmission(95, 95, 2, 500, LocalDateTime.of(2024, 12, 1, 11, 10, 1), null);

    //Second last batch
    batchId = 97;
    // Insert case submission that fails in DRC with one contr ID but will succeed in the next batch with a new contr ID
    saveCaseSubmission(197, 197, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(197, 197, 2, 504, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    // Insert case submission that fails in DRC
    saveCaseSubmission(198, 198, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(198, 198, 2, 504, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    batchId = 99;
    // Insert case submission that succeeds
    saveCaseSubmission(99, 99, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(99, 99, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(99, 99, 3, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    // Insert case submission that failed in DRC with one contr ID but succeeds in this batch with a new contr ID
    saveCaseSubmission(197, 198, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(197, 198, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(197, 198, 3, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    // Insert case submission that failed in DRC for the first time, i.e. not a repeat failure
    saveCaseSubmission(196, 196, 1, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(196, 196, 2, 200, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);
    saveCaseSubmission(196, 196, 3, 504, LocalDateTime.of(2025, 1, 2, 11, 10, 0), null);

    //Create test data for Concor Contributions
    recordType = "Contribution";

    //Older batches
    batchId = 98;

    // Insert case submission that succeeds
    saveCaseSubmission(98, 98, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(98, 98, 2, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(98, 98, 3, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);

    // latest batch
    batchId = 100;

    // Insert case submission that fails in DRC
    saveCaseSubmission(100, 200, 1, 200, LocalDateTime.of(2025, 1, 1, 11, 10, 0), null);
    saveCaseSubmission(100, 200, 2, 418, LocalDateTime.of(2025, 1, 1, 11, 10, 1), null);
  }

}