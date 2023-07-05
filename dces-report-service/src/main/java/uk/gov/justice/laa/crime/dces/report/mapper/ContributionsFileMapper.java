package uk.gov.justice.laa.crime.dces.report.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS.CcOutcomes.CcOutcome;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS.Correspondence.Letter;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;
import uk.gov.justice.laa.crime.dces.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ContributionsFileMapper {

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;
    protected CSVFileService csvFileService;
    private final static String EMPTY_CHARACTER="";


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

    public File processRequest(String xmlData, Date startDate, Date endDate, String filename) throws JAXBException, IOException {
        ContributionFile contributionFile = mapContributionsXmlStringToObject(xmlData);
        List<CSVDataLine> csvLineList = new ArrayList<>();
        for(CONTRIBUTIONS contribution : contributionFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()){
            csvLineList.add(buildCSVDataLine(contribution, startDate, endDate));
        }
        return csvFileService.writeContributionToCsv(csvLineList, filename);
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
                || !DateUtils.validateDate(contribution.getAssessment().getEffectiveDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getAssessment().getEffectiveDate());
    }
    private String getOutcomeDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        List<CcOutcome> filteredList = contribution.getCcOutcomes().getCcOutcome()
                .stream()
                .filter(Objects::nonNull)
                .filter(x->DateUtils.validateDate(x.getDate(),startDate,endDate))
                .toList();
        if(!filteredList.isEmpty()){
            return DateUtils.convertXmlGregorianToString(filteredList.get(0).getDate());
        }
        return "";
    }
    private String getCorrespondenceSentDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        List<Letter> filteredList = contribution.getCorrespondence().getLetter()
                .stream()
                .filter(Objects::nonNull)
                .filter(x->DateUtils.validateDate(x.getCreated(),startDate,endDate))
                .toList();
        if(!filteredList.isEmpty()){
            return DateUtils.convertXmlGregorianToString(filteredList.get(0).getCreated());
        }
        return "";
    }
    private String getRepOrderStatusDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if(Objects.isNull(contribution.getApplication()) || Objects.isNull(contribution.getApplication().getRepStatusDate())
                || !DateUtils.validateDate(contribution.getApplication().getRepStatusDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getApplication().getRepStatusDate());
    }
    private String getHardshipReviewDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if(Objects.isNull(contribution.getApplication())
                || Objects.isNull(contribution.getApplication().getCcHardship()) || Objects.isNull(contribution.getApplication().getCcHardship().getReviewDate())
                || !DateUtils.validateDate(contribution.getApplication().getCcHardship().getReviewDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getApplication().getCcHardship().getReviewDate());
    }
    private String getPassportedDate(CONTRIBUTIONS contribution, Date startDate, Date endDate){
        if( Objects.isNull(contribution.getPassported()) || Objects.isNull(contribution.getPassported().getDateCompleted())
                || !DateUtils.validateDate(contribution.getPassported().getDateCompleted(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getPassported().getDateCompleted());
    }


}
