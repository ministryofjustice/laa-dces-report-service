package uk.gov.justice.laa.crime.dces.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO (DCES-25): Consider the possibility to refactor this class to be auto generated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContributionFilesResponse {
    List<String> files;
}
