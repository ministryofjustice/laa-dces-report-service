package uk.gov.justice.laa.crime.dces.report.service;

import io.sentry.util.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile.FdcList;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile.FdcList.Fdc;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class CSVFileServiceTest {


    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private CSVFileService CSVFileService;
    private static final Long testMaatId=123456789L;

    private List<CSVDataLine> buildTestContributionFile(){
        var contribution = new CSVDataLine();
        contribution.setMaatId("123456789");
        ArrayList<CSVDataLine> contributionList = new ArrayList<>();
        contributionList.add(contribution);
        return contributionList;
    }


    @Test
    void testWriteContributionToCsv(){
        File file = null;
        try {
            file = File.createTempFile( "test", ".csv");

            List<CSVDataLine> contFile = buildTestContributionFile();
            CSVFileService.writeContributionToCsv(contFile, file);
            String output = FileUtils.readText(file);
            softly.assertThat(output).contains(contFile.get(0).getMaatId());
            softly.assertThat(output).contains("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date");
            softly.assertAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(file)) { file.delete();}
        }
    }
    @Test
    void testWriteFdcToCsv(){
        File file = null;
        try {
            file = File.createTempFile( "test", ".csv");

            FdcFile fdcFile = buildTestFdcFile();
            CSVFileService.writeFdcToCsv(fdcFile, file);
            String output = FileUtils.readText(file);
            softly.assertThat(output).contains(String.valueOf(testMaatId));
            softly.assertThat(output).contains("MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST");
            softly.assertAll();
        } catch (IOException | DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        } finally {
            if(Objects.nonNull(file)){file.delete();}
        }
    }

    private FdcFile buildTestFdcFile() throws DatatypeConfigurationException {
        var fdc = new Fdc();
        fdc.setMaatId(testMaatId);
        fdc.setCalculationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        var fdcList = new FdcList();
        fdcList.getFdc().add(fdc);

        var fdcFile = new FdcFile();
        fdcFile.setFdcList(fdcList);
        return fdcFile;

    }
}
