package uk.gov.justice.laa.crime.dces.report.functions;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.justice.laa.crime.dces.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DcesReportFunctionsFactory {
    @Value("spring.mvc.format.date")
    private static final String DATE_FORMAT="dd.MM.yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final DcesReportService reportService;

    @Bean
    public Consumer<Message<String>> contributionsReport() {
        return (request) -> {
            MessageHeaders headers = request.getHeaders();
            LocalDate fromDate = convertFromDate(headers);
            LocalDate toDate = convertToDate(headers);

            log.info("contributionsReport: request contribution files between [{}] and [{}]",
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

    @Bean
    public Consumer<Message<String>> fdcReport() {
        return (request) -> {
            MessageHeaders headers = request.getHeaders();
            LocalDate fromDate = convertFromDate(headers);
            LocalDate toDate = convertToDate(headers);

            log.info("fdcReport: request FDC files between [{}] and [{}]",
                    fromDate.format(dateFormatter),
                    toDate.format(dateFormatter)
            );

            try {
                File reportFile = reportService.getFdcReport(fromDate, toDate);
            } catch (JAXBException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private boolean validateInputs(MessageHeaders headers) {
        return (headers.containsKey("fromdate") && headers.containsKey("todate"));
    }

    private LocalDate convertFromDate(MessageHeaders headers) {
        return (validateInputs(headers)) ?
                convertDateFromHeader("fromdate", headers) :
                    DateUtils.getCurrentFromDate()
        ;
    }

    private LocalDate convertToDate(MessageHeaders headers) {
        return (validateInputs(headers)) ?
            convertDateFromHeader("todate", headers) :
                DateUtils.getCurrentToDate()
        ;
    }

    private LocalDate convertDateFromHeader(String key, MessageHeaders headers) {
        if (!headers.containsKey(key))
            return null;

        return LocalDate.parse(
                headers.getOrDefault(key, null).toString(),
                dateFormatter
        );
    }
}
