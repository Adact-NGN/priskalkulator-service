package no.ding.pk.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import no.ding.pk.utils.RequestHeaderUtil;

@Service
public class StandardPriceServiceImpl implements StandardPriceService {

    @Value("${sap.api.standard.price.url}")
    private final String standardPriceSapUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZPRICES_SRV/ZZStandPrisSet";

    @Value(value = "${sap.username:AZURE_ECOM}")
    private String sapUsername;

    @Value("${sap.password:AzureEcom@NGN2022}")
    private String sapPassword;
 
    public String getStdPricesForSalesOfficeAndSalesOrg(String salesOffice, String salesOrg) {
        HttpClient client = HttpClient.newBuilder()
        .build();

        String filterQuery = String.format("Salgskontor eq '%s' and Salgsorg eq '%s'", salesOffice, salesOrg);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$filter", filterQuery);
        params.add("$format", "json");

        UriComponents url = UriComponentsBuilder
        .fromUriString(standardPriceSapUrl)
        .queryParams(params)
        .build();

        HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(url.toUri())
        .header("Authorization", RequestHeaderUtil.getBasicAuthenticationHeader(sapUsername, sapPassword))
        .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }

        return response.body();
    }
}
