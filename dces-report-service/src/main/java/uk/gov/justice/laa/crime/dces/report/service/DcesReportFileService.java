/*
    This interface defines a contract for retrieving records and or files held within MAAT
    and accessible via its API and integrating the appropriate webclient.
 */
package uk.gov.justice.laa.crime.dces.report.service;

import java.time.LocalDate;
import java.util.List;

public interface DcesReportFileService {
    List<String> getFiles(LocalDate start, LocalDate end);

    String getFileName(LocalDate start, LocalDate finish);

    String getType();
}
