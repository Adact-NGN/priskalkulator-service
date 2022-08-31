package no.ding.pk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.ding.pk.utils.RequestHeaderUtil;

@Service
public class CustomerService {
    
    // private CustomerRepository repository;
    private ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    public CustomerService() {
        // this.repository = customerRepository;
    }
    
    @Value("${sap.api.customer.url}")
    private final String customerSapServiceUrl2 = "https://saptest.norskgjenvinning.no/sap/opu/odata4/sap/zapi_hp_customers2/srvd_a2x/sap/zapi_hp_customers/0001/Kunder?%24filter=Morselskap%20eq'%20'%20and%20Kundetype%20eq%20'Node'%20&%24expand=Nodekunder&sap-language=NO&%24format=json&%24top=20000";
    private final String customerSapServiceUrl = "https://saptest.norskgjenvinning.no/sap/opu/odata/sap/ZAPI_ECOM_CUSTOMERS_V2/ZC_ECOM_CUSTOMER";

    @Value(value = "${sap.username:AZURE_ECOM}")
    private String sapUsername;

    @Value("${sap.password:AzureEcom@NGN2022}")
    private String sapPassword;

    public String fetchCustomersJSON() {
        HttpClient client = HttpClient.newBuilder()
        .build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("$format", "json");

        UriComponents url = UriComponentsBuilder
        .fromUriString(customerSapServiceUrl2)
        .queryParams(params)
        // .queryParam("$format", "json")
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
    
    private String readResponse(Reader inputStreamReader) throws IOException {
        BufferedReader in = new BufferedReader(inputStreamReader);
        
        String inputLine;
        StringBuffer content = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        
        return content.toString();
    }
    
    
    public static String getParamsString(Map<String, String> params) 
    throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }
        
        String resultString = result.toString();
        return resultString.length() > 0
        ? resultString.substring(0, resultString.length() - 1)
        : resultString;
    }
    
}
