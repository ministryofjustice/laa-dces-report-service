package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.time.LocalDate;

public interface DcesReportService {

    void getContributionsReport(LocalDate start, LocalDate end) throws JAXBException, IOException;

    void getFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException;
}
