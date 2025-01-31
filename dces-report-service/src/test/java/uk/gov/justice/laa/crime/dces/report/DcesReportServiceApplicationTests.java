package uk.gov.justice.laa.crime.dces.report;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext
@EnableAutoConfiguration
@ActiveProfiles("test")
class DcesReportServiceApplicationTests {
	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void printAllBeans() {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
	}
	@java.lang.SuppressWarnings("squid:S2699")
	@Test
	void contextLoads() {}

}
