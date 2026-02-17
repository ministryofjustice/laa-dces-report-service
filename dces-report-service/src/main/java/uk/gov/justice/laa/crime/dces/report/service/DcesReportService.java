package uk.gov.justice.laa.crime.dces.report.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.dto.FailureReportDto;
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
import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.justice.laa.crime.dces.report.service.MailerService.sendEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class DcesReportService {

    private final FdcFilesService fdcFilesService;

    private final ContributionFilesService contributionFilesService;

    private final FailuresReportService failuresReportService;

    private final EmailClient emailClient;

    private final NotifyConfiguration notifyConfiguration;

    private final CaseSubmissionErrorService caseSubmissionErrorService;

    @Value("${emailClient.notify.template-id}")
    private String templateId;

    @Value("#{'${emailClient.notify.recipient.default}'.split(',')}")
    private List<String> drcProcessingReportRecipients;

    @Value("#{'${emailClient.notify.recipient.failures}'.split(',')}")
    private List<String> failuresReportRecipients;

    public void sendContributionsReport(String reportTitle, LocalDate start, LocalDate end)
            throws JAXBException, IOException,
            DcesReportSourceFilesDataNotFound, NotificationClientException {
        processReportRequest(contributionFilesService, reportTitle, start, end);
    }

    public void sendFdcReport(String reportTitle, LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {
        processReportRequest(fdcFilesService, reportTitle, start, end);
    }

    public void sendFailuresReport(String reportTitle, LocalDate reportDate) throws IOException, NotificationClientException {
        log.info("{} Report generation requested",  reportTitle);

        FailureReportDto failureReportDto = failuresReportService.generateReport(reportTitle, reportDate);
        if (failureReportDto != null) {
            sendEmailReport(failureReportDto.getReportFile(), failuresReportService.getType(), failureReportDto.getFailuresCount() + " failures found for " + reportTitle, reportDate,
                reportDate);
            log.info("{} Report generated and sent successfully", reportTitle);
        } else {
            log.info("No failures found and feature flag to send empty reports is absent/set to false, so not sending the failure report email");
        }
    }

    private void processReportRequest(DcesReportFileService fileService, String reportTitle, LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {

        log.info("{} {} Report between {} and {}, generation requested",
                fileService.getType(), reportTitle, start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        List<String> files = fileService.getFiles(start, end);
        File reportFile = fileService.processFiles(files, reportTitle, start, end);
        sendEmailReport(reportFile, fileService.getType(), reportTitle, start, end);

        log.info("{} {} Report between {} and {} generated successfully",
                fileService.getType(), reportTitle, start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));
    }

    @Timed("sendEmail")
    private void sendEmailReport(File attachment, String reportType, String reportTitle, LocalDate start, LocalDate end) throws IOException, NotificationClientException {
        log.info("[{} report] :: Creating email object for time period {} - {} ",
                reportType, start.format(DateUtils.dateFormatter), end.format(DateUtils.dateFormatter));

        EmailObject emailObject = notifyConfiguration.createEmail(attachment, reportType, reportTitle, start, end, templateId, reportType.equals(failuresReportService.getType())?failuresReportRecipients: drcProcessingReportRecipients);

        Timer timer = Metrics.globalRegistry.timer("laa_dces_report_service_send_email");
        timer.record(() -> sendEmail(emailObject, emailClient));
        timer.close();
        Files.delete(attachment.toPath());
    }

    public void sendCaseSubmissionErrorReport(String reportTitle, LocalDate reportDate) throws IOException, NotificationClientException {
        log.info("{} Case Submission error report generation requested", reportTitle);
        FailureReportDto failureReportDto = caseSubmissionErrorService.generateReport(reportDate);

        if (failureReportDto != null) {
            sendEmailReport(failureReportDto.getReportFile(), caseSubmissionErrorService.getType(), failureReportDto.getFailuresCount() + " case submission errors found for " + reportTitle, reportDate,
                    LocalDate.now());
            log.info("{} Report generated and sent successfully", reportTitle);
        } else {
            log.info("No Case Submission Error report found and feature flag to send empty reports is absent/set to false, so not sending the case submission error report email");
        }
    }

}
