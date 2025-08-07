package uk.gov.justice.laa.crime.dces.report.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import uk.gov.justice.laa.crime.dces.report.enums.ReportPeriod;

@UtilityClass
@ConfigurationPropertiesScan
public class DateUtils {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public String convertXmlGregorianToString(XMLGregorianCalendar xmlGregorianCalendar) {
        if (Objects.nonNull(xmlGregorianCalendar)) {
            return df.format(xmlGregorianCalendar.toGregorianCalendar().getTime());
        }
        return "";
    }

    public boolean validateDate(XMLGregorianCalendar date, LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(date)) {
            return false;
        }
        var convertedDate = LocalDate.parse(date.toString());
        return (Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
                && !convertedDate.isBefore(startDate)
                && !convertedDate.isAfter(endDate));
    }

    public static LocalDate getDefaultStartDateForReport(ReportPeriod reportPeriod) {
        return switch (reportPeriod) {
            case MONTHLY -> LocalDate.now().minusMonths(1).withDayOfMonth(1);
            case DAILY -> LocalDate.now().minusDays(1);
        };
    }

    public static LocalDate getDefaultEndDateForReport(ReportPeriod reportPeriod) {
        return switch (reportPeriod) {
            case MONTHLY -> LocalDate.now().withDayOfMonth(1).minusDays(1);
            case DAILY -> LocalDate.now().minusDays(1);
        };
    }
}
