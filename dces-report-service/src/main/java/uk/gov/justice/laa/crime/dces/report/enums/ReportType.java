package uk.gov.justice.laa.crime.dces.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportType {
  CONTRIBUTION("Contribution"), FDC("FDC");
  private final String description;
}
