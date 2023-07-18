package uk.gov.justice.laa.crime.dces.report.mapper;

import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

@Ignore
@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class FdcFileMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private FdcFileMapper fdcFileMapper;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String filename = "this_is_a_test.xml";

    @Test
    void testXMLValid(){
        File f = new File(getClass().getClassLoader().getResource("fdc/multiple_fdc.xml").getFile());
        FdcFile fdcFile = null;
        try {
            fdcFile = fdcFileMapper.mapFdcXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        }
        softly.assertThat(fdcFile).isNotNull();
        var fdcOutput = fdcFile.getFdcList().getFdc().get(0);
//        softly.assertThat(contributions.getFlag()).isEqualTo("update");
//        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()).isEqualTo(1);
        softly.assertAll();
    }
//
//    @Test
//    void testMultipleFdcEntries(){
//        File f = new File(getClass().getClassLoader().getResource("contributions/multiple_contributions.xml").getFile());
//        FdcFile fdcFile = null;
//        try {
//            fdcFile = fdcFileMapper.mapFdcXMLFileToObject(f);
//        } catch (JAXBException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(16);
//        softly.assertAll();
//    }
//
//    @Test
//    void testFieldMappingForCSV(){
//        File f = new File(getClass().getClassLoader().getResource("contributions/report_values_filled.xml").getFile());
//        FdcFile fdcFile = null;
//        try {
//            fdcFile = fdcFileMapper.mapFdcXMLFileToObject(f);
//        } catch (JAXBException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(1);
//
//        var contributions = fdcFile.getFdcList().getFdc().get(0);
//        softly.assertThat(contributions.getMaatId()).isEqualTo(5635978L);
//        softly.assertThat(contributions.getAgfsTotal()).isEqualTo(new BigDecimal(1));
//        softly.assertThat(contributions.getId()).isEqualTo(1L);
//        softly.assertThat(contributions.getFinalCost()).isEqualTo(1L);
//        softly.assertThat(contributions.getLgfsTotal()).isEqualTo(1L);
//        softly.assertThat(contributions.getCalculationDate().toString()).isEqualTo("2020-02-12");
//        softly.assertThat(contributions.getSentenceDate().toString()).isEqualTo("2020-02-12");
//        softly.assertAll();
//    }
//
//    @Test
//    void testFileNotFound(){
//        var filePath = getClass().getClassLoader().getResource("fdc/FileNotFound.xml");
//        assertThrows(RuntimeException.class, () -> {new File(filePath.getFile());});
//    }
//
//    @Test
//    void testInvalidXML(){
//        File f = new File(getClass().getClassLoader().getResource("fdc/invalid.XML").getFile());
//        assertThrows(UnmarshalException.class, () -> {fdcFileMapper.mapFdcXMLFileToObject(f);});
//    }
//
//
//
//    @Test
//    void testStringConversion(){
//
//        FdcFile fdcFile = null;
//        try {
//            fdcFile = fdcFileMapper.mapFdcXmlStringToObject(getXMLString());
//        } catch (JAXBException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(1);
//    }
//
//    private String getXMLString(){
//        return "<?xml version=\"1.0\"?><contribution_file>    <header id=\"222772044\">        <filename>CONTRIBUTIONS_202102122031.xml</filename>        <dateGenerated>2021-02-12</dateGenerated>        <recordCount>1</recordCount>        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>    </header>    <CONTRIBUTIONS_LIST>        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">            <maat_id>5635978</maat_id>            <applicant id=\"222767510\">                <firstName>F Name</firstName>                <lastName>L Name</lastName>                <dob>1990-04-07</dob>                <preferredPaymentDay>1</preferredPaymentDay>                <noFixedAbode>no</noFixedAbode>                <specialInvestigation>no</specialInvestigation>                <homeAddress>                    <detail>                        <line1>102 Petty France</line1>                        <line2/>                        <line3/>                        <city/>                        <country/>                        <postcode/>                    </detail>                </homeAddress>                <postalAddress>                    <detail>                        <line1>SW1H 9EA</line1>                        <line2>SW1H 9EA</line2>                        <line3>SW1H 9EA</line3>                        <city/>                        <country/>                        <postcode>SW1H 9EA</postcode>                    </detail>                </postalAddress>                <employmentStatus>                    <code>SELF</code>                    <description>Self Employed</description>                </employmentStatus>                <disabilitySummary>                    <declaration>NOT_STATED</declaration>                </disabilitySummary>            </applicant>            <application>                <offenceType>                    <code>MURDER</code>                    <description>A-Homicide &amp; grave offences</description>                </offenceType>                <caseType>                    <code>EITHER WAY</code>                    <description>Either-Way</description>                </caseType>                <repStatus>                    <status>CURR</status>                    <description>Current</description>                </repStatus>                <magsCourt>                    <court>246</court>                    <description>Aberdare</description>                </magsCourt>                <repStatusDate>2021-01-25</repStatusDate><ccHardship><reviewDate>2020-05-05</reviewDate><reviewResult></reviewResult></ccHardship>                <arrestSummonsNumber>2011999999999999ASND</arrestSummonsNumber>                <inCourtCustody>no</inCourtCustody>                <imprisoned>no</imprisoned>                <repOrderWithdrawalDate>2021-01-29</repOrderWithdrawalDate>                <committalDate>2020-09-15</committalDate>                <solicitor>                    <accountCode>0D088G</accountCode>                    <name>MERRY &amp; CO</name>                </solicitor>            </application>            <assessment>                <effectiveDate>2021-01-30</effectiveDate>                <monthlyContribution>0</monthlyContribution>                <upfrontContribution>0</upfrontContribution>                <incomeContributionCap>185806</incomeContributionCap>                <assessmentReason>                    <code>PAI</code>                    <description>Previous Assessment was Incorrect</description>                </assessmentReason>                <assessmentDate>2021-02-12</assessmentDate>                <incomeEvidenceList>                    <incomeEvidence>                        <evidence>ACCOUNTS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>BANK STATEMENT</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>CASH BOOK</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>NINO</evidence>                        <mandatory>yes</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>OTHER BUSINESS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>TAX RETURN</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                </incomeEvidenceList>                <sufficientDeclaredEquity>no</sufficientDeclaredEquity>                <sufficientVerifiedEquity>no</sufficientVerifiedEquity>                <sufficientCapitalandEquity>no</sufficientCapitalandEquity>            </assessment>            <passported><date_completed>2020-02-12</date_completed></passported>            <equity/>            <capitalSummary>                <noCapitalDeclared>no</noCapitalDeclared>            </capitalSummary>            <ccOutcomes>                <ccOutcome>                    <code>CONVICTED</code>                    <date>2021-01-25</date>                </ccOutcome>            </ccOutcomes>            <correspondence>                <letter>                    <Ref>W1</Ref>                    <id>222771991</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222771938</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770074</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769497</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769466</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769440</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769528</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770104</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769803</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770161</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770044</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769886</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769831</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769774</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769652</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769589</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769562</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed>2021-01-30</printed>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769959</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769931</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769745</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769716</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769987</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770015</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770132</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>T2</Ref>                    <id>222767525</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-25</created>                    <printed>2021-01-25</printed>                </letter>            </correspondence>            <breathingSpaceInfo/>        </CONTRIBUTIONS>    </CONTRIBUTIONS_LIST></contribution_file>";
//    }
//
//    @Test
//    void testProcessRequest(){
//        FdcFile fdcFile = null;
//        try {
//            Date startDate = getDate("2020-01-01");
//            Date endDate = getDate("2023-01-01");
//            CSVFileService csvServiceMock = mock(CSVFileService.class);
//            when(csvServiceMock.writeFdcToCsv(any(),anyString())).thenReturn(new File(filename));
//            fdcFileMapper.csvFileService=csvServiceMock;
//            fdcFileMapper.processRequest(getXMLString(), startDate, endDate, filename);
//        } catch (JAXBException | IOException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//    }
//
//    @Test
//    void testProcessRequestTooNew(){
//        try {
//            Date startDate = getDate("2010-01-01");
//            Date endDate = getDate("2011-01-01");
//            CSVFileService csvServiceMock = mock(CSVFileService.class);
//            when(csvServiceMock.writeFdcToCsv(any(),anyString())).thenReturn(new File(filename));
//            fdcFileMapper.csvFileService=csvServiceMock;
//            fdcFileMapper.processRequest(getXMLString(), startDate, endDate, filename);
//        } catch (JAXBException | IOException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//    }
//
//    private Date getDate(String date){
//        try {
//            return dateFormat.parse(date);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void testProcessRequestTooOld(){
//        try {
//            Date startDate = getDate("2025-01-01");
//            Date endDate = getDate("2025-01-01");
//            CSVFileService csvServiceMock = mock(CSVFileService.class);
//            when(csvServiceMock.writeFdcToCsv(any(),anyString())).thenReturn(new File(filename));
//            fdcFileMapper.csvFileService=csvServiceMock;
//            fdcFileMapper.processRequest(getXMLString(), startDate, endDate, filename);
//        } catch (JAXBException | IOException e) {
//            fail("Exception occurred in mapping test:"+e.getMessage());
//        }
//    }

    @Test
    void testProcessRequestFileGeneration(){
        File input = new File(getClass().getClassLoader().getResource("fdc/multiple_fdc.xml").getFile());
        File f = null;
        try {
            f = fdcFileMapper.processRequest(new String[]{FileUtils.readText(input)}, filename);

            softly.assertThat(f).isNotNull();
            String csvOutput = FileUtils.readText(f);
            // check header present
            softly.assertThat(csvOutput).contains("MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST");
            // verify content has been mapped
            softly.assertThat(csvOutput).isEqualTo("MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST\n" +
                    "2525925,30/09/2016,22/12/2016,1774.4,1180.64,593.76\n" +
                    "2492027,04/02/2011,04/07/2018,1479.23,569.92,909.31\n" +
                    "5275089,19/08/2016,02/09/2016,2849.95,1497.6,1352.35\n" +
                    "5427879,23/08/2016,06/09/2016,2252.6,937.86,1314.74\n" +
                    "5438043,25/08/2016,19/12/2016,1969.47,1085.5,883.97\n" +
                    "4971278,14/10/2016,11/01/2017,3226.01,1327.99,1898.02");
            softly.assertAll();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:"+e.getMessage());
        } finally {
            if (Objects.nonNull(f)) { f.delete();}
        }
    }

}
