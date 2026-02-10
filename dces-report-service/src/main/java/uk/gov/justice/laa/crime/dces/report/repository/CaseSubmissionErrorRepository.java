package uk.gov.justice.laa.crime.dces.report.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionErrorEntity;

@Repository
public interface CaseSubmissionErrorRepository extends
    JpaRepository<CaseSubmissionErrorEntity, Integer> {

  List<CaseSubmissionErrorEntity> findByCreationDateBetween(LocalDateTime start, LocalDateTime end);
}
