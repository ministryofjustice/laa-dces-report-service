package uk.gov.justice.laa.crime.dces.report.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.dces.report.model.DrcProcessingStatusEntity;

@Repository
public interface DrcProcessingStatusRepository extends
    JpaRepository<DrcProcessingStatusEntity, Long> {

  List<DrcProcessingStatusEntity> findByCreationTimestampBetween(Instant start, Instant end);
}
