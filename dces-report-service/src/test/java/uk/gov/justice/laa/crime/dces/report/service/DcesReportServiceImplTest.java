package uk.gov.justice.laa.crime.dces.report.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
    ContributionFilesService mockRecordsService;

    @Before
    void setup() {
        given(mockFdcService.getFiles(any(), any())).willReturn(List.of("<xml1>"));
    }

    @Test
    void getInitialContributionsCollection() {
        // setup
        LocalDate dateParam = LocalDate.of(2023, 7, 10);

        // assert
        Mockito.verify(mockFdcService, times(1)).getFiles(dateParam, dateParam);
        Mockito.verify(mockRecordsService, times(0)).getFiles(any(), any());
    }
}