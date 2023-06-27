
```mermaid

sequenceDiagram
    autonumber
    participant SchedularCronService 
    participant DcesReportService 
    participant DataCollectionService
    participant ContributionFilesReportService
    participant FcdFilesReportService
    participant MaatApiClient
    participant XmlConvertorService
    participant CsvFileGenerator
    participant ContributionsFileMapper 
    participant EmailService
    participant SesEmailClient
    
    Note over SchedularCronService ,DcesReportService: 1. Java/Spring Cron Job.  
    SchedularCronService->>DcesReportService: Trigger every 30-day
    rect gray
        DcesReportService->>DataCollectionService: Getting XML files.
        Note over DcesReportService ,MaatApiClient: 2. Input files. Dependency on the Cognito Service for Auth.  
        DataCollectionService->>ContributionFilesReportService: Calling for contribution files 
        ContributionFilesReportService->>MaatApiClient: Calling a Client with start/end date. 
        MaatApiClient-->>ContributionFilesReportService: Returning a List<String>  
        
        DataCollectionService->>FcdFilesReportService: Calling a FDC files
        FcdFilesReportService->>MaatApiClient: Calling a Client with start/end date.
        MaatApiClient-->>FcdFilesReportService: Returning a List<String>
        Note over DataCollectionService, DcesReportService : These are XML files returning as a String list 
        DataCollectionService-->>DcesReportService: Returning list of XML.
    end  

    alt when List is empty 
    DcesReportService-->>SchedularCronService: terminating the process. 
    end
    rect gray 
        Note over DcesReportService, XmlConvertorService : 3. Component to process and parse XML and generate CSV
        DcesReportService->>XmlConvertorService: Processing the XML
        XmlConvertorService->>ContributionsFileMapper: Converting XML into objects
        ContributionsFileMapper-->>XmlConvertorService: Returning logical objects
        XmlConvertorService->>CsvFileGenerator: Generating CSV file  
        XmlConvertorService->>DcesReportService: Returning CSV File
    end 
    Note over DcesReportService, EmailService: 4. Sending email per file type (e.g. FDC).
    DcesReportService->>EmailService: Processing the Email.
    EmailService->>SesEmailClient: Creating an email config to send out.