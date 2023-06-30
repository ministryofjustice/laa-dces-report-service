package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile.FdcList.Fdc;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CSVFileService {

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
        File targetFile = getFile(fileName);
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

    public File writeFdcToCsv(FdcFile fdcFile, String fileName) throws IOException {
        File targetFile = getFile(fileName);
        return writeFdcToCsv(fdcFile, targetFile);
    }

    private File getFile(String fileName){
        // TODO: have file path in config, fix below line!
        String path = System.getProperty("user.home")+"/Desktop";
        return new File(path,fileName);
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
//        sb.append();



        return "";
    }

}
