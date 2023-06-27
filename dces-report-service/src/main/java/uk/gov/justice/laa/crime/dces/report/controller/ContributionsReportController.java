package uk.gov.justice.laa.crime.dces.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.dces.contributions.generated.ContributionFile;
import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;
import uk.gov.justice.laa.crime.dces.report.mapper.ContributionsFileMapper;
import uk.gov.justice.laa.crime.dces.report.model.ContributionFilesResponse;
import uk.gov.justice.laa.crime.dces.report.service.ContributionFilesReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/internal/v1/dces/report")
@Tag(name = "DCES Contribution files report", description = "Rest API to retrieve and generate contribution files report")
public class ContributionsReportController {

    private ContributionFilesReportService contributionFilesService;


    @GetMapping(value = "/contributions/{start}/{finish}")
    @Operation(description = "Retrieve information regarding contribution files sent during the given period and generate a report")
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
    public void getContributionFiles(
            @PathVariable("start") @DateTimeFormat(pattern = ContributionFilesReportService.DATE_FORMAT)
                LocalDate start,
            @PathVariable("finish") @DateTimeFormat(pattern = ContributionFilesReportService.DATE_FORMAT)
                LocalDate finish) {
        ContributionFilesResponse contributionFiles = contributionFilesService.getContributionFiles(start, finish);

        // TODO (DCES-25): This is a draft template for what the next step should look like
//        List<ContributionFile> parsedFiles =  contributionFiles.getFiles()
//                .stream()
//                .map(ContributionsFileMapper::mapContributionsXMLFileToObject)
//                .toList();
    }
}
