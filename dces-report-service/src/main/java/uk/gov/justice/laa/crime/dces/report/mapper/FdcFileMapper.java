package uk.gov.justice.laa.crime.dces.report.mapper;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.contributions.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.model.CSVDataLine;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FdcFileMapper {

    JAXBContext jaxbContext;
    Unmarshaller unmarshaller;

    @Autowired
    public FdcFileMapper fdcFileMapper(){
        try {
            this.jaxbContext = JAXBContext.newInstance(FdcFile.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void processRequest(String xmlData, Date startDate, Date toDate) throws JAXBException {
        FdcFile fdcFile = mapFdcXmlStringToObject(xmlData);
        List<CSVDataLine> csvLineList = new ArrayList<>();
    }

    public FdcFile mapFdcXmlFileToObject(File xmlFile) throws JAXBException {
            return (FdcFile) unmarshaller.unmarshal(xmlFile);
    }

    public FdcFile mapFdcXmlStringToObject(String xmlString) throws JAXBException {
        StringReader sr = new StringReader(xmlString);
        return (FdcFile) unmarshaller.unmarshal(sr);
    }

}
