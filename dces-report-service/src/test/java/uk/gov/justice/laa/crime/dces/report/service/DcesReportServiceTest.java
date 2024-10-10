package uk.gov.justice.laa.crime.dces.report.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.client.ContributionFilesClient;
import uk.gov.justice.laa.crime.dces.report.client.FdcFilesClient;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static uk.gov.justice.laa.crime.dces.report.scheduler.DcesReportScheduler.ReportPeriod.Monthly;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
@WireMockTest(httpPort = 1111)
class DcesReportServiceTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Autowired
    DcesReportService dcesReportService;

    @MockBean
    FdcFilesClient fdcFilesClient;

    @MockBean
    ContributionFilesClient contributionFilesClient;

    @MockBean
    NotificationClient notifyClient;

    @AfterEach
    void postTest() {
        softly.assertAll();
    }

    void setupMockitoForTest() {
        given(fdcFilesClient.getContributions(any(), any())).willReturn(List.of("<?xml version=\"1.0\"?><fdc_file>    <header file_id=\"222637370\">        <filename>FDC_201807251354.xml</filename>        <dateGenerated>2018-07-25</dateGenerated>        <recordCount>6260</recordCount>    </header>    <fdc_list>        <fdc id=\"27783002\">            <maat_id>2525925</maat_id>            <sentenceDate>2016-09-30</sentenceDate>            <calculationDate>2016-12-22</calculationDate>            <final_cost>1774.4</final_cost>            <lgfs_total>1180.64</lgfs_total>            <agfs_total>593.76</agfs_total>        </fdc>    </fdc_list></fdc_file>"));
        given(contributionFilesClient.getContributions(any(), any())).willReturn(List.of("<?xml version=\"1.0\"?><contribution_file>    <header id=\"222772044\">        <filename>CONTRIBUTIONS_202102122031.xml</filename>        <dateGenerated>2021-02-12</dateGenerated>        <recordCount>1</recordCount>        <formatVersion>format version 1.7 - xsd=contribution_file.xsd version 1.16</formatVersion>    </header>    <CONTRIBUTIONS_LIST>        <CONTRIBUTIONS id=\"222769650\" flag=\"update\">            <maat_id>5635978</maat_id>            <applicant id=\"222767510\">                <firstName>F Name</firstName>                <lastName>L Name</lastName>                <dob>1990-04-07</dob>                <preferredPaymentDay>1</preferredPaymentDay>                <noFixedAbode>no</noFixedAbode>                <specialInvestigation>no</specialInvestigation>                <homeAddress>                    <detail>                        <line1>102 Petty France</line1>                        <line2/>                        <line3/>                        <city/>                        <country/>                        <postcode/>                    </detail>                </homeAddress>                <postalAddress>                    <detail>                        <line1>SW1H 9EA</line1>                        <line2>SW1H 9EA</line2>                        <line3>SW1H 9EA</line3>                        <city/>                        <country/>                        <postcode>SW1H 9EA</postcode>                    </detail>                </postalAddress>                <employmentStatus>                    <code>SELF</code>                    <description>Self Employed</description>                </employmentStatus>                <disabilitySummary>                    <declaration>NOT_STATED</declaration>                </disabilitySummary>            </applicant>            <application>                <offenceType>                    <code>MURDER</code>                    <description>A-Homicide &amp; grave offences</description>                </offenceType>                <caseType>                    <code>EITHER WAY</code>                    <description>Either-Way</description>                </caseType>                <repStatus>                    <status>CURR</status>                    <description>Current</description>                </repStatus>                <magsCourt>                    <court>246</court>                    <description>Aberdare</description>                </magsCourt>                <repStatusDate>2021-01-25</repStatusDate><ccHardship><reviewDate>2020-05-05</reviewDate><reviewResult></reviewResult></ccHardship>                <arrestSummonsNumber>2011999999999999ASND</arrestSummonsNumber>                <inCourtCustody>no</inCourtCustody>                <imprisoned>no</imprisoned>                <repOrderWithdrawalDate>2021-01-29</repOrderWithdrawalDate>                <committalDate>2020-09-15</committalDate>                <solicitor>                    <accountCode>0D088G</accountCode>                    <name>MERRY &amp; CO</name>                </solicitor>            </application>            <assessment>                <effectiveDate>2021-01-30</effectiveDate>                <monthlyContribution>0</monthlyContribution>                <upfrontContribution>0</upfrontContribution>                <incomeContributionCap>185806</incomeContributionCap>                <assessmentReason>                    <code>PAI</code>                    <description>Previous Assessment was Incorrect</description>                </assessmentReason>                <assessmentDate>2021-02-12</assessmentDate>                <incomeEvidenceList>                    <incomeEvidence>                        <evidence>ACCOUNTS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>BANK STATEMENT</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>CASH BOOK</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>NINO</evidence>                        <mandatory>yes</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>OTHER BUSINESS</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                    <incomeEvidence>                        <evidence>TAX RETURN</evidence>                        <mandatory>no</mandatory>                    </incomeEvidence>                </incomeEvidenceList>                <sufficientDeclaredEquity>no</sufficientDeclaredEquity>                <sufficientVerifiedEquity>no</sufficientVerifiedEquity>                <sufficientCapitalandEquity>no</sufficientCapitalandEquity>            </assessment>            <passported><date_completed>2020-02-12</date_completed></passported>            <equity/>            <capitalSummary>                <noCapitalDeclared>no</noCapitalDeclared>            </capitalSummary>            <ccOutcomes>                <ccOutcome>                    <code>CONVICTED</code>                    <date>2021-01-25</date>                </ccOutcome>            </ccOutcomes>            <correspondence>                <letter>                    <Ref>W1</Ref>                    <id>222771991</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222771938</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-02-12</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770074</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769497</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769466</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769440</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-29</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769528</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770104</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769803</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770161</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770044</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769886</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769831</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769774</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769652</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769589</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769562</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed>2021-01-30</printed>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769959</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769931</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769745</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769716</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-30</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222769987</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770015</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>W1</Ref>                    <id>222770132</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-31</created>                    <printed/>                </letter>                <letter>                    <Ref>T2</Ref>                    <id>222767525</id>                    <type>CONTRIBUTION_NOTICE</type>                    <created>2021-01-25</created>                    <printed>2021-01-25</printed>                </letter>            </correspondence>            <breathingSpaceInfo/>        </CONTRIBUTIONS>    </CONTRIBUTIONS_LIST></contribution_file>"));
    }

    @Test
    void getFdcCollection() throws JAXBException, IOException, NotificationClientException {
        // setup
        setupMockitoForTest();
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // execute
        dcesReportService.sendFdcReport(Monthly);

        // assert
        Mockito.verify(fdcFilesClient, times(1)).getContributions(dateParam, dateParam);
        Mockito.verify(contributionFilesClient, times(0)).getContributions(any(), any());
    }

    @Test
    void getInitialContributionsCollection() throws JAXBException, IOException, NotificationClientException {
        // setup
        setupMockitoForTest();
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // execute
        dcesReportService.sendContributionsReport(Monthly);

        // assert
        Mockito.verify(contributionFilesClient, times(1)).getContributions(dateParam, dateParam);
        Mockito.verify(fdcFilesClient, times(0)).getContributions(any(), any());
    }

    @Test
    void sendEmailReport() throws NotificationClientException, IOException {
        dcesReportService.sendEmailReport(new File(getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_202102122031.xml").getFile()), "Full Defence Cost", Monthly, LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 10));
    }
}