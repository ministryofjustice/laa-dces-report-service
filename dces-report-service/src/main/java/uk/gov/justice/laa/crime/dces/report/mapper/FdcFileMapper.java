package uk.gov.justice.laa.crime.dces.report.mapper;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.dces.report.model.generated.FdcFile;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class FdcFileMapper {

    private Unmarshaller unmarshaller;
    protected CSVFileService csvFileService;

    @Autowired
    public FdcFileMapper fdcFileMapper() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(FdcFile.class);
        unmarshaller = jaxbContext.createUnmarshaller();
        return this;
    }

    public File processRequest(String[] xmlData, String filename) throws JAXBException, IOException {
        List<FdcFile> csvLineList = new ArrayList<>();
        for (String xmlString: xmlData) {
            csvLineList.add(mapFdcXmlStringToObject(xmlString));
        }
        return csvFileService.writeFdcFileListToCsv(csvLineList, filename);
    }

    public FdcFile mapFdcXmlStringToObject(String xmlString) throws JAXBException {
        StringReader sr = new StringReader(xmlString);
        return (FdcFile) unmarshaller.unmarshal(sr);
    }

    public FdcFile mapFdcXMLFileToObject(File xmlFile) throws JAXBException {
        return (FdcFile) unmarshaller.unmarshal(xmlFile);
    }

}
