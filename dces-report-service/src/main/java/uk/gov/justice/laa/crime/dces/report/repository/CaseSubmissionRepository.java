package uk.gov.justice.laa.crime.dces.report.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionEntity;

@Repository
public interface CaseSubmissionRepository extends JpaRepository<CaseSubmissionEntity, Integer> {

  //Get the latest batch ID for a given record type
  CaseSubmissionEntity findTopByRecordTypeAndBatchIdIsNotNullOrderByBatchIdDesc(String recordType);

  //Get the second-latest batch ID for a given record type when given the latest batch ID
  CaseSubmissionEntity findTopByRecordTypeAndBatchIdIsNotNullAndBatchIdLessThanOrderByBatchIdDesc(String recordType, Integer batchId);

  //Get all case submissions for a given batch ID where the MAAT ID is not null, i.e. all except the header records
  List<CaseSubmissionEntity> findByBatchIdAndMaatIdIsNotNull(Integer batchId);

}