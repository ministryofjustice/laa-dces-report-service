package uk.gov.justice.laa.crime.dces.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * The result of how a Contribution or FDC was processed by the Debt Recovery Company (DRC).
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "drc_processing_status")
public class DrcProcessingStatusEntity {

  @Id
  @SequenceGenerator(name = "drc_processing_status_gen_seq", sequenceName = "drc_processing_status_gen_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drc_processing_status_gen_seq")
  private Long id;
  private Long maatId;
  private Long concorContributionId;
  private Long fdcId;
  private Integer ackResponseStatus;
  private String statusMessage;
  private Instant drcProcessingTimestamp;
  private Instant creationTimestamp;

}

