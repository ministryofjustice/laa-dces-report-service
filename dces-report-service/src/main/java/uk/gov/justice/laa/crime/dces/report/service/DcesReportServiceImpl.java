package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DcesReportServiceImpl implements DcesReportService {

    @Autowired
    private FdcFilesService fdcFilesService;

    @Autowired
    private ContributionFilesService contributionFilesService;

    @Override
    public ContributionFilesResponse getApiFiles(ReportFileType type, LocalDate start, LocalDate end) {
        return getApiFilesService(type).getFiles(start, end);
    }

    @Override
    public File processFiles(
            ReportFileType type,
            List<String> files,
            LocalDate start,
            LocalDate end,
            String fileName) throws JAXBException, IOException {
        return getApiFilesService(type).processFiles(files, start, end, fileName);
    }

    private MaatApiFilesService getApiFilesService(ReportFileType type) {
        if (type.equals(ReportFileType.FDC)) {
            return fdcFilesService;
        }

        return contributionFilesService;
    }
}
