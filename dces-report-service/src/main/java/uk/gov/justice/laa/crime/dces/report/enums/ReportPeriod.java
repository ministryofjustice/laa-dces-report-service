package uk.gov.justice.laa.crime.dces.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportPeriod {
  Monthly("Monthly"), Daily("Daily");
  private final String description;
}
