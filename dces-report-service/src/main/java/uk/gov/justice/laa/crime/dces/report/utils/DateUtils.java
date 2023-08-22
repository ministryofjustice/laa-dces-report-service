package uk.gov.justice.laa.crime.dces.report.utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@UtilityClass
@ConfigurationPropertiesScan
public class DateUtils {
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

    public static LocalDate getDefaultStartDateForReport() {
        return LocalDate.now().minusMonths(1).withDayOfMonth(1);
    }

    public static LocalDate getDefaultEndDateForReport() {
        return LocalDate.now().withDayOfMonth(1).minusDays(1);
    }

}
