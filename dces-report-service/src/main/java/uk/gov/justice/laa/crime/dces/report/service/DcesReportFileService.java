/*
    This interface defines a contract for retrieving and processing the records and or files held within MAAT
    and accessible via its API and integrating the appropriate webclient.
    Shared between departments as well external vendors for the purpose of creating periodic reports.
 */
package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public interface DcesReportFileService {
    ContributionFilesResponse getFiles(LocalDate start, LocalDate end);

    File processFiles(List<String> files, LocalDate start, LocalDate finish, String fileName) throws JAXBException, IOException;

    String getFileName(LocalDate start, LocalDate finish);

    default boolean searchInFile(File file, String toSearchFor) throws FileNotFoundException {
        boolean isFound = false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                isFound |= searchInLine(scanner.nextLine(), toSearchFor);
            }
        }

        return isFound;
    }

    default boolean searchInLine(String line, String toSearchFor) {
        // TODO (DCES-57): remove sout once we are happy with test result
        System.out.println(line);
        return line.contains(toSearchFor);
    }
}
