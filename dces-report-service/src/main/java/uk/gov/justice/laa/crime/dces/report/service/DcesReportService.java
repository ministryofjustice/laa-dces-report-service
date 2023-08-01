package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;

public interface DcesReportService {

    void sendContributionsReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException;

    void sendFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException;
}
