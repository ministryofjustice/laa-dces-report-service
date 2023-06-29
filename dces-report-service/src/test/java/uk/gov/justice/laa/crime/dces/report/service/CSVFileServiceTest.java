package uk.gov.justice.laa.crime.dces.report.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
public class CSVFileServiceTest {


    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private CSVFileService CSVFileService;
    @MockBean
    FileWriter fw;

    @BeforeEach
    void setup(){

    }

    private List<CSVDataLine> buildTestContributionFile(){
        var contribution = new CSVDataLine();
        contribution.setMaatId("123456788");
        ArrayList<CSVDataLine> contributionList = new ArrayList<>();
        return contributionList;
    }


    @Test
    void testwriteLogicalObjectToCsv(){
        try {
            Mockito.when(fw.append(Mockito.anyString())).thenReturn(fw);
            CSVFileService.writeContributionToCsv(buildTestContributionFile(), "test.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
