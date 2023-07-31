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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DcesReportFunctionsFactory {
    private static final String START_DATE_KEY = "fromdate";
    private static final String END_DATE_KEY = "todate";

    @Value("spring.mvc.format.date")
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final DcesReportService reportService;

    @Bean
    public Consumer<Message<String>> contributionsReport() {
        return (request) -> {
            MessageHeaders headers = request.getHeaders();
            LocalDate startDate = convertStartDate(headers);
            LocalDate endDate = convertEndDate(headers);

            log.info("contributionsReport: request contribution files between [{}] and [{}]",
                    startDate.format(dateFormatter),
                    endDate.format(dateFormatter)
            );

            try {
                reportService.getContributionsReport(startDate, endDate);
            } catch (JAXBException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    public Consumer<Message<String>> fdcReport() {
        return (request) -> {
            MessageHeaders headers = request.getHeaders();
            LocalDate startDate = convertStartDate(headers);
            LocalDate endDate = convertEndDate(headers);

            log.info("fdcReport: request FDC files between [{}] and [{}]",
                    startDate.format(dateFormatter),
                    endDate.format(dateFormatter)
            );

            try {
                reportService.getFdcReport(startDate, endDate);
            } catch (JAXBException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private boolean shouldUseDefaultDates(MessageHeaders headers) {
        return !(headers.containsKey(START_DATE_KEY) && headers.containsKey(END_DATE_KEY));
    }

    private LocalDate convertStartDate(MessageHeaders headers) {
        return (shouldUseDefaultDates(headers)) ?
                DateUtils.getDefaultStartDateForReport() :
                convertDateFromHeader(START_DATE_KEY, headers)

                ;
    }

    private LocalDate convertEndDate(MessageHeaders headers) {
        return (shouldUseDefaultDates(headers)) ?
                DateUtils.getDefaultEndDateForReport() :
                convertDateFromHeader(END_DATE_KEY, headers)
                ;
    }

    private LocalDate convertDateFromHeader(String key, MessageHeaders headers) {
        return LocalDate.parse(
                headers.getOrDefault(key, null).toString(),
                dateFormatter
        );
    }
}
