package uk.gov.justice.laa.crime.dces.report.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile;

import java.io.File;

public class ContributionsFileMapper {

    public static ContributionFile mapContributionsXMLFileToObject(File xmlFile) {
        ContributionFile contFile;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ContributionFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            contFile = (ContributionFile) jaxbUnmarshaller.unmarshal(xmlFile);
        } catch (
                JAXBException e) {
            throw new RuntimeException(e);
        }
        return contFile;
    }

    public static ContributionFile mapContributionsXMLFileToObject(String xmlFile) {
        return null;
    }
}
