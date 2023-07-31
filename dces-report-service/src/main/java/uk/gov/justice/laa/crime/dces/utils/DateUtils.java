package uk.gov.justice.laa.crime.dces.utils;

import lombok.experimental.UtilityClass;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

@UtilityClass
public class DateUtils {

    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


    public String convertXmlGregorianToString(XMLGregorianCalendar xmlGregorianCalendar){
        if(Objects.nonNull(xmlGregorianCalendar)){
            return df.format(xmlGregorianCalendar.toGregorianCalendar().getTime());
        }
        return "";
    }

    public boolean validateDate(XMLGregorianCalendar date, LocalDate startDate, LocalDate endDate){
        if(Objects.isNull(date)){ return false;}
        var convertedDate = LocalDate.parse(date.toString());
        return ( Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
                && convertedDate.compareTo(startDate)>=0
                && convertedDate.compareTo(endDate)<=0);
    }

    public static LocalDate getDefaultStartDateForReport() {
        return LocalDate.now().minusMonths(1).withDayOfMonth(1);
    }

    public static LocalDate getDefaultEndDateForReport() {
        return LocalDate.now().withDayOfMonth(1).minusDays(1);
    }

}
