package uk.gov.justice.laa.crime.dces.report.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.dces.report.model.DrcProcessingStatusEntity;

@Repository
public interface DrcProcessingStatusRepository extends
    JpaRepository<DrcProcessingStatusEntity, Long> {

  /**
   * Find error records created with the given range.
   * @param start include records equal to or after this timestamp
   * @param end include records before but not including this timestamp
   */
  @Query("""
    SELECT e
    FROM DrcProcessingStatusEntity e
    WHERE e.creationTimestamp >= :start
      AND e.creationTimestamp < :end
      AND e.statusMessage <> 'Success'
  """)
  List<DrcProcessingStatusEntity> findErrorsCreatedWithinRange(
      @Param("start") Instant start,
      @Param("end") Instant end);

}
