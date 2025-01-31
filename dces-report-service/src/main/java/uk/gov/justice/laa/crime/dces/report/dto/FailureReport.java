package uk.gov.justice.laa.crime.dces.report.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@IdClass(FailureReportKey.class)
public class FailureReport {
  @Id
  private Long maatId;

  @Id
  private String contributionType;

  @Id
  private Long contributionId;

  private Boolean sentToDrc;
  private Integer drcSendAttempts;
  private LocalDateTime firstDrcAttemptDate;
  private LocalDateTime lastDrcAttemptDate;
  private Boolean updatedInMaat;
  private Integer maatUpdateAttempts;
  private LocalDateTime firstMaatAttemptDate;
  private LocalDateTime lastMaatAttemptDate;

}