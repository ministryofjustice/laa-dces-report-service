package uk.gov.justice.laa.crime.dces.report.mapper;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import java.nio.file.Files;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class ContributionsFileMapperTest {
    private final DateTimeFormatter dateFormatterXml = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateFormatterCsv = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String filename = "this_is_a_test.xml";
    private static final String EXPECTED_HEADER = "MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date" + System.lineSeparator();
    private static final String NO_DATA_MESSAGE = "### There is no data to report for the specified date range. ####";
    private static final String EXPECTED_TITLE = "Test Contributions Report REPORTING DATE FROM: %s | REPORTING DATE TO: %s | REPORTING PRODUCED ON: %s" + System.lineSeparator();


    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private ContributionsFileMapper contributionsFileMapper;
    File csvFile = null;


    @AfterEach
    void resetCsvFileService() {
        // check all assertions
        softly.assertAll();

        // Clean CSV generated files
        if (Objects.nonNull(csvFile)) {
            closeFile(csvFile);
            // Reset CSV file reference
            csvFile = null;
        }

        // Reset service reference
        contributionsFileMapper.csvFileService = new CSVFileService(null);
    }

    private void closeFile(File f) {
        if (Objects.nonNull(f)) {
            if (f.delete()) return;
        }
    }

    @Test
    void testXMLValid() {
        File f = new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_202102122031.xml"))
            .getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(contributionsFile).isNotNull();
        var contributions = contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contributions.getFlag()).isEqualTo("update");
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);
    }

    @Test
    void testMultipleContributions() {
        File f = new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("contributions/multiple_contributions.xml")).getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(16);
    }

    @Test
    void testFieldMappingForCSV() {
        //MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date
        File f = new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("contributions/report_values_filled.xml")).getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);

        var contributions = contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contributions.getMaatId()).isEqualTo(5635978);
        softly.assertThat(contributions.getFlag()).isEqualTo("update");
        softly.assertThat(contributions.getAssessment().getEffectiveDate().toString()).isEqualTo("2021-01-30");
        softly.assertThat(contributions.getCcOutcomes().getCcOutcome().get(0).getDate().toString()).isEqualTo("2021-01-25");
        softly.assertThat(contributions.getCorrespondence().getLetter().get(0).getCreated().toString()).isEqualTo("2021-02-12");
        softly.assertThat(contributions.getApplication().getRepStatusDate().toString()).isEqualTo("2021-01-25");
        softly.assertThat(contributions.getApplication().getCcHardship().getReviewDate().toString()).isEqualTo("2020-05-05");
        softly.assertThat(contributions.getPassported().getDateCompleted().toString()).isEqualTo("2020-02-12");
    }

    @Test
    void testInvalidXML() {
        File f = new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("contributions/invalid.XML")).getFile());
        assertThrows(UnmarshalException.class, () -> contributionsFileMapper.mapContributionsXMLFileToObject(f));
    }

    @Test
    void testStringConversion() {
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXmlStringToObject(getXMLString());
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);
    }

    @Test
    void testProcessRequest() {
        try {
            LocalDate startDate = getDate("01-01-2020");
            LocalDate endDate = getDate("01-01-2023");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(), anyString(), any(), any(),
                anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString()}, "Test", startDate, endDate, filename);
            softly.assertThat(csvFile).isNotNull();
            softly.assertThat(csvFile.getName()).isEqualTo(filename);
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    @Test
    void testProcessRequestTooNew() {
        try {
            LocalDate startDate = getDate("01-01-2010");
            LocalDate endDate = getDate("01-01-2011");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(), anyString(), any(), any(),
                anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString()}, "Test", startDate, endDate, filename);
            softly.assertThat(csvFile).isNotNull();
            softly.assertThat(csvFile.getName()).isEqualTo(filename);
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    private LocalDate getDate(String date) {
        return LocalDate.parse(date, dateFormat);
    }

    @Test
    void testProcessRequestTooOld() {
        try {
            LocalDate startDate = getDate("01-01-2025");
            LocalDate endDate = getDate("01-01-2025");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(), anyString(), any(), any(),
                anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString()}, "Test", startDate, endDate, filename);
            softly.assertThat(csvFile).isNotNull();
            softly.assertThat(csvFile.getName()).isEqualTo(filename);
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    @Test
    void testProcessRequestFileGeneration() {
        try {
            LocalDate startDate = getDate("01-01-2021");
            LocalDate endDate = getDate("02-02-2021");
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString()}, "Test", startDate, endDate, filename);

            softly.assertThat(csvFile).isNotNull();
            String csvOutput = Files.readString(csvFile.toPath());
            // check header present
            softly.assertThat(csvOutput).contains(EXPECTED_HEADER);
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021");
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    @Test
    void testProcessRequestFileDateOutOfRangeGeneration() {
        try {
            LocalDate startDate = getDate("01-01-2022");
            LocalDate endDate = getDate("02-02-2022");
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString()},"Test", startDate, endDate, filename);

            softly.assertThat(csvFile).isNotNull();
            String csvOutput = Files.readString(csvFile.toPath());
            // check header present
            softly.assertThat(csvOutput).contains(EXPECTED_HEADER);
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,,,,,,,12/02/2021");
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    @Test
    void testProcessMultipleRequestFileGeneration() {
        try {
            LocalDate startDate = getDate("01-01-2021");
            LocalDate endDate = getDate("02-02-2021");
            csvFile = contributionsFileMapper.processRequest(new String[]{getXMLString(),getXMLString()},"Test", startDate, endDate, filename);

            softly.assertThat(csvFile).isNotNull();
            String csvOutput = Files.readString(csvFile.toPath());
            //String csvOutput = io.sentry.util.FileUtils.readText(csvFile);
            // check header present
            softly.assertThat(csvOutput).contains(EXPECTED_HEADER);
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,");

            String expectedTitle = String.format(EXPECTED_TITLE, startDate, endDate, LocalDate.now());
            softly.assertThat(csvOutput).isEqualTo(expectedTitle + EXPECTED_HEADER  +
                    "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021" + System.lineSeparator() +
                    "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021" + System.lineSeparator());
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
    }

    /**
     * Contribution List Tests
     **/

    @Test
    void givenContributionListNull_whenCallingProcessRequest_ShouldReturnFileWithHeaderAndNoDataMessage()
            throws JAXBException, IOException {
        String sourceData = getXmlDataContributionsList(true);
        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceData
        );
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceData },
           "Test",
                testDate,
                testDate,
                filename
        );

        String csvOutput = Files.readString(csvFile.toPath());
        // verify only title and header are present
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).isEqualTo(expectedTitle + EXPECTED_HEADER + NO_DATA_MESSAGE);
    }

    @Test
    void givenContributionsListEmpty_whenCallingProcessRequest_ShouldReturnFileWithHeaderAndNoDataMessage()
            throws JAXBException, IOException {
        String sourceData = getXmlDataContributionsList(false);
        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceData
        );
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST()).isNotNull();
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()).isNotNull();
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()).isEmpty();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String csvOutput = Files.readString(csvFile.toPath());
        // verify only title and header are present
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).isEqualTo(expectedTitle + EXPECTED_HEADER + NO_DATA_MESSAGE);
    }

    /**
     *  Assessment Date Tests
     **/

    @Test
    void givenAssessmentNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getAssessment()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify title, header and content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenAssessmentDateNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);

        softly.assertThat(contribution.getAssessment()).isNotNull();
        softly.assertThat(contribution.getAssessment().getEffectiveDate()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenAssessmentDate_whenCallingProcessRequest_ShouldReturnCsvLineWithAssessmentDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(false, false);

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,%s,,,,,,%s",
                testDate.format(dateFormatterCsv),
                testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    /**
     * Correspondence Date Tests
     **/

    @Test
    void givenCorrespondenceNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCorrespondenceDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getCorrespondence()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenCorrespondenceDateNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCorrespondenceDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);

        softly.assertThat(contribution.getCorrespondence()).isNotNull();
        softly.assertThat(contribution.getCorrespondence().getLetter()).isEmpty();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenCorrespondenceDate_whenCallingProcessRequest_ShouldReturnCsvLineWithCorrespondenceDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCorrespondenceDate(false, false);

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,%s,,,,%s",
                testDate.format(dateFormatterCsv),
                testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    /**
     * Application Date Tests
     **/

    @Test
    void givenApplicationIsNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );
        String csvOutput = Files.readString(csvFile.toPath());


        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenApplicationRepStatusDateIsNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication()).isNotNull();
        softly.assertThat(contribution.getApplication().getRepStatusDate()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );
        String csvOutput = Files.readString(csvFile.toPath());

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenApplicationDate_whenCallingProcessRequest_ShouldReturnCsvLineWithApplicationDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationDate(false, false);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication().getRepStatusDate()).isNotNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );
        String csvOutput = Files.readString(csvFile.toPath());

        String expectedCsvLine = String.format("5635978,update,,,,%s,,,%s", testDate.format(dateFormatterCsv), testDate.format(dateFormatterCsv));
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenApplicationHardshipIsNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationCcHardshipDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication().getCcHardship()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenApplicationHardshipReviewDateIsNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationCcHardshipDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication().getCcHardship()).isNotNull();
        softly.assertThat(contribution.getApplication().getCcHardship().getReviewDate()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", LocalDate.now().format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenApplicationHardshipDate_whenCallingProcessRequest_ShouldReturnCsvLineHardshipDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataApplicationCcHardshipDate(false, false);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getApplication().getCcHardship()).isNotNull();
        softly.assertThat(contribution.getApplication().getCcHardship().getReviewDate()).isNotNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );
        String csvOutput = Files.readString(csvFile.toPath());

        String expectedCsvLine = String.format("5635978,update,,,,,%s,,%s", testDate.format(dateFormatterCsv), testDate.format(dateFormatterCsv));
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    /**
     * CC Outcome Dates Tests
     **/

    @Test
    void givenCcOutcomesNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCcOutcomeDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getCcOutcomes()).isNull();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenCcOutcomeDateNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCcOutcomeDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);

        softly.assertThat(contribution.getCcOutcomes()).isNotNull();
        softly.assertThat(contribution.getCcOutcomes().getCcOutcome()).isEmpty();

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }

    @Test
    void givenCcOutcomeDate_whenCallingProcessRequest_ShouldReturnCsvLineWithCorrespondenceDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataCcOutcomeDate(false, false);

        LocalDate testDate = LocalDate.now();
        csvFile = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
            "Test",
                testDate,
                testDate,
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,%s,,,,,%s",
                testDate.format(dateFormatterCsv),
                testDate.format(dateFormatterCsv));

        String csvOutput = Files.readString(csvFile.toPath());
        // verify file content
        String expectedTitle = String.format(EXPECTED_TITLE, testDate, testDate, LocalDate.now());
        softly.assertThat(csvOutput).startsWith(expectedTitle + EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
    }


    /**
     * Test data providers
     **/

    private String getXMLString() {
        return "<?xml version=\"1.0\"?><contribution_file>    <header id=\"222772044\">        <filename>CONTRIBUTIONS_202102122031.xml</filename>        <dateGenerated>2021-02-12</dateGenerated>        <recordCount>1</recordCount>        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>    </header>    <CONTRIBUTIONS_LIST>        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">            <maat_id>5635978</maat_id>            <applicant id=\"222767510\">                <firstName>F Name</firstName>                <lastName>L Name</lastName>                <dob>1990-04-07</dob>                <preferredPaymentDay>1</preferredPaymentDay>                <noFixedAbode>no</noFixedAbode>                <specialInvestigation>no</specialInvestigation>                <homeAddress>                    <detail>                        <line1>102 Petty France</line1>                        <line2/>                        <line3/>                        <city/>                        <country/>                        <postcode/>                    </detail>                </homeAddress>                <postalAddress>                    <detail>                        <line1>SW1H 9EA</line1>                        <line2>SW1H 9EA</line2>                        <line3>SW1H 9EA</line3>                        <city/>                        <country/>                        <postcode>SW1H 9EA</postcode>                    </detail>                </postalAddress>                <employmentStatus>                    <code>SELF</code>                    <description>Self Employed</description>                </employmentStatus>                <disabilitySummary>                    <declaration>NOT_STATED</declaration>                </disabilitySummary>            </applicant>            <application>                <offenceType>                    <code>MURDER</code>                    <description>A-Homicide &amp; grave offences</description>                </offenceType>                <caseType>                    <code>EITHER WAY</code>                    <description>Either-Way</description>                </caseType>                <repStatus>                    <status>CURR</status>                    <description>Current</description>                </repStatus>                <magsCourt>                    <court>246</court>                    <description>Aberdare</description>                </magsCourt>                <repStatusDate>2021-01-25</repStatusDate><ccHardship><reviewDate>2020-05-05</reviewDate><reviewResult></reviewResult></ccHardship>                <arrestSummonsNumber>2011999999999999ASND</arrestSummonsNumber>                <inCourtCustody>no</inCourtCustody>                <imprisoned>no</imprisoned>                <repOrderWithdrawalDate>2021-01-29</repOrderWithdrawalDate>                <committalDate>2020-09-15</committalDate>                <solicitor>                    <accountCode>0D088G</accountCode>                    <name>MERRY &amp; CO</name>                </solicitor>            </application>            <assessment>                <effectiveDate>2021-01-30</effectiveDate>                <monthlyContribution>0</monthlyContribution>                <upfrontContribution>0</upfrontContribution>                <incomeContributionCap>185806</incomeContributionCap>                <assessmentReason>                    <code>PAI</code>                    <description>Previous Assessment was Incorrect</description>                </assessmentReason>                <assessmentDate>2021-02-12</assessmentDate>                <incomeEvidenceList>                    <incomeEvidence>                        <evidence>ACCOUNTS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>BANK STATEMENT</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>CASH BOOK</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>NINO</evidence>                        <mandatory>yes</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>OTHER BUSINESS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>TAX RETURN</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                </incomeEvidenceList>                <sufficientDeclaredEquity>no</sufficientDeclaredEquity>                <sufficientVerifiedEquity>no</sufficientVerifiedEquity>                <sufficientCapitalandEquity>no</sufficientCapitalandEquity>            </assessment>            <passported><date_completed>2020-02-12</date_completed></passported>            <equity/>            <capitalSummary>                <noCapitalDeclared>no</noCapitalDeclared>            </capitalSummary>            <ccOutcomes>                <ccOutcome>                    <code>CONVICTED</code>                    <date>2021-01-25</date>                </ccOutcome>            </ccOutcomes>            <correspondence>                <letter>                    <Ref>W1</Ref>                    <id>222771991</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222771938</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770074</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769497</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769466</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769440</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769528</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770104</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769803</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770161</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770044</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769886</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769831</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769774</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769652</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769589</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769562</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed>2021-01-30</printed>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769959</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769931</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769745</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769716</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769987</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770015</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770132</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>T2</Ref>                    <id>222767525</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-25</created>                    <printed>2021-01-25</printed>                </letter>            </correspondence>            <breathingSpaceInfo/>        </CONTRIBUTIONS>    </CONTRIBUTIONS_LIST></contribution_file>";
    }

    private String getXmlDataContributionsList(boolean isNull) {
        String tagSuffix = (isNull) ? "Null" : "";
        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>2021-02-12</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST%s>
                            </CONTRIBUTIONS_LIST%s>
                        </contribution_file>""",
                tagSuffix, tagSuffix);
    }

    private String getXmlDataAssessmentDate(boolean isNull, boolean isNullDate) {
        String tagSuffix = (isNull) ? "Null" : "";
        String dateTagSuffix = (isNullDate) ? "Null" : "";

        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>%s</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST>
                                <CONTRIBUTIONS id="222769650" flag="update">
                                    <maat_id>5635978</maat_id>
                                    <assessment%s>
                                        <effectiveDate%s>%s</effectiveDate%s>
                                    </assessment%s>
                                </CONTRIBUTIONS>
                            </CONTRIBUTIONS_LIST>
                        </contribution_file>
                        """,
                LocalDate.now().format(dateFormatterXml),
                tagSuffix,
                dateTagSuffix,
                LocalDate.now().format(dateFormatterXml),
                dateTagSuffix,
                tagSuffix);
    }

    private String getXmlDataCorrespondenceDate(boolean isNullTag, boolean isNullDate) {
        String tagSuffix = (isNullTag) ? "Null" : "";
        String dateTagSuffix = (isNullDate) ? "Null" : "";

        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>%s</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST>
                                <CONTRIBUTIONS id="222769650" flag="update">
                                    <maat_id>5635978</maat_id>
                                    <correspondence%s>
                                        <letter%s> <created>%s</created> </letter%s>
                                    </correspondence%s>
                                </CONTRIBUTIONS>
                            </CONTRIBUTIONS_LIST>
                        </contribution_file>
                        """,
                LocalDate.now().format(dateFormatterXml),
                tagSuffix,
                dateTagSuffix,
                LocalDate.now().format(dateFormatterXml),
                dateTagSuffix,
                tagSuffix);
    }

    private String getXmlDataApplicationDate(boolean isNull, boolean isNullDate) {
        String tagSuffix = (isNull) ? "Null" : "";
        String dateTagSuffix = (isNullDate) ? "Null" : "";

        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>%s</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST>
                                <CONTRIBUTIONS id="222769650" flag="update">
                                    <maat_id>5635978</maat_id>
                                    <application%s>
                                        <repStatusDate%s>%s</repStatusDate%s>
                                    </application%s>
                                </CONTRIBUTIONS>
                            </CONTRIBUTIONS_LIST>
                        </contribution_file>
                        """,
                LocalDate.now().format(dateFormatterXml),
                tagSuffix,
                dateTagSuffix,
                LocalDate.now().format(dateFormatterXml),
                dateTagSuffix,
                tagSuffix);
    }
    private String getXmlDataApplicationCcHardshipDate(boolean isNull, boolean isNullDate) {
        String innerTagSuffix = (isNull) ? "Null" : "";
        String dateTagSuffix = (isNullDate) ? "Null" : "";

        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>%s</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST>
                                <CONTRIBUTIONS id="222769650" flag="update">
                                    <maat_id>5635978</maat_id>
                                    <application>
                                        <ccHardship%s>
                                            <reviewDate%s>%s</reviewDate%s>
                                        </ccHardship%s>
                                    </application>
                                </CONTRIBUTIONS>
                            </CONTRIBUTIONS_LIST>
                        </contribution_file>
                        """,
                LocalDate.now().format(dateFormatterXml),
                innerTagSuffix,
                dateTagSuffix,
                LocalDate.now().format(dateFormatterXml),
                dateTagSuffix,
                innerTagSuffix);
    }

    private String getXmlDataCcOutcomeDate(boolean isNullTag, boolean isNullDate) {
        String tagSuffix = (isNullTag) ? "Null" : "";
        String dateTagSuffix = (isNullDate) ? "Null" : "";

        return String.format("""
                        <?xml version="1.0"?>
                        <contribution_file>
                            <header id="222772044">
                                <filename>CONTRIBUTIONS_202102122031.xml</filename>
                                <dateGenerated>%s</dateGenerated>
                                <recordCount>1</recordCount>
                                <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>
                            </header>
                            <CONTRIBUTIONS_LIST>
                                <CONTRIBUTIONS id="222769650" flag="update">
                                    <maat_id>5635978</maat_id>
                                    <ccOutcomes%s>
                                        <ccOutcome%s> <date>%s</date> </ccOutcome%s>
                                    </ccOutcomes%s>
                                </CONTRIBUTIONS>
                            </CONTRIBUTIONS_LIST>
                        </contribution_file>
                        """,
                LocalDate.now().format(dateFormatterXml),
                tagSuffix,
                dateTagSuffix,
                LocalDate.now().format(dateFormatterXml),
                dateTagSuffix,
                tagSuffix);
    }

}
