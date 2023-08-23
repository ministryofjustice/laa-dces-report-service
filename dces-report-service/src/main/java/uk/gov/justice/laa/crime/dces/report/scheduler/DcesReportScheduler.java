package uk.gov.justice.laa.crime.dces.report.scheduler;

import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@ConditionalOnProperty(name="spring.scheduling.enabled", matchIfMissing=true)
public class DcesReportScheduler {
    private final DcesReportService reportService;

    @Timed("laa_dces_report_service_scheduled_contributions")
    @Scheduled(cron = "${spring.scheduling.contributions.cron}")
    public void contributionsReport() throws JAXBException, IOException, NotificationClientException {
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport();
        LocalDate toDate = DateUtils.getDefaultEndDateForReport();

        log.info("Start scheduled Contributions Report between [{}] and [{}]",
                fromDate.format(DateUtils.dateFormatter), toDate.format(DateUtils.dateFormatter));

        reportService.sendContributionsReport(fromDate, toDate);

        log.info("Successfully finished scheduled Contributions Report between {} and {}",
                fromDate.format(DateUtils.dateFormatter), toDate.format(DateUtils.dateFormatter));
    }

    @Timed("laa_dces_report_service_scheduled_fdc")
    @Scheduled(cron = "${spring.scheduling.fdc.cron}")
    public void fdcReport() throws JAXBException, IOException, NotificationClientException {
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport();
        LocalDate toDate = DateUtils.getDefaultEndDateForReport();

        log.info("Start scheduled FDC Report between [{}] and [{}]",
                fromDate.format(DateUtils.dateFormatter), toDate.format(DateUtils.dateFormatter));

        reportService.sendFdcReport(fromDate, toDate);

        log.info("Successfully finished scheduled FDC Report between {} and {}",
                fromDate.format(DateUtils.dateFormatter), toDate.format(DateUtils.dateFormatter));
    }
}
