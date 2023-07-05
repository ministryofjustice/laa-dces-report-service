package uk.gov.justice.laa.crime.dces.report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext
@EnableAutoConfiguration
@ActiveProfiles("test")
class DcesReportServiceApplicationTests {
	
	@Test
	void contextLoads() {}

}
