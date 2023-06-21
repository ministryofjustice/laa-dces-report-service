package uk.gov.justice.laa.crime.dces.report.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SoftAssertionsExtension.class)
public class ContributionsFileMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void testXMLValid(){
        File f = new File(getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_202102122031.xml").getFile());
        var contributionsFile = ContributionsFileMapper.mapContributionsXMLFileToObject(f);
        softly.assertThat(contributionsFile).isNotNull();
        var contributions = contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contributions.getFlag()).isEqualTo("update");
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()==1);
        softly.assertAll();
    }

    @Test
    void testMultipleContributions(){
        File f = new File(getClass().getClassLoader().getResource("contributions/MULTIPLE_CONTRIBUTIONS.xml").getFile());
        var contributionsFile = ContributionsFileMapper.mapContributionsXMLFileToObject(f);
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()==2);

        f = new File(getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_201809271933.xml").getFile());
        contributionsFile = ContributionsFileMapper.mapContributionsXMLFileToObject(f);
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()==16);
        softly.assertAll();
    }

    @Test
    void testFieldMappingForCSV(){
        //MAAT ID,Data Feed Type,Assessment Date,CC OutCome Date,Correspondence Sent Date,Rep Order Status Date,Hardship Review Date,Passported Date
        // TODO: Need to finish test once ccOutcome date, and CorrespondenceSentDate mappings are understood.
        File f = new File(getClass().getClassLoader().getResource("contributions/CONTRIBUTIONS_202102122031_ForReport.xml").getFile());
        var contributionsFile = ContributionsFileMapper.mapContributionsXMLFileToObject(f);
        softly.assertThat(contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().size()==1);

        var contributions = contributionsFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS().get(0);
        softly.assertThat(contributions.getMaatId()).isEqualTo(5635978);
        softly.assertThat(contributions.getFlag()).isEqualTo("update");
        softly.assertThat(contributions.getAssessment().getAssessmentDate().toString()).isEqualTo("2021-02-12");
        //cc outcome date
        //correspondence sent date
        softly.assertThat(contributions.getApplication().getRepStatusDate().toString()).isEqualTo("2021-01-25");
        softly.assertThat(contributions.getApplication().getCcHardship().getReviewDate().toString()).isEqualTo("2020-05-05");
        softly.assertThat(contributions.getPassported().getDateCompleted().toString()).isEqualTo("2020-02-12");
        softly.assertAll();
    }

    @Test
    void testFileNotFound(){
        var filePath = getClass().getClassLoader().getResource("contributions/FileNotFound.xml");
        assertThrows(RuntimeException.class, () -> {new File(filePath.getFile());});
    }

    @Test
    void testInvalidXML(){
        File f = new File(getClass().getClassLoader().getResource("contributions/invalid.XML").getFile());
        assertThrows(RuntimeException.class, () -> {ContributionsFileMapper.mapContributionsXMLFileToObject(f);});
    }

}
