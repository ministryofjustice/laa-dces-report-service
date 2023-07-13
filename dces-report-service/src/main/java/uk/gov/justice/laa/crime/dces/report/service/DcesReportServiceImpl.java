package uk.gov.justice.laa.crime.dces.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DcesReportServiceImpl implements DcesReportService {

    @Autowired
    private FdcRecordsService fdcRecordsService;

    @Autowired
    private ContributionRecordsService contributionRecordsService;

    @Override
    public ContributionFilesResponse getContributionsCollection(ReportFileType type, LocalDate start, LocalDate end) {
        return getMaatApiService(type).getFiles(start, end);
    }

    private MaatApiRecords getMaatApiService(ReportFileType type) {
        if (type.equals(ReportFileType.FDC)) {
            return fdcRecordsService;
        }

        return contributionRecordsService;
    }


}
