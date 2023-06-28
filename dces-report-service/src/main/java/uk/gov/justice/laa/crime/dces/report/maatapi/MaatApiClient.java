package uk.gov.justice.laa.crime.dces.report.maatapi;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import uk.gov.justice.laa.crime.dces.report.maatapi.model.MaatApiResponseModel;


@HttpExchange
public interface MaatApiClient {

    @GetExchange("/get")
    MaatApiResponseModel sendGetRequest();
}