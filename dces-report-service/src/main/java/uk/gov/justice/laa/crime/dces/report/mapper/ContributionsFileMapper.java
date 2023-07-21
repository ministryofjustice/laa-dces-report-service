package uk.gov.justice.laa.crime.dces.report.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS.CcOutcomes.CcOutcome;
import uk.gov.justice.laa.crime.dces.report.model.generated.ContributionFile.CONTRIBUTIONSLIST.CONTRIBUTIONS.Correspondence.Letter;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;
import uk.gov.justice.laa.crime.dces.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ContributionsFileMapper {

    private Unmarshaller unmarshaller;
    protected CSVFileService csvFileService;
    private static final String EMPTY_CHARACTER="";


    @Autowired
    public ContributionsFileMapper contributionsFileMapper() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ContributionFile.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        this.csvFileService = new CSVFileService();
        return this;
    }

    public File processRequest(String[] xmlData, LocalDate startDate, LocalDate endDate, String filename) throws IOException, JAXBException {
        List<CSVDataLine> csvLineList = new ArrayList<>();
        for (String xmlString: xmlData) {
            processXMLFile(xmlString, startDate, endDate, csvLineList);
        }
        return csvFileService.writeContributionToCsv(csvLineList, filename);
    }

    private void processXMLFile(String xmlData, LocalDate startDate, LocalDate endDate, List<CSVDataLine> csvLineList) throws JAXBException {
        ContributionFile contributionFile = mapContributionsXmlStringToObject(xmlData);
        for(CONTRIBUTIONS contribution : contributionFile.getCONTRIBUTIONSLIST().getCONTRIBUTIONS()){
            csvLineList.add(buildCSVDataLine(contribution, startDate, endDate));
        }

    }

    public ContributionFile mapContributionsXMLFileToObject(File xmlFile) throws JAXBException {
        return (ContributionFile) unmarshaller.unmarshal(xmlFile);
    }

    public ContributionFile mapContributionsXmlStringToObject(String xmlString) throws JAXBException {
        StringReader sr = new StringReader(xmlString);
        return (ContributionFile) unmarshaller.unmarshal(sr);
    }

    public CSVDataLine buildCSVDataLine(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
        // Business logic inside several of these. Go to methods for details.
        return CSVDataLine.builder()
                .maatId(getMaatId(contribution))
                .dataFeedType(getDataFeed(contribution))
                // Uses start and end date to control visibility. If a date entry is outside of the range, it is not used.
                // E.g. hardshipReviewDate is 01/01/2023, and the start/end dates are 01/10/2022 and 01/11/2022, then it
                // would return "", as it lies outside the values.
                .assessmentDate(getAssessmentDate(contribution, startDate, endDate))
                .repOrderStatusDate(getRepOrderStatusDate(contribution, startDate, endDate))
                .hardshipReviewDate(getHardshipReviewDate(contribution, startDate, endDate))
                .passportedDate(getPassportedDate(contribution, startDate, endDate))
                // filter list of values to only ones where date is in range given.
                // Then take the date from the very first one in the list, if exists.
                // Otherwise returns ""
                .ccOutcomeDate(getOutcomeDate(contribution, startDate, endDate))
                .correspondenceSentDate(getCorrespondenceSentDate(contribution, startDate, endDate))
                .build();
    }


    private String getMaatId(CONTRIBUTIONS contribution){
        return String.valueOf(contribution.getMaatId());
    }
    private String getDataFeed(CONTRIBUTIONS contribution){
        return contribution.getFlag();
    }
    private String getAssessmentDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
        if(Objects.isNull(contribution.getAssessment()) || Objects.isNull(contribution.getAssessment().getEffectiveDate())
                || !DateUtils.validateDate(contribution.getAssessment().getEffectiveDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getAssessment().getEffectiveDate());
    }
    private String getOutcomeDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
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
    private String getCorrespondenceSentDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
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
    private String getRepOrderStatusDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
        if(Objects.isNull(contribution.getApplication()) || Objects.isNull(contribution.getApplication().getRepStatusDate())
                || !DateUtils.validateDate(contribution.getApplication().getRepStatusDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getApplication().getRepStatusDate());
    }
    private String getHardshipReviewDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
        if(Objects.isNull(contribution.getApplication())
                || Objects.isNull(contribution.getApplication().getCcHardship()) || Objects.isNull(contribution.getApplication().getCcHardship().getReviewDate())
                || !DateUtils.validateDate(contribution.getApplication().getCcHardship().getReviewDate(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getApplication().getCcHardship().getReviewDate());
    }
    private String getPassportedDate(CONTRIBUTIONS contribution, LocalDate startDate, LocalDate endDate){
        if( Objects.isNull(contribution.getPassported()) || Objects.isNull(contribution.getPassported().getDateCompleted())
                || !DateUtils.validateDate(contribution.getPassported().getDateCompleted(), startDate, endDate)){
            return EMPTY_CHARACTER;
        }
        return DateUtils.convertXmlGregorianToString(contribution.getPassported().getDateCompleted());
    }


}
