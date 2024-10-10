package uk.gov.justice.laa.crime.dces.report.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.exception.DcesReportSourceFilesDataNotFound;
import uk.gov.justice.laa.crime.dces.report.scheduler.DcesReportScheduler.ReportPeriod;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static uk.gov.justice.laa.crime.dces.report.service.MailerService.sendEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class DcesReportService {

    @Autowired
    private final FdcFilesService fdcFilesService;

    @Autowired
    private final ContributionFilesService contributionFilesService;

    @Autowired
    private final EmailClient emailClient;

    @Value("${emailClient.notify.template-id}")
    private String templateId;

    @Value("#{'${emailClient.notify.recipient}'.split(',')}")
    private List<String> recipients;

    @Autowired
    private NotifyConfiguration notifyConfiguration;


    public void sendContributionsReport(ReportPeriod reportPeriod)
            throws JAXBException, IOException,
            DcesReportSourceFilesDataNotFound, NotificationClientException {
        processReportRequest(contributionFilesService, reportPeriod);
    }

    public void sendFdcReport(ReportPeriod reportPeriod) throws JAXBException, IOException, NotificationClientException {
        processReportRequest(fdcFilesService, reportPeriod);
    }

    private void processReportRequest(DcesReportFileService fileService, ReportPeriod reportPeriod) throws JAXBException, IOException, NotificationClientException {
        LocalDate start = DateUtils.getDefaultStartDateForReport(reportPeriod);
        LocalDate end = DateUtils.getDefaultEndDateForReport(reportPeriod);

        log.info("{} {} Report between {} and {}, generation requested",
                fileService.getType(), reportPeriod.getDescription(), start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        List<String> files = fileService.getFiles(start, end);
        File reportFile = fileService.processFiles(files, start, end);
        sendEmailReport(reportFile, fileService.getType(), reportPeriod, start, end);

        log.info("{} {} Report between {} and {} generated successfully",
                fileService.getType(), reportPeriod.getDescription(), start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));
    }

    @Timed("sendEmail")
    public void sendEmailReport(File attachment, String reportType, ReportPeriod reportPeriod, LocalDate start, LocalDate end) throws IOException, NotificationClientException {
        log.info("[{} report] :: Creating email object for time period {} - {} ",
                reportType, start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        EmailObject emailObject = notifyConfiguration.createEmail(attachment, reportType, reportPeriod.getDescription(), start, end, templateId, recipients);

        Timer timer = Metrics.globalRegistry.timer("laa_dces_report_service_send_email");
        timer.record(() -> sendEmail(emailObject, emailClient));
        timer.close();
        Files.delete(attachment.toPath());
    }
}
