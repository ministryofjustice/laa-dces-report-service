package uk.gov.justice.laa.crime.dces.report.mapper;

import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    private static final String EXPECTED_HEADER = "MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date";
    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private ContributionsFileMapper contributionsFileMapper;

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String filename = "this_is_a_test.xml";

    @AfterEach
    void resetCsvFileService(){
        contributionsFileMapper.csvFileService = new CSVFileService();
    }

    @Test
    void testXMLValid(){
        File f = new File(getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_202102122031.xml").getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        }
        softly.assertThat(contributionsFile).isNotNull();
        var contributions = contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contributions.getFlag()).isEqualTo("update");
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    void testMultipleContributions(){
        File f = new File(getClass().getClassLoader().getResource("contributions/multiple_contributions.xml").getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        }
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(16);
        softly.assertAll();
    }

    @Test
    void testFieldMappingForCSV(){
        //MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date
        File f = new File(getClass().getClassLoader().getResource("contributions/report_values_filled.xml").getFile());
        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
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
        softly.assertAll();
    }

    @Test
    void testInvalidXML(){
        File f = new File(getClass().getClassLoader().getResource("contributions/invalid.XML").getFile());
        assertThrows(UnmarshalException.class, () -> contributionsFileMapper.mapContributionsXMLFileToObject(f));
    }

    @Test
    void testStringConversion(){

        ContributionFile contributionsFile = null;
        try {
            contributionsFile = contributionsFileMapper.mapContributionsXmlStringToObject(getXMLString());
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        }
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);
    }

    private String getXMLString(){
        return "<?xml version=\"1.0\"?><contribution_file>    <header id=\"222772044\">        <filename>CONTRIBUTIONS_202102122031.xml</filename>        <dateGenerated>2021-02-12</dateGenerated>        <recordCount>1</recordCount>        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>    </header>    <CONTRIBUTIONS_LIST>        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">            <maat_id>5635978</maat_id>            <applicant id=\"222767510\">                <firstName>F Name</firstName>                <lastName>L Name</lastName>                <dob>1990-04-07</dob>                <preferredPaymentDay>1</preferredPaymentDay>                <noFixedAbode>no</noFixedAbode>                <specialInvestigation>no</specialInvestigation>                <homeAddress>                    <detail>                        <line1>102 Petty France</line1>                        <line2/>                        <line3/>                        <city/>                        <country/>                        <postcode/>                    </detail>                </homeAddress>                <postalAddress>                    <detail>                        <line1>SW1H 9EA</line1>                        <line2>SW1H 9EA</line2>                        <line3>SW1H 9EA</line3>                        <city/>                        <country/>                        <postcode>SW1H 9EA</postcode>                    </detail>                </postalAddress>                <employmentStatus>                    <code>SELF</code>                    <description>Self Employed</description>                </employmentStatus>                <disabilitySummary>                    <declaration>NOT_STATED</declaration>                </disabilitySummary>            </applicant>            <application>                <offenceType>                    <code>MURDER</code>                    <description>A-Homicide &amp; grave offences</description>                </offenceType>                <caseType>                    <code>EITHER WAY</code>                    <description>Either-Way</description>                </caseType>                <repStatus>                    <status>CURR</status>                    <description>Current</description>                </repStatus>                <magsCourt>                    <court>246</court>                    <description>Aberdare</description>                </magsCourt>                <repStatusDate>2021-01-25</repStatusDate><ccHardship><reviewDate>2020-05-05</reviewDate><reviewResult></reviewResult></ccHardship>                <arrestSummonsNumber>2011999999999999ASND</arrestSummonsNumber>                <inCourtCustody>no</inCourtCustody>                <imprisoned>no</imprisoned>                <repOrderWithdrawalDate>2021-01-29</repOrderWithdrawalDate>                <committalDate>2020-09-15</committalDate>                <solicitor>                    <accountCode>0D088G</accountCode>                    <name>MERRY &amp; CO</name>                </solicitor>            </application>            <assessment>                <effectiveDate>2021-01-30</effectiveDate>                <monthlyContribution>0</monthlyContribution>                <upfrontContribution>0</upfrontContribution>                <incomeContributionCap>185806</incomeContributionCap>                <assessmentReason>                    <code>PAI</code>                    <description>Previous Assessment was Incorrect</description>                </assessmentReason>                <assessmentDate>2021-02-12</assessmentDate>                <incomeEvidenceList>                    <incomeEvidence>                        <evidence>ACCOUNTS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>BANK STATEMENT</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>CASH BOOK</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>NINO</evidence>                        <mandatory>yes</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>OTHER BUSINESS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>TAX RETURN</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                </incomeEvidenceList>                <sufficientDeclaredEquity>no</sufficientDeclaredEquity>                <sufficientVerifiedEquity>no</sufficientVerifiedEquity>                <sufficientCapitalandEquity>no</sufficientCapitalandEquity>            </assessment>            <passported><date_completed>2020-02-12</date_completed></passported>            <equity/>            <capitalSummary>                <noCapitalDeclared>no</noCapitalDeclared>            </capitalSummary>            <ccOutcomes>                <ccOutcome>                    <code>CONVICTED</code>                    <date>2021-01-25</date>                </ccOutcome>            </ccOutcomes>            <correspondence>                <letter>                    <Ref>W1</Ref>                    <id>222771991</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222771938</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770074</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769497</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769466</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769440</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769528</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770104</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769803</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770161</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770044</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769886</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769831</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769774</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769652</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769589</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769562</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed>2021-01-30</printed>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769959</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769931</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769745</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769716</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769987</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770015</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770132</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>T2</Ref>                    <id>222767525</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-25</created>                    <printed>2021-01-25</printed>                </letter>            </correspondence>            <breathingSpaceInfo/>        </CONTRIBUTIONS>    </CONTRIBUTIONS_LIST></contribution_file>";
    }

    @Test
    void testProcessRequest(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2020");
            LocalDate endDate = getDate("01-01-2023");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(),anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            f = contributionsFileMapper.processRequest(new String[]{getXMLString()}, startDate, endDate, filename);
            softly.assertThat(f).isNotNull();
            softly.assertThat(f.getName()).isEqualTo(filename);
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    @Test
    void testProcessRequestTooNew(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2010");
            LocalDate endDate = getDate("01-01-2011");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(),anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            f = contributionsFileMapper.processRequest(new String[]{getXMLString()}, startDate, endDate, filename);
            softly.assertThat(f).isNotNull();
            softly.assertThat(f.getName()).isEqualTo(filename);
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    private LocalDate getDate(String date){
        return LocalDate.parse(date, dateFormat);
    }

    @Test
    void testProcessRequestTooOld(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2025");
            LocalDate endDate = getDate("01-01-2025");
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeContributionToCsv(any(),anyString())).thenReturn(new File(filename));
            contributionsFileMapper.csvFileService=csvServiceMock;
            f = contributionsFileMapper.processRequest(new String[]{getXMLString()}, startDate, endDate, filename);
            softly.assertThat(f).isNotNull();
            softly.assertThat(f.getName()).isEqualTo(filename);
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    @Test
    void testProcessRequestFileGeneration(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2021");
            LocalDate endDate = getDate("02-02-2021");
            f = contributionsFileMapper.processRequest(new String[]{getXMLString()}, startDate, endDate, filename);

            softly.assertThat(f).isNotNull();
            String csvOutput = FileUtils.readText(f);
            // check header present
            softly.assertThat(csvOutput).contains("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date");
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021");
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    @Test
    void testProcessRequestFileDateOutOfRangeGeneration(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2022");
            LocalDate endDate = getDate("02-02-2022");
            f = contributionsFileMapper.processRequest(new String[]{getXMLString()}, startDate, endDate, filename);

            softly.assertThat(f).isNotNull();
            String csvOutput = FileUtils.readText(f);
            // check header present
            softly.assertThat(csvOutput).contains("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date");
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,,,,,,,12/02/2021");
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    @Test
    void testProcessMultipleRequestFileGeneration(){
        File f = null;
        try {
            LocalDate startDate = getDate("01-01-2021");
            LocalDate endDate = getDate("02-02-2021");
            f = contributionsFileMapper.processRequest(new String[]{getXMLString(),getXMLString()}, startDate, endDate, filename);

            softly.assertThat(f).isNotNull();
            String csvOutput = FileUtils.readText(f);
            // check header present
            softly.assertThat(csvOutput).contains("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date");
            // verify content has been mapped
            softly.assertThat(csvOutput).contains("5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,");
            softly.assertThat(csvOutput).isEqualTo("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date\n" +
                    "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021\n" +
                    "5635978,update,30/01/2021,25/01/2021,31/01/2021,25/01/2021,,,12/02/2021");
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            closeFile(f);
        }
    }

    @Test
    void givenContributionListIsNull_whenCallingProcessRequest_ShouldReturnFileOnlyWithHeader()
            throws JAXBException, IOException {
        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                getXmlDataContributionListNull()
        );
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST()).isNull();

        File f = contributionsFileMapper.processRequest(
                new String[]{ getXmlDataContributionListNull() },
                LocalDate.now(),
                LocalDate.now(),
                filename
        );

        String csvOutput = FileUtils.readText(f);
        // verify only header is present
        softly.assertThat(csvOutput).isEqualTo("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date");
        closeFile(f);
    }

    @Test
    void givenContributionsIsNull_whenCallingProcessRequest_ShouldReturnFileOnlyWithHeader()
            throws JAXBException, IOException {
        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                getXmlDataContributionsNull()
        );
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST()).isNotNull();
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()).isNotNull();
        softly.assertThat(fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()).isEmpty();

        File f = contributionsFileMapper.processRequest(
                new String[]{ getXmlDataContributionListNull() },
                LocalDate.now(),
                LocalDate.now(),
                filename
        );

        String csvOutput = FileUtils.readText(f);
        // verify only header is present
        softly.assertThat(csvOutput).isEqualTo("MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date,Transmission Date");
        closeFile(f);
    }

    @Test
    void givenContributionAssessmentNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(true, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contribution.getAssessment()).isNull();

        File f = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
                LocalDate.now(),
                LocalDate.now(),
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", LocalDate.now().format(dateFormatterCsv));

        String csvOutput = FileUtils.readText(f);
        // verify only header is present
        softly.assertThat(csvOutput).startsWith(EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
        closeFile(f);
    }

    @Test
    void givenContributionAssessmentDateNull_whenCallingProcessRequest_ShouldReturnCsvLineWithGeneratedDateOnly()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(false, true);

        ContributionFile fileToTest = contributionsFileMapper.mapContributionsXmlStringToObject(
                sourceXmlData
        );

        ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS contribution = fileToTest.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);

        softly.assertThat(contribution.getAssessment()).isNotNull();
        softly.assertThat(contribution.getAssessment().getEffectiveDate()).isNull();

        File f = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
                LocalDate.now(),
                LocalDate.now(),
                filename
        );

        String expectedCsvLine = String.format("5635978,update,,,,,,,%s", LocalDate.now().format(dateFormatterCsv));

        String csvOutput = FileUtils.readText(f);
        // verify only header is present
        softly.assertThat(csvOutput).startsWith(EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
        closeFile(f);
    }

    @Test
    void givenContributionAssessmentDate_whenCallingProcessRequest_ShouldReturnCsvLineWithAssessmentDate()
            throws JAXBException, IOException {
        String sourceXmlData = getXmlDataAssessmentDate(false, false);

        File f = contributionsFileMapper.processRequest(
                new String[]{ sourceXmlData },
                LocalDate.now(),
                LocalDate.now(),
                filename
        );

        String expectedCsvLine = String.format("5635978,update,%s,,,,,,%s",
                LocalDate.now().format(dateFormatterCsv),
                LocalDate.now().format(dateFormatterCsv));

        String csvOutput = FileUtils.readText(f);
        // verify only header is present
        softly.assertThat(csvOutput).startsWith(EXPECTED_HEADER);
        softly.assertThat(csvOutput).contains(expectedCsvLine);
        closeFile(f);
    }

    private void closeFile(File f){
        if(Objects.nonNull(f)){
            f.delete();
        }
    }

    private String getXmlDataContributionListNull() {
        return "<?xml version=\"1.0\"?>\n" +
                "<contribution_file>\n" +
                "    <header id=\"222772044\">\n" +
                "        <filename>CONTRIBUTIONS_202102122031.xml</filename>\n" +
                "        <dateGenerated>2021-02-12</dateGenerated>\n" +
                "        <recordCount>1</recordCount>\n" +
                "        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>\n" +
                "    </header>\n" +
                "</contribution_file>";
    }

    private String getXmlDataContributionsNull() {
        return "<?xml version=\"1.0\"?>\n" +
                "<contribution_file>\n" +
                "    <header id=\"222772044\">\n" +
                "        <filename>CONTRIBUTIONS_202102122031.xml</filename>\n" +
                "        <dateGenerated>2021-02-12</dateGenerated>\n" +
                "        <recordCount>1</recordCount>\n" +
                "        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>\n" +
                "    </header>\n" +
                "    <CONTRIBUTIONS_LIST>\n" +
                "    </CONTRIBUTIONS_LIST>\n" +
                "</contribution_file>";
    }

    private String getXmlDataAssessmentDate(boolean isNullAssessment, boolean isNullDate) {
        String assessmentTagSuffix = (isNullAssessment) ? "Null" : "";
        String assessmentDateSuffix = (isNullDate) ? "Null" : "";

        return String.format("<?xml version=\"1.0\"?>\n" +
                "<contribution_file>\n" +
                "    <header id=\"222772044\">\n" +
                "        <filename>CONTRIBUTIONS_202102122031.xml</filename>\n" +
                "        <dateGenerated>%s</dateGenerated>\n" +
                "        <recordCount>1</recordCount>\n" +
                "        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>\n" +
                "    </header>\n" +
                "    <CONTRIBUTIONS_LIST>\n" +
                "        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">\n" +
                "            <maat_id>5635978</maat_id>\n" +
                "            <assessment%s>\n" +
                "                <effectiveDate%s>%s</effectiveDate%s>\n" +
                "            </assessment%s>\n" +
                "        </CONTRIBUTIONS>\n" +
                "    </CONTRIBUTIONS_LIST>\n" +
                "</contribution_file>\n",
                LocalDate.now().format(dateFormatterXml),
                assessmentTagSuffix,
                assessmentDateSuffix,
                LocalDate.now().format(dateFormatterXml),
                assessmentDateSuffix,
                assessmentTagSuffix);
    }

    private String getXmlDataValidNull() {
        return "<?xml version=\"1.0\"?>\n" +
                "<contribution_file>\n" +
                "    <header id=\"222772044\">\n" +
                "        <filename>CONTRIBUTIONS_202102122031.xml</filename>\n" +
                "        <dateGenerated>2021-02-12</dateGenerated>\n" +
                "        <recordCount>1</recordCount>\n" +
                "        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>\n" +
                "    </header>\n" +
                "    <CONTRIBUTIONS_LIST>\n" +
                "        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">\n" +
                "            <maat_id>5635978</maat_id>\n" +
                "            <applicant id=\"222767510\">\n" +
                "                <firstName>F Name</firstName>\n" +
                "                <lastName>L Name</lastName>\n" +
                "                <dob>1990-04-07</dob>\n" +
                "                <preferredPaymentDay>1</preferredPaymentDay>\n" +
                "            </applicant>\n" +
                "            <application>\n" +
                "                <ccHardship>\n" +
                "                    <reviewDate>2021-01-25</reviewDate>\n" +
                "                </ccHardship>\n" +
                "                <repStatusDate>2021-01-25</repStatusDate>\n" +
                "                <repOrderWithdrawalDate>2021-01-29</repOrderWithdrawalDate>\n" +
                "                <committalDate>2020-09-15</committalDate>\n" +
                "            </application>\n" +
                "            <assessment>\n" +
                "                <effectiveDate>2021-01-30</effectiveDate>\n" +
                "                <assessmentDate>2021-02-12</assessmentDate>\n" +
                "            </assessment>\n" +
                "            <ccOutcomes>\n" +
                "                <ccOutcome>\n" +
                "                    <date>2021-01-25</date>\n" +
                "                </ccOutcome>\n" +
                "            </ccOutcomes>\n" +
                "            <passported>\n" +
                "                <dateCompleted>2021-01-30</dateCompleted>\n" +
                "            </passported>\n" +
                "            <correspondence>\n" +
                "                <letter>\n" +
                "                    <Ref>W1</Ref>\n" +
                "                    <id>222771991</id>\n" +
                "                    <type>CONTRIBUTION_NOTICE</type>\n" +
                "                    <created>2021-02-12</created>\n" +
                "                    <printed/>\n" +
                "                </letter>\n" +
                "                <letter>\n" +
                "                    <Ref>W1</Ref>\n" +
                "                    <id>222771938</id>\n" +
                "                    <type>CONTRIBUTION_NOTICE</type>\n" +
                "                    <created>2021-02-12</created>\n" +
                "                    <printed/>\n" +
                "                </letter>\n" +
                "            </correspondence>\n" +
                "            <breathingSpaceInfo/>\n" +
                "        </CONTRIBUTIONS>\n" +
                "    </CONTRIBUTIONS_LIST>\n" +
                "</contribution_file>\n";
    }

}
