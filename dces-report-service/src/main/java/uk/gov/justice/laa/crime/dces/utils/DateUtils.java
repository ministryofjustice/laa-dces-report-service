package uk.gov.justice.laa.crime.dces.utils;

import lombok.experimental.UtilityClass;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@UtilityClass
public class DateUtils {

    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public String convertXmlGregorianToString(XMLGregorianCalendar xmlGregorianCalendar){
        if(Objects.nonNull(xmlGregorianCalendar)){
            return df.format(xmlGregorianCalendar.toGregorianCalendar().getTime());
        }
        return "";
    }

    public boolean validateDate(XMLGregorianCalendar date, Date startDate, Date endDate){
        Date convertedDate;
        try {
            convertedDate = dateFormat.parse(date.toString());
        } catch (ParseException e) {
            return false;
        }
        return validateDate(convertedDate, startDate, endDate);

    }
    private boolean validateDate(Date toValidate, Date startDate, Date endDate){
        return ( Objects.nonNull(toValidate)
                && Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
                && toValidate.compareTo(startDate)>=0
                && toValidate.compareTo(endDate)<=0);
    }


}
