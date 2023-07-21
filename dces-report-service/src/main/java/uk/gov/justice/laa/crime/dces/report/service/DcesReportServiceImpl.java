package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public File getContributionsReport(LocalDate start, LocalDate end) throws JAXBException, IOException {
        List<String> contributionFiles = contributionFilesService.getFiles(start, end);
        // @TODO handle empty list

        return contributionFilesService.processFiles(
                contributionFiles,
                start,
                end,
                contributionFilesService.getFileName(start, end)
        );
    }

    public File getFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException {
        List<String> contributionFiles = fdcFilesService.getFiles(start, end);
        // @TODO handle empty list
        return fdcFilesService.processFiles(contributionFiles, start, end);
    }
}
