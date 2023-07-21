package uk.gov.justice.laa.crime.dces.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService.ReportFileType;

import java.time.LocalDate;
import java.util.ArrayList;
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
    ContributionFilesService mockRecordsService;

    @BeforeEach
    void setup() {
        given(mockFdcService.getFiles(any(), any())).willReturn(new ArrayList<>());
    }

    @Test
    void getInitialContributionsCollection() {
        // setup
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // execute
        dcesReportService.getApiFiles(ReportFileType.FDC, dateParam, dateParam);

        // assert
        Mockito.verify(mockFdcService, times(1)).getFiles(dateParam, dateParam);
        Mockito.verify(mockRecordsService, times(0)).getFiles(any(), any());
    }
}