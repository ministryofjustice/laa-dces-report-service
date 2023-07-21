package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList.Fdc;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;
import uk.gov.justice.laa.crime.dces.utils.DateUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CSVFileService {


    public static final String FDC_FORMAT = "%s";
    public static final String FDC_FORMAT_COMMA = "%s,";
    public static final String EMPTY_CHARACTER = "";

    public File writeContributionToCsv(List<CSVDataLine> contributionData, File targetFile) throws IOException {
        // if file does not exist, we need to add the headers.
        if(targetFile.length()==0) {
            contributionData.add(0, getContributionsHeader());
        }
        // filewriter initialise
        FileWriter fw = new FileWriter(targetFile, true);
        for (CSVDataLine csvDataLine: contributionData){
            writeContributionLine(fw, csvDataLine);
        }
        fw.close();
        return targetFile;
    }

    public File writeContributionToCsv(List<CSVDataLine> contributionData, String fileName) throws IOException {
        File targetFile = createCsvFile(fileName);
        return writeContributionToCsv(contributionData, targetFile);
    }

    public File writeFdcToCsv(FdcFile fdcFile, File targetFile) throws IOException {
        List<Fdc> fdcList = fdcFile.getFdcList().getFdc();
        // filewriter initialise
        FileWriter fw = new FileWriter(targetFile, true);
        if(targetFile.length()==0) {
            writeFdcHeader(fw);
        }
        for (Fdc fdcLine: fdcList){
            writeFdcLine(fw, fdcLine);
        }
        fw.close();
        return targetFile;
    }

    public File writeFdcFileListToCsv(List<FdcFile> fdcFiles, String fileName) throws IOException {
        File targetFile = createCsvFile(fileName);
        for(FdcFile file: fdcFiles){
            writeFdcToCsv(file, targetFile);
        }
        return targetFile;
    }

    private File createCsvFile(String fileName) throws IOException {
        return File.createTempFile( fileName, ".csv");
    }

    private CSVDataLine getContributionsHeader(){
        return CSVDataLine.builder()
                .maatId("MAAT ID")
                .dataFeedType("Data Feed Type")
                .assessmentDate("Assessment Date")
                .ccOutcomeDate("CC OutCome Date")
                .correspondenceSentDate("Correspondence Sent Date")
                .repOrderStatusDate("Rep Order Status Date")
                .hardshipReviewDate("Hardship Review Date")
                .passportedDate("Passported Date")
                .build();
    }

    private void writeFdcHeader(FileWriter fw) throws IOException {
        String headerLine = "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST"+System.lineSeparator();
        fw.append(headerLine);
    }

    private void writeContributionLine(FileWriter fw, CSVDataLine dataLine) throws IOException {
        String lineOutput = dataLine.toString()+System.lineSeparator();
        fw.append(lineOutput);
    }

    private void writeFdcLine(FileWriter fw, Fdc fdcLine) throws IOException {
        fw.append(fdcLineBuilder(fdcLine));
    }

    private String fdcLineBuilder(Fdc fdcLine){
        StringBuilder sb = new StringBuilder();
        sb.append(getFdcValue(fdcLine.getMaatId(),true));
        sb.append(getFdcValue(fdcLine.getSentenceDate()));
        sb.append(getFdcValue(fdcLine.getCalculationDate()));
        sb.append(getFdcValue(fdcLine.getFinalCost(),true));
        sb.append(getFdcValue(fdcLine.getLgfsTotal(),true));
        sb.append(getFdcValue(fdcLine.getAgfsTotal(),false));
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    private String getFdcValue(Object o, boolean insertComma){
        return String.format( (insertComma?FDC_FORMAT_COMMA:FDC_FORMAT),(Objects.nonNull(o)?o:EMPTY_CHARACTER));
    }

    private String getFdcValue(XMLGregorianCalendar o){
        return ( getFdcValue(DateUtils.convertXmlGregorianToString(o),true));
    }

}
