package uk.gov.justice.laa.crime.dces.report.scheduler;

import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class DcesReportScheduler {
    @Value("${spring.mvc.format.date}")
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final DcesReportService reportService;

    @Timed("laa_dces_report_service_scheduled_contributions")
    @Scheduled(cron = "${spring.scheduling.contributions.cron}")
    public void contributionsReport() throws JAXBException, IOException, NotificationClientException {
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport();
        LocalDate toDate = DateUtils.getDefaultEndDateForReport();

        log.info("CRON :: contributions: launching between [{}] and [{}]",
                fromDate.format(dateFormatter),
                toDate.format(dateFormatter));

        reportService.sendContributionsReport(fromDate, toDate);

        log.info("Processing finished successfully for contribution files between {} and {}",
                fromDate.format(dateFormatter), toDate.format(dateFormatter));
    }

    @Timed("laa_dces_report_service_scheduled_fdc")
    @Scheduled(cron = "${spring.scheduling.fdc.cron}")
    public void fdcReport() throws JAXBException, IOException, NotificationClientException {
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport();
        LocalDate toDate = DateUtils.getDefaultEndDateForReport();

        log.info("CRON :: FDC: launching between [{}] and [{}]",
                fromDate.format(dateFormatter),
                toDate.format(dateFormatter));

        reportService.sendFdcReport(fromDate, toDate);

        log.info("Processing finished successfully for FDC files between {} and {}",
                fromDate.format(dateFormatter), toDate.format(dateFormatter));
    }
}
