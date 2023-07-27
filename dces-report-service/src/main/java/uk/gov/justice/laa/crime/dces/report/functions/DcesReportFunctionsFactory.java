package uk.gov.justice.laa.crime.dces.report.functions;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportServiceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DcesReportFunctionsFactory {
    @Value("spring.mvc.format.date")
    private static final String DATE_FORMAT="dd.MM.yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final DcesReportServiceImpl reportService;

    @Bean
    public Consumer<Message<String>> contributions() {
        return (requestMessage) -> {
            MessageHeaders headers = requestMessage.getHeaders();
            LocalDate fromDate = convertDateFromHeader("fromDate", headers);
            LocalDate toDate = convertDateFromHeader("toDate", headers);

            log.info("getContributionsReport: between [{}] and [{}]",
                    fromDate.format(dateFormatter),
                    toDate.format(dateFormatter)
            );

            try {
                File reportFile = reportService.getContributionsReport(fromDate, toDate);
            } catch (JAXBException | IOException e) {
                throw new RuntimeException(e);
            }
        };

    }

    private LocalDate convertDateFromHeader(String key, MessageHeaders headers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String dateToConvert = headers.getOrDefault(key.toLowerCase(), "").toString();
        return LocalDate.parse(dateToConvert, formatter);
    }

    @Bean
    public Supplier<String> health() {
        log.info("Health check requested");
        return () -> "Alive";
    }
}
