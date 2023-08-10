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
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DcesReportService {

    @Autowired
    private final FdcFilesService fdcFilesService;

    @Autowired
    private final ContributionFilesService contributionFilesService;

    @Autowired
    private final NotifyEmailClient emailClient;

    @Value("${emailClient.notify.template-id}")
    private String templateId;

    @Value("${emailClient.notify.recipient}")
    private String recipient;


    public void sendContributionsReport(LocalDate start, LocalDate end)
            throws JAXBException, IOException,
                   DcesReportSourceFilesDataNotFound, NotificationClientException {
        log.info("Start processing Contributions Report Service");
        List<String> contributionFiles = contributionFilesService.getFiles(start, end);

        log.info("Files received and starting processing XML files");
        File file = contributionFilesService.processFiles(contributionFiles, start,end);

        sendEmailWithAttachment(file, contributionFilesService.getType(), start, end);
    }

    public void sendFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {
        log.info("Start processing FDC Report");
        List<String> contributionFiles = fdcFilesService.getFiles(start, end);
        File fdcFile = fdcFilesService.processFiles(contributionFiles, start, end);

        sendEmailWithAttachment(fdcFile, fdcFilesService.getType(), start, end);
    }

    @Timed("sendEmail")
    private void sendEmailWithAttachment(File attachment, String reportType, LocalDate start, LocalDate end) throws IOException, NotificationClientException {
        log.info("Start sending email for report type {}", reportType);
        HashMap<String, Object> personalisation = new HashMap<>();
        personalisation.put("report_type", reportType);
        personalisation.put("from_date", start.toString());
        personalisation.put("to_date", end.toString());
        EmailObject emailObject = new NotifyEmailObject(
                templateId,
                recipient,
                personalisation,
                "_ref",
                ""
        );
        emailObject.addAttachment(attachment);

        Timer timer = Metrics.globalRegistry.timer("laa_dces_report_service_send_email");
        timer.record(() -> emailClient.send(emailObject));
        timer.close();
        Files.delete(attachment.toPath());
    }
}
