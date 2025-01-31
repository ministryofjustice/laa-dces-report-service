package uk.gov.justice.laa.crime.dces.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailureReportKey implements Serializable {
  private Long maatId;
  private String contributionType;
  private Long contributionId;
}