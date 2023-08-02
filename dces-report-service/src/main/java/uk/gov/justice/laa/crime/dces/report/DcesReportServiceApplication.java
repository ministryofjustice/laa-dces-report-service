package uk.gov.justice.laa.crime.dces.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class DcesReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DcesReportServiceApplication.class, args);
	}

}
