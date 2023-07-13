/*
    This interface defines a contract for retrieving and processing the records and or files held within MAAT
    and accessible via its API and integrating the appropriate webclient.
    Shared between departments as well external vendors for the purpose of creating periodic reports.
 */
package uk.gov.justice.laa.crime.dces.report.service;

import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

public interface MaatApiRecords {
    ContributionFilesResponse getFiles(LocalDate start, LocalDate end);

    String processFiles(String xmlString);
}
