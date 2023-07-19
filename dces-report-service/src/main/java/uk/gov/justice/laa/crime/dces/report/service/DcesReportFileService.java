/*
    This interface defines a contract for retrieving and processing the records and or files held within MAAT
    and accessible via its API and integrating the appropriate webclient.
    Shared between departments as well external vendors for the purpose of creating periodic reports.
 */
package uk.gov.justice.laa.crime.dces.report.service;

import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public interface DcesReportFileService {
    ContributionFilesResponse getFiles(LocalDate start, LocalDate end);

    File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException;

    String getFileName(LocalDate start, LocalDate finish);

    default boolean searchInFile(File file, String toSearchFor) throws IOException {
        return Optional.ofNullable(FileUtils.readText(file))
                .orElse("")
                .contains(toSearchFor);
    }

    default boolean searchInLine(String line, String toSearchFor) {
       return line.contains(toSearchFor);
    }
}
