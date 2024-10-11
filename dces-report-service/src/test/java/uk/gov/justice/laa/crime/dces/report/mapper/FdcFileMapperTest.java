package uk.gov.justice.laa.crime.dces.report.mapper;

import io.sentry.util.FileUtils;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class FdcFileMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;
    @Autowired
    private FdcFileMapper fdcFileMapper;

    private static final String filename = "this_is_a_test.xml";

    @AfterEach
    void resetCsvFileService() {
        fdcFileMapper.csvFileService = new CSVFileService();
    }

    @Test
    void testXMLFileMappingValid() {
        File f = new File(getClass().getClassLoader().getResource("fdc/single_fdc.xml").getFile());
        FdcFile fdcFile = null;
        try {
            fdcFile = fdcFileMapper.mapFdcXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(fdcFile).isNotNull();
        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(1);

        var fdcOutput = fdcFile.getFdcList().getFdc().get(0);
        softly.assertThat(fdcOutput.getMaatId()).isEqualTo(2525925);
        softly.assertThat(fdcOutput.getSentenceDate().toString()).isEqualTo("2016-09-30");
        softly.assertThat(fdcOutput.getCalculationDate().toString()).isEqualTo("2016-12-22");
        softly.assertThat(fdcOutput.getFinalCost()).isEqualTo(new BigDecimal("1774.4"));
        softly.assertThat(fdcOutput.getLgfsTotal()).isEqualTo(new BigDecimal("1180.64"));
        softly.assertThat(fdcOutput.getAgfsTotal()).isEqualTo(new BigDecimal("593.76"));
        softly.assertThat(fdcOutput.getId()).isEqualTo(27783002);

        softly.assertAll();
    }

    @Test
    void testXMLStringMappingValid() {
        FdcFile fdcFile = null;
        try {
            fdcFile = fdcFileMapper.mapFdcXmlStringToObject(getXMLString(true));
        } catch (JAXBException | IOException e) {
            fail("Unexpected Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(fdcFile).isNotNull();
        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(1);

        var fdcOutput = fdcFile.getFdcList().getFdc().get(0);
        softly.assertThat(fdcOutput.getMaatId()).isEqualTo(2525925);
        softly.assertThat(fdcOutput.getSentenceDate().toString()).isEqualTo("2016-09-30");
        softly.assertThat(fdcOutput.getCalculationDate().toString()).isEqualTo("2016-12-22");
        softly.assertThat(fdcOutput.getFinalCost()).isEqualTo(new BigDecimal("1774.4"));
        softly.assertThat(fdcOutput.getLgfsTotal()).isEqualTo(new BigDecimal("1180.64"));
        softly.assertThat(fdcOutput.getAgfsTotal()).isEqualTo(new BigDecimal("593.76"));
        softly.assertThat(fdcOutput.getId()).isEqualTo(27783002);

        softly.assertAll();
    }

    @Test
    void testMultipleFileFdcEntries() {
        File f = new File(getClass().getClassLoader().getResource("fdc/multiple_fdc.xml").getFile());
        FdcFile fdcFile = null;
        try {
            fdcFile = fdcFileMapper.mapFdcXMLFileToObject(f);
        } catch (JAXBException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(6);
        softly.assertAll();
    }

    @Test
    void testMultipleStringFdcEntries() {
        FdcFile fdcFile = null;
        try {
            fdcFile = fdcFileMapper.mapFdcXmlStringToObject(getXMLString(false));
        } catch (JAXBException | IOException e) {
            fail("Unexpected exception occurred in mapping test:" + e.getMessage());
        }
        softly.assertThat(fdcFile.getFdcList().getFdc().size()).isEqualTo(6);
        softly.assertAll();
    }

    @Test
    void testInvalidXML() {
        File f = new File(getClass().getClassLoader().getResource("fdc/invalid_fdc.xml").getFile());
        assertThrows(UnmarshalException.class, () -> {
            fdcFileMapper.mapFdcXMLFileToObject(f);
        });
    }

    String getXMLString(boolean wantsSingle) throws IOException {
        File f = new File(getClass().getClassLoader().getResource(wantsSingle ? "fdc/single_fdc.xml" : "fdc/multiple_fdc.xml").getFile());
        return FileUtils.readText(f);
    }

    @Test
    void testProcessRequest() {
        File f = null;
        try {
            CSVFileService csvServiceMock = mock(CSVFileService.class);
            when(csvServiceMock.writeFdcFileListToCsv(any(), anyString(), anyString(), any(), any())).thenReturn(new File(filename));
            fdcFileMapper.csvFileService = csvServiceMock;
            LocalDate date = LocalDate.now();
            f = fdcFileMapper.processRequest(new String[]{getXMLString(false)}, filename, "Test", date, date);
            softly.assertThat(f).isNotNull();
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        } finally {
            softly.assertAll();
            closeFile(f);
        }
    }

    @Test
    void testProcessRequestFileGeneration() {
        File input = new File(getClass().getClassLoader().getResource("fdc/multiple_fdc.xml").getFile());
        File f = null;
        try {
            LocalDate date = LocalDate.now();
            f = fdcFileMapper.processRequest(new String[]{FileUtils.readText(input)}, filename, "Test", date, date);

            softly.assertThat(f).isNotNull();
            String csvOutput = FileUtils.readText(f);
            // check header present
            softly.assertThat(csvOutput).contains("MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Transmission Date");
            // verify content has been mapped
            String title = String.format("Test Final Defence Cost Report REPORTING DATE FROM: %s | REPORTING DATE TO: %s | REPORTING PRODUCED ON: %s\n", date, date, date);
            softly.assertThat(csvOutput).isEqualTo(title +
                    "MAAT ID, Sentence Date, Calculation Date, Final Cost, LGFS Cost, AGFS COST, Transmission Date\n" +
                    "2525925,30/09/2016,22/12/2016,1774.40,1180.64,593.76,25/07/2018\n" +
                    "2492027,04/02/2011,04/07/2018,1479.23,569.92,909.31,25/07/2018\n" +
                    "5275089,19/08/2016,02/09/2016,2849.95,1497.60,1352.35,25/07/2018\n" +
                    "5427879,23/08/2016,06/09/2016,2252.60,937.86,1314.74,25/07/2018\n" +
                    "5438043,25/08/2016,19/12/2016,1969.47,1085.50,883.97,25/07/2018\n" +
                    "4971278,14/10/2016,11/01/2017,3226.01,1327.99,1898.02,25/07/2018");
        } catch (JAXBException | IOException e) {
            fail("Exception occurred in mapping test:" + e.getMessage());
        } finally {
            softly.assertAll();
            closeFile(f);
        }
    }

    private void closeFile(File f) {
        if (Objects.nonNull(f)) {
            f.delete();
        }
    }


}
