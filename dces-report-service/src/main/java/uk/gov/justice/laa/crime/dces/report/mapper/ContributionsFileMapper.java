package uk.gov.justice.laa.crime.dces.report.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Setter
@Getter
public class ContributionsFileMapper {

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;
    private CSVFileService csvFileService;
    private final static String EMPTY_CHARACTER="";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ContributionsFileMapper contributionsFileMapper(){
        try {
            this.jaxbContext = JAXBContext.newInstance(ContributionFile.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            this.csvFileService = new CSVFileService();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public File processRequest(String xmlData, Date startDate, Date endDate) throws JAXBException, IOException {
        ContributionFile contributionFile = mapContributionsXmlStringToObject(xmlData);
        List<CSVDataLine> csvLineList = new ArrayList<>();
        for(CONTRIBUTIONS contribution : contributionFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()){
            csvLineList.add(buildCSVDataLine(contribution, startDate, endDate));
        }
        return csvFileService.writeContributionToCsv(csvLineList, "output.csv");
    }

    public ContributionFile mapContributionsXMLFileToObject(File xmlFile) throws JAXBException {
        return (ContributionFile) unmarshaller.unmarshal(xmlFile);
    }

    public ContributionFile mapContributionsXmlStringToObject(String xmlString) throws JAXBException {
        StringReader sr = new StringReader(xmlString);
        return (ContributionFile) unmarshaller.unmarshal(sr);
    }

    public CSVDataLine buildCSVDataLine(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        return CSVDataLine.builder()
                .maatId(getMaatId(contribution))
                .dataFeedType(getDataFeed(contribution))
                .assessmentDate(getAssessmentDate(contribution, startDate, endDate))
                .ccOutcomeDate(getOutcomeDate(contribution, startDate, endDate))
                .correspondenceSentDate(getCorrespondenceSentDate(contribution, startDate, endDate))
                .repOrderStatusDate(getRepOrderStatusDate(contribution, startDate, endDate))
                .hardshipReviewDate(getHardshipReviewDate(contribution, startDate, endDate))
                .passportedDate(getPassportedDate(contribution, startDate, endDate))
                .build();
    }

    private String getMaatId(CONTRIBUTIONS contribution){
        return String.valueOf(contribution.getMaatId());
    }
    private String getDataFeed(CONTRIBUTIONS contribution){
        return contribution.getFlag();
    }
    private String getAssessmentDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if(Objects.isNull(contribution.getAssessment()) || Objects.isNull(contribution.getAssessment().getEffectiveDate())
                || !validateDate(contribution.getAssessment().getEffectiveDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return contribution.getAssessment().getEffectiveDate().toString();
    }
    private String getOutcomeDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        return "TODO: FIX ME";
    }
    private String getCorrespondenceSentDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        return "TODO: FIX ME";
    }
    private String getRepOrderStatusDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if(Objects.isNull(contribution.getApplication()) || Objects.isNull(contribution.getApplication().getRepStatusDate())
                || !validateDate(contribution.getApplication().getRepStatusDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return contribution.getApplication().getRepStatusDate().toString();
    }
    private String getHardshipReviewDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if(Objects.isNull(contribution.getApplication())
                || Objects.isNull(contribution.getApplication().getCcHardship()) || Objects.isNull(contribution.getApplication().getCcHardship().getReviewDate())
                || !validateDate(contribution.getApplication().getCcHardship().getReviewDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return contribution.getApplication().getCcHardship().getReviewDate().toString();
    }
    private String getPassportedDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if( Objects.isNull(contribution.getPassported()) || Objects.isNull(contribution.getPassported().getDateCompleted())
                || !validateDate(contribution.getPassported().getDateCompleted(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return contribution.getPassported().getDateCompleted().toString();
    }

    private boolean validateDate(Date toValidate, Date startDate, Date endDate){
        return ( Objects.nonNull(toValidate)
                && Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
                && toValidate.compareTo(startDate)>=0
                && toValidate.compareTo(endDate)<=0);
    }

    private boolean validateDate(XMLGregorianCalendar date, Date startDate, Date endDate){
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(date.toString());
        } catch (ParseException e) {
            return false;
        }
        return validateDate(convertedDate, startDate, endDate);
    }
}
