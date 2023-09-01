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


    public void sendContributionsReport(LocalDate start, LocalDate end)
            throws JAXBException, IOException,
            DcesReportSourceFilesDataNotFound, NotificationClientException {

        log.info("Contributions Report between {} and {}, generation requested",
                start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        List<String> contributionFiles = contributionFilesService.getFiles(start, end);
        File file = contributionFilesService.processFiles(contributionFiles, start, end);
        sendEmailReport(file, contributionFilesService.getType(), start, end);

        log.info("Contributions Report between {} and {}, generated successfully",
                start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));
    }

    public void sendFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {
        log.info("Start generating FDC Report report between {} and {}",
                start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        List<String> contributionFiles = fdcFilesService.getFiles(start, end);
        File fdcFile = fdcFilesService.processFiles(contributionFiles, start, end);
        sendEmailReport(fdcFile, fdcFilesService.getType(), start, end);

        log.info("Report {} - {} generated successfully",
                start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));
    }

    @Timed("sendEmail")
    private void sendEmailReport(File attachment, String reportType, LocalDate start, LocalDate end) throws IOException, NotificationClientException {
        log.info("[{}] :: Preparing to email for {} report between {} and {} ",
                reportType, reportType, start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        EmailObject emailObject = notifyConfiguration.createEmail(attachment, reportType, start, end, templateId, recipients);

        Timer timer = Metrics.globalRegistry.timer("laa_dces_report_service_send_email");
        timer.record(() -> sendEmail(emailObject, emailClient));
        timer.close();
        Files.delete(attachment.toPath());
    }
}
