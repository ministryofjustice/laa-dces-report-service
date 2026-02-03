package uk.gov.justice.laa.crime.dces.report.service;

import io.sentry.util.FileUtils;
import java.time.ZoneOffset;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.dto.DrcProcessingStatusDto;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
import uk.gov.justice.laa.crime.dces.report.model.ContributionCSVDataLine;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile.FdcList.Fdc;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import uk.gov.justice.laa.crime.dces.report.utils.TestDataUtil;


@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CSVFileServiceTest {
    private static final String CONTRIBUTIONS_HEADER = "MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date";
    private static final String FDC_HEADER = "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Transmission Date";

    private static final String CASE_SUBMISSION_ERROR_COLUMNS_HEADER = "MAAT Id,Concor Contribution Id,Fdc Id,Error Type,Processed Date" + System.lineSeparator();

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
            testFile = csvFileService.writeContributionToCsv(contFile, "Test", date.minusDays(30),
                date, "test");
            String output = FileUtils.readText(testFile);

            softly.assertThat(output).contains(CONTRIBUTIONS_HEADER);
            softly.assertThat(output).contains(contFile.get(0).getMaatId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ContributionCSVDataLine> buildTestContributionFile() {
        var contribution = new ContributionCSVDataLine();
        contribution.setMaatId("123456789");
        ArrayList<ContributionCSVDataLine> contributionList = new ArrayList<>();
        contributionList.add(contribution);
        return contributionList;
    }

    @Test
    void testWriteFdcToCsv() {
        try {
            List<FdcFile> fdcFiles = buildTestFdcFiles();
            LocalDate date = LocalDate.now();
            testFile = csvFileService.writeFdcFileListToCsv(fdcFiles, "Test", "Test", date.minusDays(30), date);
            String output = FileUtils.readText(testFile);

            softly.assertThat(output).contains(FDC_HEADER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<FdcFile> buildTestFdcFiles() throws DatatypeConfigurationException {
        ArrayList<FdcFile> fdcFiles = new ArrayList<>();
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
        fdcFiles.add(fdcFile);
        return fdcFiles;
    }

    @Test
    void givenACaseSubmissionData_whenWriteDrcProcessingErrorToCsvIsInvoked_shouldGenerateReport() {
        DrcProcessingStatusDto drcProcessingStatusDto = DrcProcessingStatusDto.builder()
                .id(1L)
                .maatId(1234L)
                .concorContributionId(1L)
                .fdcId(1L)
                .statusMessage("MAATID invalid")
                .drcProcessingTimestamp(TestDataUtil.toInstant(2025, 1, 1, 0, 0, 0, ZoneOffset.UTC))
                .creationTimestamp(TestDataUtil.toInstant(2025, 1, 1, 0, 0, 5, ZoneOffset.UTC))
                .build();

        String expectedData = CASE_SUBMISSION_ERROR_COLUMNS_HEADER + "1234,1,1,MAATID invalid,"+LocalDateTime.of(2025, 1, 1, 0, 0, 0).toString();

        try {
            FailureReportDto f = csvFileService.writeDrcProcessingErrorToCsv(List.of(drcProcessingStatusDto), "Test");
            String output = FileUtils.readText(f.getReportFile());
            softly.assertThat(output).contains(expectedData);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenAEmptyCaseSubmissionError_whenWriteDrcProcessingErrorToCsvIsInvoked_shouldGenerateFileWithHeader() {

        String expectedData = CASE_SUBMISSION_ERROR_COLUMNS_HEADER + "### There is no data to report for the specified date range. ####";

        try {
            FailureReportDto f = csvFileService.writeDrcProcessingErrorToCsv(Collections.emptyList(), "Test");
            String output = FileUtils.readText(f.getReportFile());
            softly.assertThat(output).contains(expectedData);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
