package uk.gov.justice.laa.crime.dces.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributionFilesResponse {
    List<String> files;
}
