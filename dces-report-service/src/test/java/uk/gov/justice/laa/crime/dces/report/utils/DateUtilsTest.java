package uk.gov.justice.laa.crime.dces.report.utils;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;


@SpringBootTest
@ExtendWith(SoftAssertionsExtension.class)
@ActiveProfiles("test")
class DateUtilsTest {
    @InjectSoftAssertions
    private SoftAssertions softly;


    @AfterEach
    void resetCsvFileService(){
        softly.assertAll();
    }

    @Test
    void givenNullDateInput_whenCallingValidateDate_ShouldReturnFalse() {
        softly.assertThat(DateUtils.validateDate(null, LocalDate.now(), LocalDate.now()))
                .isFalse();

    }

    @Test
    void givenDateFromCallingGetXmlDate_whenCallingLocalDateParse_shouldReturnValidLocalDateNow() throws DatatypeConfigurationException {
        // Set
        XMLGregorianCalendar xmlDate = getXmlDate();
        // Expected
        LocalDate expectedLocalDate = LocalDate.now();
        // Test
        softly.assertThat(xmlDate.getYear()).isEqualTo(expectedLocalDate.getYear());
        softly.assertThat(xmlDate.getMonth()).isEqualTo(expectedLocalDate.getMonthValue());
        softly.assertThat(xmlDate.getDay()).isEqualTo(expectedLocalDate.getDayOfMonth());
        softly.assertThat(xmlDate.getTimezone()).isEqualTo(DatatypeConstants.FIELD_UNDEFINED);

        LocalDate revertedLocalDate = LocalDate.parse(xmlDate.toString());
        softly.assertThat(revertedLocalDate.getYear()).isEqualTo(expectedLocalDate.getYear());
        softly.assertThat(revertedLocalDate.getMonthValue()).isEqualTo(expectedLocalDate.getMonthValue());
        softly.assertThat(revertedLocalDate.getDayOfMonth()).isEqualTo(expectedLocalDate.getDayOfMonth());
    }

    @Test
    void givenXmlDateBetweenStartAndEndDateInputs_whenCallingValidateDate_ShouldReturnTrue() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlDate = getXmlDate();

        softly.assertThat(DateUtils.validateDate(
                xmlDate,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(1)))
            .isTrue();
    }

    @Test
    void givenXmlDateBeforeStartDateInput_whenCallingValidateDate_ShouldReturnFalse() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlDate = getXmlDate();

        softly.assertThat(DateUtils.validateDate(
                        xmlDate,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)))
                .isFalse();
    }

    @Test
    void givenXmlDateAfterEndDateInput_whenCallingValidateDate_ShouldReturnFalse() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlDate = getXmlDate();

        softly.assertThat(DateUtils.validateDate(
                        xmlDate,
                        LocalDate.now().minusDays(2),
                        LocalDate.now().minusDays(1)))
                .isFalse();
    }

    @Test
    void givenNullStartDateInput_whenCallingValidateDate_ShouldReturnFalse() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlDate = getXmlDate();

        softly.assertThat(DateUtils.validateDate(
                    xmlDate,
                    null,
                    LocalDate.now().plusDays(1)))
                .isFalse();
    }

    @Test
    void givenNullEndDateInput_whenCallingValidateDate_ShouldReturnFalse() throws DatatypeConfigurationException {
        XMLGregorianCalendar xmlDate = getXmlDate();

        softly.assertThat(DateUtils.validateDate(
                    xmlDate,
                    LocalDate.now().minusDays(1),
                    null))
                .isFalse();
    }

    private XMLGregorianCalendar getXmlDate() throws DatatypeConfigurationException {
        // Gregorian Calendar object creation
        LocalDate localDate = LocalDate.now();
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }
}