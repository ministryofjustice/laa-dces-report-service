package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.ContributionCSVDataLine;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList.Fdc;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CSVFileService {

    public static final String FDC_FORMAT_COMMA = "%s,";
    public static final String EMPTY_CHARACTER = "";

    public File writeContributionToCsv(List<ContributionCSVDataLine> contributionData, File targetFile) throws IOException {
        // if file does not exist, we need to add the headers.
        if (targetFile.length() == 0) {
            contributionData.add(0, getContributionsHeader());
        }
        // filewriter initialise
        try (FileWriter fw = new FileWriter(targetFile, true)) {
            for (ContributionCSVDataLine contributionCsvDataLine : contributionData) {
                writeContributionLine(fw, contributionCsvDataLine);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return targetFile;
    }

    public File writeContributionToCsv(List<ContributionCSVDataLine> contributionData, String fileName) throws IOException {
        File targetFile = createCsvFile(fileName);
        return writeContributionToCsv(contributionData, targetFile);
    }

    public void writeFdcToCsv(FdcFile fdcFile, File targetFile) throws IOException {
        List<Fdc> fdcList = fdcFile.getFdcList().getFdc();
        String dateGenerated = DateUtils.convertXmlGregorianToString(fdcFile.getHeader().getDateGenerated());
        // filewriter initialise
        try (FileWriter fw = new FileWriter(targetFile, true)) {
            if (targetFile.length() == 0) {
                writeFdcHeader(fw);
            }
            for (Fdc fdcLine : fdcList) {
                writeFdcLine(fw, fdcLine, dateGenerated);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public File writeFdcFileListToCsv(List<FdcFile> fdcFiles, String fileName) throws IOException {
        File targetFile = createCsvFile(fileName);
        for (FdcFile file : fdcFiles) {
            writeFdcToCsv(file, targetFile);
        }
        return targetFile;
    }

    private ContributionCSVDataLine getContributionsHeader() {
        return ContributionCSVDataLine.builder()
                .maatId("MAAT ID")
                .dataFeedType("Data Feed Type")
                .assessmentDate("Assessment Date")
                .ccOutcomeDate("CC OutCome Date")
                .correspondenceSentDate("Correspondence Sent Date")
                .repOrderStatusDate("Rep Order Status Date")
                .hardshipReviewDate("Hardship Review Date")
                .passportedDate("Passported Date")
                .dateGenerated("Date Generated")
                .build();
    }

    private void writeFdcHeader(FileWriter fw) throws IOException {
        String headerLine = "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Date Generated" + System.lineSeparator();
        fw.append(headerLine);
    }

    private void writeContributionLine(FileWriter fw, ContributionCSVDataLine dataLine) throws IOException {
        String lineOutput = dataLine.toString() + System.lineSeparator();
        fw.append(lineOutput);
    }

    private void writeFdcLine(FileWriter fw, Fdc fdcLine, String dateGenerated) throws IOException {
        fw.append(fdcLineBuilder(fdcLine,dateGenerated));
    }

    private String fdcLineBuilder(Fdc fdcLine, String dateGenerated) {
        return getFdcValue(fdcLine.getMaatId()) +
                getFdcValue(fdcLine.getSentenceDate()) +
                getFdcValue(fdcLine.getCalculationDate()) +
                getFdcValue(fdcLine.getFinalCost()) +
                getFdcValue(fdcLine.getLgfsTotal()) +
                getFdcValue(fdcLine.getAgfsTotal()) +
                dateGenerated +
                System.lineSeparator();
    }

    private String getFdcValue(Object o) {
        return String.format(( FDC_FORMAT_COMMA ), (Objects.nonNull(o) ? o : EMPTY_CHARACTER));
    }

    private String getFdcValue(BigDecimal o){
        return getFdcValue(Objects.nonNull(o) ? o.setScale(2, RoundingMode.UNNECESSARY).toString(): null);
    }

    private String getFdcValue(XMLGregorianCalendar o) {
        return (getFdcValue(DateUtils.convertXmlGregorianToString(o)));
    }

    private File createCsvFile(String fileName) throws IOException {
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
        return Files.createTempFile(fileName, ".csv", attr).toFile();
    }

}
