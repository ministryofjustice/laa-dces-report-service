package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import java.util.Map;

import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;


@HttpExchange
public interface MaatApiClient {

    @GetExchange("/get")
    MaatApiResponseModel sendGetRequest(@PathVariable long usn, @RequestHeader Map<String, String> headers);
}