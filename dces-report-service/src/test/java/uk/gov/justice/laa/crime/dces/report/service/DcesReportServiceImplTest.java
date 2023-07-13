package uk.gov.justice.laa.crime.dces.report.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService.ReportFileType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@SpringBootTest
class DcesReportServiceImplTest {

    @Autowired
    DcesReportServiceImpl dcesReportService;

    @MockBean
    FdcRecordsService mockFdcService;

    @MockBean
    ContributionRecordsService mockRecordsService;

    @Before
    void setup() {
        given(mockFdcService.getFiles(any(), any())).willReturn(new ContributionFilesResponse());
    }

    @Test
    void getInitialContributionsCollection() {
        LocalDate dateParam = LocalDate.of(2023, 7, 10);
        ContributionFilesResponse contributionsCollection = dcesReportService
                .getContributionsCollection(ReportFileType.FDC, dateParam, dateParam);
        Mockito.verify(mockFdcService, times(1)).getFiles(dateParam, dateParam);
        Mockito.verify(mockRecordsService, times(0)).getFiles(any(), any());
    }
}