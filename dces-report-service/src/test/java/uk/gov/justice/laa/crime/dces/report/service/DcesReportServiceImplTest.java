package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@SpringBootTest
class DcesReportServiceImplTest {

    @Autowired
    DcesReportServiceImpl dcesReportService;

    @MockBean
    FdcFilesService mockFdcService;

    @MockBean
    ContributionFilesService mockContributionsService;

    @Before
    void setup() {
        given(mockFdcService.getFiles(any(), any())).willReturn(List.of("<xml1>"));
        given(mockContributionsService.getFiles(any(), any())).willReturn(List.of("<xml1>"));
    }

    @Test
    void getFdcCollection() throws JAXBException, IOException {
        // setup
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // execute
        dcesReportService.getFdcReport(dateParam, dateParam);

        // assert
        Mockito.verify(mockFdcService, times(1)).getFiles(dateParam, dateParam);
        Mockito.verify(mockContributionsService, times(0)).getFiles(any(), any());
    }

    @Test
    void getInitialContributionsCollection() throws JAXBException, IOException {
        // setup
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // execute
        dcesReportService.getContributionsReport(dateParam, dateParam);

        // assert
        Mockito.verify(mockContributionsService, times(1)).getFiles(dateParam, dateParam);
        Mockito.verify(mockFdcService, times(0)).getFiles(any(), any());
    }
}