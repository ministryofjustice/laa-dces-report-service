package uk.gov.justice.laa.crime.dces.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;

@Repository
public interface CaseSubmissionErrorRepository extends
    JpaRepository<CaseSubmissionErrorEntity, Integer> {

}
