package uk.gov.justice.laa.crime.dces.report.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrcProcessingStatusDto {
  private Long id;
  private Long maatId;
  private Long concorContributionId;
  private Long fdcId;
  private String statusMessage;
  private Instant drcProcessingTimestamp;
  private Instant creationTimestamp;
}
