/*
    This interface defines a contract for retrieving and processing the records and or files held within MAAT
    and accessible via its API and integrating the appropriate webclient.
    Shared between departments as well external vendors for the purpose of creating periodic reports.
 */
package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DcesReportFileService {
    List<String> getFiles(LocalDate start, LocalDate end);

    File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException;

    String getFileName(LocalDate start, LocalDate finish);
}
