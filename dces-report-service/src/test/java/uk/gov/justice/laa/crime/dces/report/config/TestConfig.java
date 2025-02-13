package uk.gov.justice.laa.crime.dces.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.crime.dces.report.repository.EventTypeRepository;
import uk.gov.justice.laa.crime.dces.report.service.CSVFileService;

@Configuration
@ComponentScan(basePackages = "uk.gov.justice.laa.crime.dces.report")
public class TestConfig {

  @Bean
  public CSVFileService csvFileService(EventTypeRepository eventTypeRepository) {
    return new CSVFileService(eventTypeRepository);
  }
}
