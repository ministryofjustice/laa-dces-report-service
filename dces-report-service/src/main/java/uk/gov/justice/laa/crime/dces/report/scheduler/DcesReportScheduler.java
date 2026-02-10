package uk.gov.justice.laa.crime.dces.report.scheduler;

import io.micrometer.core.annotation.Timed;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.gov.justice.laa.crime.dces.report.config.FeatureProperties;
import uk.gov.justice.laa.crime.dces.report.enums.ReportPeriod;
import uk.gov.justice.laa.crime.dces.report.enums.ReportType;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.report.utils.DateUtils;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class DcesReportScheduler {

    private final DcesReportService reportService;

    private final FeatureProperties feature;


    @Timed("laa_dces_report_service_scheduled_contributions_monthly")
    @Scheduled(cron = "${spring.scheduling.cron.contributions.monthly:-}")
    public void contributionsReportMonthly() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.MONTHLY, ReportType.CONTRIBUTION);
    }

    @Timed("laa_dces_report_service_scheduled_contributions_daily")
    @Scheduled(cron = "${spring.scheduling.cron.contributions.daily:-}")
    public void contributionsReportDaily() throws JAXBException, IOException, NotificationClientException {
        if (feature.runDailyReport()) {
            sendRequestedReport(ReportPeriod.DAILY, ReportType.CONTRIBUTION);
        } else {
            log.info("Not running Daily Contributions report because the feature flag FEATURE_RUNDAILYREPORT is set to false.");
        }
    }

    @Timed("laa_dces_report_service_scheduled_fdc_monthly")
    @Scheduled(cron = "${spring.scheduling.cron.fdc.monthly:-}")
    public void fdcReportMonthly() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.MONTHLY, ReportType.FDC);
    }

    @Timed("laa_dces_report_service_scheduled_fdc_daily")
    @Scheduled(cron = "${spring.scheduling.cron.fdc.daily:-}")
    public void fdcReportDaily() throws JAXBException, IOException, NotificationClientException {
        if (feature.runDailyReport()) {
            sendRequestedReport(ReportPeriod.DAILY, ReportType.FDC);
        } else {
            log.info("Not running Daily FDC report because the feature flag FEATURE_RUNDAILYREPORT is set to false.");
        }
    }

    @Timed("laa_dces_report_service_scheduled_failures_daily")
    @Scheduled(cron = "${spring.scheduling.cron.failures.daily:-}")
    public void failuresReportDaily() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.DAILY, ReportType.FAILURES);
    }

    private void sendRequestedReport(ReportPeriod reportPeriod, ReportType reportType) throws JAXBException, IOException, NotificationClientException {

        log.info("Scheduled {} {} Report", reportType, reportPeriod);
        LocalDate fromDate = DateUtils.getDefaultStartDateForReport(reportPeriod);
        LocalDate toDate = DateUtils.getDefaultEndDateForReport(reportPeriod);
        switch (reportType) {
            case CONTRIBUTION -> reportService.sendContributionsReport(reportPeriod.getDescription(), fromDate, toDate);
            case FDC -> reportService.sendFdcReport(reportPeriod.getDescription(), fromDate, toDate);
            case FAILURES -> reportService.sendFailuresReport(reportPeriod.getDescription(), toDate);
            case CASE_SUBMISSION_ERROR -> reportService.sendCaseSubmissionErrorReport(reportPeriod.getDescription(), fromDate);
        }

        log.info("Successfully finished {} {} Report", reportType, reportPeriod);
    }

    @Timed("laa_dces_report_service_scheduled_case_submission_error_daily")
    @Scheduled(cron = "${spring.scheduling.cron.caseSubmissionError.daily:-}")
    public void caseSubmissionErrorReportDaily() throws JAXBException, IOException, NotificationClientException {
        sendRequestedReport(ReportPeriod.DAILY, ReportType.CASE_SUBMISSION_ERROR);
    }

}
