package uk.gov.justice.laa.crime.dces.report.utils;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionEntity;
import uk.gov.justice.laa.crime.dces.report.repository.CaseSubmissionRepository;

@Component
public class TestDataUtil {

  @Autowired
  private CaseSubmissionRepository caseSubmissionRepository;

  private Long testId = 0L;
  private Long batchId = 100L;
  private Long traceId = 200L;
  private Long maatId = 300L;
  private Long concorId = 400L;
  private Long fdcId = 500L;

  private void resetTestData() {
    testId = 0L;
    batchId = 100L;
    traceId = 200L;
    maatId = 300L;
    concorId = 400L;
    fdcId = 500L;
    caseSubmissionRepository.deleteAll();
  }

  public void createTestDataWithFailures() {
    resetTestData();

    // Insert case submission that fails in DRC once
    CaseSubmissionEntity caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId, maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);

    // Insert case submission that fails in DRC twice
    maatId++;
    concorId++;
    caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId, maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(400);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setProcessedDate(LocalDateTime.of(2025, 1, 2, 11, 10, 0));
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);

    // Insert case submission that succeeds after failing once in DRC
    maatId++;
    concorId++;
    caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId, maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    // Insert case submission that succeeds in DRC but fails twice in MAAT
    maatId++;
    concorId++;
    caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId, maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setProcessedDate(LocalDateTime.of(2025, 1, 3, 11, 10, 0));
    caseSubmission.setHttpStatus(404);
    caseSubmissionRepository.save(caseSubmission);
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setProcessedDate(LocalDateTime.of(2025, 1, 4, 11, 10, 0));
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);
    // Insert case submission for FDC that has no DRC or MAAT responses
    caseSubmission.setId(++testId);
    caseSubmission.setEventType(1);
    caseSubmission.setHttpStatus(200);
    caseSubmission.setFdcId(fdcId);
    caseSubmission.setRecordType("Fdc");
    caseSubmission.setConcorContributionId(null);
    caseSubmissionRepository.save(caseSubmission);

  }

  public void createTestDataWithNoFailures() {
    resetTestData();

    // Insert case submission that fails in DRC once but then succeeds
    CaseSubmissionEntity caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId,
        maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    // Insert case submission that fails in MAAT once but then succeeds
    caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId,
        maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

  }

  public void createTestDataWithMultipleContributionsPerMaatId() {
    resetTestData();

    // Insert case submission that succeeds

    CaseSubmissionEntity caseSubmission = new CaseSubmissionEntity(++testId, batchId, traceId,
        maatId, concorId, null, "Contribution",
        LocalDateTime.of(2025, 1, 1, 11, 10, 0), 1, 200, null);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(3);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    // Insert case submission with a new concor ID that fails

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(1);
    caseSubmission.setConcorContributionId(++concorId);
    caseSubmission.setHttpStatus(200);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(418);
    caseSubmissionRepository.save(caseSubmission);

    caseSubmission.setId(++testId);
    caseSubmission.setEventType(2);
    caseSubmission.setHttpStatus(418);
    caseSubmission.setProcessedDate(LocalDateTime.of(2025, 1, 4, 11, 10, 0));
    caseSubmissionRepository.save(caseSubmission);

  }
}