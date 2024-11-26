package uk.gov.justice.laa.crime.dces.report.controller;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.dces.report.service.DcesReportService;
import uk.gov.service.notify.NotificationClientException;
import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/internal/v1/dces/report")
@Tag(name = "DCES Contribution files report", description = "Rest API to retrieve and generate contribution files report")
public class DcesReportController {

    private final DcesReportService reportService;


    @GetMapping(value = "/contributions/{reportTitle}/{start}/{finish}")
    @Operation(description = "Generate Contributions report for the given period and send it by email")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400",
            description = "Bad request.",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public void getContributionsReport(@PathVariable("reportTitle") String reportTitle, @PathVariable("start") LocalDate start, @PathVariable("finish") LocalDate finish) throws JAXBException, IOException, NotificationClientException {
        log.info("Contributions Report entry point called with title {}, start:{}, end: {}", reportTitle, start, finish);
        Sentry.captureMessage("Processing contri", SentryLevel.INFO);
        reportService.sendContributionsReport(reportTitle, start, finish);
    }

    @GetMapping(value = "/fdc/{reportTitle}/{start}/{finish}")
    @Operation(description = "Generate FDC report for the given period and send it by email")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "400",
            description = "Bad request.",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public void getFdcReport(@PathVariable("reportTitle") String reportTitle, @PathVariable("start") LocalDate start, @PathVariable("finish") LocalDate finish) throws JAXBException, IOException, NotificationClientException {
        log.info("FDC Report entry point called with title {}, start:{}, end: {}", reportTitle, start, finish);
        reportService.sendFdcReport(reportTitle, start, finish);
    }
}
