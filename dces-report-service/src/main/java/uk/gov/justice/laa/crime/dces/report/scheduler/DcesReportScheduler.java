package uk.gov.justice.laa.crime.dces.report.scheduler;

import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.gov.justice.laa.crime.dces.report.config.FeatureFlags;
import uk.gov.justice.laa.crime.dces.report.enums.ReportPeriod;
import uk.gov.justice.laa.crime.dces.report.enums.ReportType;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@ConditionalOnProperty(name="spring.scheduling.enabled", havingValue = "true")
public class DcesReportScheduler {

    private final DcesReportService reportService;

    private final FeatureFlags featureFlags;

    @Timed("laa_dces_report_service_scheduled_contributions_monthly")
    @Scheduled(cron = "${spring.scheduling.cron.contributions.monthly}")
    public void contributionsReportMonthly() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.Monthly, ReportType.Contribution);
    }

    @Timed("laa_dces_report_service_scheduled_contributions_daily")
    @Scheduled(cron = "${spring.scheduling.cron.contributions.daily}")
    public void contributionsReportDaily() throws JAXBException, IOException, NotificationClientException {
        if (featureFlags.runDailyReport()) {
            sendRequestedReport(ReportPeriod.Daily, ReportType.Contribution);
        } else {
            log.info("Not running Daily Contributions report because the feature flag FEATURE_RUNDAILYREPORT is set to false.");
        }
    }

    @Timed("laa_dces_report_service_scheduled_fdc_monthly")
    @Scheduled(cron = "${spring.scheduling.cron.fdc.monthly}")
    public void fdcReportMonthly() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.Monthly, ReportType.FDC);
    }

    @Timed("laa_dces_report_service_scheduled_fdc_daily")
    @Scheduled(cron = "${spring.scheduling.cron.fdc.daily}")
    public void fdcReportDaily() throws JAXBException, IOException, NotificationClientException {
        if (featureFlags.runDailyReport()) {
            sendRequestedReport(ReportPeriod.Daily, ReportType.FDC);
        } else {
            log.info("Not running Daily FDC report because the feature flag FEATURE_RUNDAILYREPORT is set to false.");
        }
    }

    private void sendRequestedReport(ReportPeriod reportPeriod, ReportType reportType) throws JAXBException, IOException, NotificationClientException {

        log.info("Scheduled {} {} Report", reportType, reportPeriod);
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport(reportPeriod);
        LocalDate toDate = DateUtils.getDefaultEndDateForReport(reportPeriod);
        switch (reportType) {
            case Contribution -> reportService.sendContributionsReport(reportPeriod.getDescription(), fromDate, toDate);
            case FDC -> reportService.sendFdcReport(reportPeriod.getDescription(), fromDate, toDate);
        }

        log.info("Successfully finished {} {} Report", reportType, reportPeriod);
    }

}
