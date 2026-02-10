package uk.gov.justice.laa.crime.dces.report.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseSubmissionErrorDto {
  private Integer id;
  private Integer maatId;
  private Integer concorContributionId;
  private Integer fdcId;
  private String title;
  private Integer status;
  private String detail;
  private LocalDateTime creationDate;
}
