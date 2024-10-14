package uk.gov.justice.laa.crime.dces.report.service;

import io.sentry.util.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.model.ContributionCSVDataLine;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList.Fdc;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class CSVFileServiceTest {
    private static final String CONTRIBUTIONS_HEADER = "MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date";
    private static final String FDC_HEADER = "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Transmission Date";

    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private CSVFileService csvFileService;
    private static final Long testMaatId = 123456789L;
    File testFile = null;


    @AfterEach
    void postTest()
    {
        softly.assertAll();

        if (Objects.nonNull(testFile)) {
            testFile.delete();
            testFile = null;
        }
    }


    @Test
    void testWriteContributionToCsv() {
        try {
            List<ContributionCSVDataLine> contFile = buildTestContributionFile();
            LocalDate date = LocalDate.now();
            testFile = csvFileService.writeContributionToCsv(contFile, "test", "Test",  date.minusDays(30), date);
            String output = FileUtils.readText(testFile);

            softly.assertThat(output).contains(CONTRIBUTIONS_HEADER);
            softly.assertThat(output).contains(contFile.get(0).getMaatId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenTargetFileNotEmpty_whenCallingWriteContributionToCsv_thenShouldReturnCVSFileWithoutHeader() throws IOException {
        try (FileWriter fw =  new FileWriter(testFile, true)) {
            fw.append("Mocked Title\n");
        } catch (IOException e) {
            throw new IOException(e);
        }

        softly.assertThat(testFile.length()).isGreaterThan(0);

        List<ContributionCSVDataLine> contFile = buildTestContributionFile();
        LocalDate date = LocalDate.now();
        testFile = csvFileService.writeContributionToCsv(contFile, "test", "Test", date.minusDays(30), date);
        String output = FileUtils.readText(testFile);

        softly.assertThat(output).startsWith("Mocked Title");
        softly.assertThat(output).doesNotContain(CONTRIBUTIONS_HEADER);
        softly.assertThat(output).contains(contFile.get(0).getMaatId());
    }


    private List<ContributionCSVDataLine> buildTestContributionFile() {
        var contribution = new ContributionCSVDataLine();
        contribution.setMaatId("123456789");
        ArrayList<ContributionCSVDataLine> contributionList = new ArrayList<>();
        contributionList.add(contribution);
        return contributionList;
    }

    private FdcFile buildTestFdcFile() throws DatatypeConfigurationException {
        var fdc = new Fdc();
        fdc.setMaatId(testMaatId);
        fdc.setCalculationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2020, Calendar.JUNE, 30, 4, 0, 0)));
        var fdcList = new FdcList();
        fdcList.getFdc().add(fdc);

        var fdcFile = new FdcFile();
        fdcFile.setFdcList(fdcList);

        FdcFile.Header header = new FdcFile.Header();
        header.setDateGenerated(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2020, Calendar.JUNE, 30, 4, 0, 0)));
        fdcFile.setHeader(header);
        return fdcFile;
    }
}
