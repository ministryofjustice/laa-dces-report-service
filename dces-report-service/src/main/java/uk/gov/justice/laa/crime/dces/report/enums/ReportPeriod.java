package uk.gov.justice.laa.crime.dces.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportPeriod {
  MONTHLY("Monthly"), DAILY("Daily");
  private final String description;
}
