package uk.gov.justice.laa.crime.dces.report.service;

import java.io.OutputStreamWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.model.CaseSubmissionEntity;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CSVFileService {

    public static final String FDC_FORMAT_COMMA = "%s,";
    public static final String EMPTY_CHARACTER = "";

    private static final String CONTRIBUTIONS_HEADING = "Contributions Report";

    private static final String FDC_HEADING = "Final Defence Cost Report";

    private static final String FAILURES_HEADING = "DCES DRC API Failures Report";

    private static final String FAILURES_COLUMNS_HEADER = "MAAT Id,Contribution Type,Contribution Id,Batch No,Trace Id,Case Submission Id,Processed Date,Event Type Id,Event Type Desc,HTTP Status,Payload" + System.lineSeparator();

    private static final String NO_DATA_MESSAGE = "### There is no data to report for the specified date range. ####";

    private static final String TITLE_TEMPLATE = "%s %s REPORTING DATE FROM: %s | REPORTING DATE TO: %s | REPORTING PRODUCED ON: %s" + System.lineSeparator();

    private static final String FDC_COLUMNS_HEADER = "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Transmission Date" + System.lineSeparator();
    private static final String FILE_PERMISSIONS = "rwx------";
    private final EventTypeService eventTypeService;

    public File writeContributionToCsv(
        List<ContributionCSVDataLine> contributionData,
        String reportTitle,
        LocalDate fromDate,
        LocalDate toDate,
        String fileName
    ) throws IOException {
        File targetFile = createCsvFile(fileName);
        // file-writer initialise
        try (FileWriter fw = new FileWriter(targetFile, true)) {
            String title = String.format(TITLE_TEMPLATE, reportTitle, CONTRIBUTIONS_HEADING, fromDate, toDate, LocalDate.now());
            fw.append(title);

            contributionData.add(0, getContributionsHeader());

            for (ContributionCSVDataLine contributionCsvDataLine : contributionData) {
                writeContributionLine(fw, contributionCsvDataLine);
            }
            if (contributionData.size() == 1) {
                fw.append(NO_DATA_MESSAGE);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return targetFile;
    }

    public File writeFdcFileListToCsv(List<FdcFile> fdcFiles, String fileName, String reportTitle, LocalDate fromDate, LocalDate toDate) throws IOException {
        File targetFile = createCsvFile(fileName);
        // file-writer initialise
        try (FileWriter fw = new FileWriter(targetFile, true)) {
            writeFdcHeader(fw, reportTitle, fromDate, toDate);
            boolean someDataFound = false;
            for (FdcFile fdcFile : fdcFiles) {
                List<Fdc> fdcList = fdcFile.getFdcList().getFdc();
                String dateGenerated = DateUtils.convertXmlGregorianToString(
                    fdcFile.getHeader().getDateGenerated());
                for (Fdc fdcLine : fdcList) {
                    writeFdcLine(fw, fdcLine, dateGenerated);
                    someDataFound = true;
                }
            }
            if (!someDataFound) {
                fw.append(NO_DATA_MESSAGE);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return targetFile;
    }

    public FailureReportDto writeFailuresToCsv(List<CaseSubmissionEntity> failures, String fileName, LocalDate reportDate) throws IOException {
        String failuresCountMessage = failures.size() +" failures found for ";
        File targetFile = createCsvFile(fileName);
        // file-writer initialise
        try (FileWriter fw = new FileWriter(targetFile, true)) {
            writeFailuresHeader(fw, failuresCountMessage, reportDate);
            boolean someDataFound = false;
            for (CaseSubmissionEntity failure : failures) {
                fw.append(buildFailureLine(failure));
                someDataFound = true;
            }
            if (!someDataFound) {
                fw.append(NO_DATA_MESSAGE);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return new FailureReportDto(targetFile, failuresCountMessage);
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
                .dateGenerated("Transmission Date")
                .build();
    }

    private void writeFdcHeader(FileWriter fw, String reportTitle, LocalDate fromDate, LocalDate toDate) throws IOException {
        String headerLine = String.format(TITLE_TEMPLATE, reportTitle, FDC_HEADING, fromDate, toDate, LocalDate.now());
        headerLine += FDC_COLUMNS_HEADER;
        fw.append(headerLine);
    }

    private void writeFailuresHeader(OutputStreamWriter fw, String reportTitle, LocalDate reportDate) throws IOException {
        String headerLine = String.format(TITLE_TEMPLATE, reportTitle, FAILURES_HEADING, "N/A", reportDate, LocalDate.now());
        headerLine += FAILURES_COLUMNS_HEADER;
        fw.append(headerLine);
    }

    private void writeContributionLine(FileWriter fw, ContributionCSVDataLine dataLine) throws IOException {
        String lineOutput = dataLine.toString() + System.lineSeparator();
        fw.append(lineOutput);
    }

    private void writeFdcLine(FileWriter fw, Fdc fdcLine, String dateGenerated) throws IOException {
        fw.append(fdcLineBuilder(fdcLine, dateGenerated));
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

    private String buildFailureLine(CaseSubmissionEntity failure) {
        return Objects.toString(failure.getMaatId(), "")
            + ',' + Objects.toString(failure.getRecordType(), "")
            + ',' + Objects.toString(failure.getRecordType().equals("Fdc")?failure.getFdcId():failure.getConcorContributionId(), "")
            + ',' + failure.getBatchId()
            + ',' + failure.getTraceId()
            + ',' + failure.getId()
            + ',' + failure.getProcessedDate()
            + ',' + failure.getEventType()
            + ',' + eventTypeService.getEventTypeEntity(failure.getEventType()).getDescription()
            + ',' + failure.getHttpStatus()
            + ',' + failure.getPayload()
            + System.lineSeparator();
    }

    private String getFdcValue(Object o) {
        return String.format((FDC_FORMAT_COMMA), (Objects.nonNull(o) ? o : EMPTY_CHARACTER));
    }

    private String getFdcValue(BigDecimal o) {
        return getFdcValue(Objects.nonNull(o) ? o.setScale(2, RoundingMode.UNNECESSARY).toString() : null);
    }

    private String getFdcValue(XMLGregorianCalendar o) {
        return (getFdcValue(DateUtils.convertXmlGregorianToString(o)));
    }

    private File createCsvFile(String fileName) throws IOException {
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(FILE_PERMISSIONS));
        return Files.createTempFile(fileName, ".csv", attr).toFile();
    }
}
