package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public interface DcesReportService {

    File getContributionsReport(LocalDate start, LocalDate end) throws JAXBException, IOException;

    File getFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException;
}
