package uk.gov.justice.laa.crime.dces.report.service;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.utils.email.EmailObject;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailClient;
import uk.gov.justice.laa.crime.dces.report.utils.email.NotifyEmailObject;
import uk.gov.service.notify.NotificationClientException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DcesReportServiceImpl implements DcesReportService {

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


    @Override
    public void sendContributionsReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {
        List<String> contributionFiles = contributionFilesService.getFiles(start, end);
        // TODO (DCES-78):  handle empty list

        File file = contributionFilesService.processFiles(
                contributionFiles,
                start,
                end,
                contributionFilesService.getFileName(start, end)
        );

        sendEmailWithAttachment(file, "Contributions", start, end);
    }

    @Override
    public void sendFdcReport(LocalDate start, LocalDate end) throws JAXBException, IOException, NotificationClientException {
        List<String> contributionFiles = fdcFilesService.getFiles(start, end);
        // TODO (DCES-78):  handle empty list
        File fdcFile = fdcFilesService.processFiles(contributionFiles, start, end);

        sendEmailWithAttachment(fdcFile, "Final Defence Cost", start, end);
    }

    private void sendEmailWithAttachment(File attachment, String reportType, LocalDate start, LocalDate end) throws IOException, NotificationClientException {
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

        emailClient.send(emailObject);
    }
}
