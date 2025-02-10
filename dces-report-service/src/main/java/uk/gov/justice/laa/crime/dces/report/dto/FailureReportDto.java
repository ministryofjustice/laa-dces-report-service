package uk.gov.justice.laa.crime.dces.report.dto;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailureReportDto {
  File reportFile;
  String failuresCountMessage;
}
