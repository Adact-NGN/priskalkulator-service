package no.ding.pk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.management.RuntimeErrorException;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import no.ding.pk.domain.User;
import no.ding.pk.utils.LocalJSONUtils;
import no.ding.pk.web.dto.AdUserDTO;
import no.ding.pk.web.mappers.MapperService;

@Service
public class UserAzureAdServiceImpl implements UserAzureAdService {

    private static final Logger log = LoggerFactory.getLogger(UserAzureAdServiceImpl.class);

    private String graphBaseUrl;

    private String adUserInfoSelectList;

    private String scope;
    
    private ConfidentialClientApplication app;
    private ObjectMapper objectMapper;
    private MapperService mapperService;

    @Autowired
    public UserAzureAdServiceImpl(ConfidentialClientApplication app,
    ObjectMapper objectMapper,
    MapperService mapperService) {
        this.app = app;
        this.objectMapper = objectMapper;
        this.mapperService = mapperService;
    }

    

    public List<User> getUsersList() {
        String userListFromGraph;
        try {
            userListFromGraph = executeUserGraphRequest(null, List.of(adUserInfoSelectList.split(",")));
        } catch (IOException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not establish connection with Graph API."));
        }

        LocalJSONUtils.checkForValidJSON(userListFromGraph);

        List<AdUserDTO> adUserDtoList = jsonToAdUserDTOList(userListFromGraph);

        return mapperService.toUserList(adUserDtoList);
    }

    private List<AdUserDTO> jsonToAdUserDTOList(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonValue = jsonObject.getJSONArray("value");

        List<AdUserDTO> adUserDtoList = new ArrayList<>();
        for(int i = 0; i < jsonValue.length(); i++) {
            String userJson = jsonValue.getJSONObject(i).toString();

            AdUserDTO adUserDTO = jsonToAdUserDTO(userJson);
            adUserDtoList.add(adUserDTO);
        }

        return adUserDtoList;
    }

    public User getUserByEmail(String email) {
        
        String userJson;
        try {
            userJson = executeUserGraphRequest(Collections.singletonList(email), List.of(adUserInfoSelectList.split(",")));
        } catch (IOException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not establish connection with Graph API."));
        }

        LocalJSONUtils.checkForValidJSON(userJson);

        AdUserDTO user = jsonToAdUserDTO(userJson);

        return mapperService.toUser(user);
    }

    public List<User> searchForUserByEmail(String email) {
        String userSearch;

        try {
            userSearch = executeUserSearchRequest(email, List.of(adUserInfoSelectList.split(",")));
        } catch (IOException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not establish connection with Graph API."));
        }

        LocalJSONUtils.checkForValidJSON(userSearch);

        List<AdUserDTO> adUserDTO = jsonToAdUserDTOList(userSearch);

        return mapperService.toUserList(adUserDTO);
    }

    private AdUserDTO jsonToAdUserDTO(String userJson) {
        log.debug("Getting JSON: " + userJson);
        try {
            return objectMapper.readValue(userJson, AdUserDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not deserialize JSON to object."));
        }
    }

    private String executeUserSearchRequest(String email, List<String> selectList) throws IOException {
        String graphUrl = graphBaseUrl + "/users?$filter=startsWith(mail,'" + email + "')&$count=true"; // or startsWith(userPrincipalName,'" + email + "')&$count ne 0";

        if(selectList != null && !selectList.isEmpty()) {
            String joinedSelects = String.join(",", selectList);

            graphUrl = graphUrl + "&$select=" + joinedSelects;
        }

        IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
        URL url = new URL(graphUrl);
        log.debug("Created search URL: " + url.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + result.accessToken());
        conn.setRequestProperty("Accept",MediaType.APPLICATION_JSON_VALUE);

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            log.debug("Response to AD not OK: " + conn.getResponseMessage());
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }

    private String executeUserGraphRequest(List<String> parameters, List<String> selectList) throws IOException {
        
        String graphUrl = graphBaseUrl + "/users";

        if(parameters != null && !parameters.isEmpty()) {
            String joinedParameters = String.join("/", parameters);

            graphUrl = graphUrl + "/" + joinedParameters;
        }

        if(selectList != null && !selectList.isEmpty()) {
            String joinedSelects = String.join(",", selectList);

            graphUrl = graphUrl + "?$select=" + joinedSelects;
        }

        log.debug("Calling Graph URL with: " + graphUrl);

        IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
        URL url = new URL(graphUrl);
        log.debug("Created URL: " + url.toString());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + result.accessToken());
        conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON_VALUE);

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            log.debug("Response to AD not OK: " + conn.getResponseMessage());
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }

    private IAuthenticationResult getAccessTokenByClientCredentialGrant() {
    	
    	// With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(scope))
                .build();
    	
        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        log.debug("Acquiring access token");
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception thrown of type: ", e.getClass());
            log.error("Exception message: ", e.getMessage());

            throw new RuntimeErrorException(new Error("Could not get Access token."));
        }
    }

    @Value("${graph.url:https://graph.microsoft.com/v1.0}")
    public void setGraphBaseUrl(String graphBaseUrl) {
        log.debug("Setting graph url: " + graphBaseUrl);
        this.graphBaseUrl = graphBaseUrl;
    }

    @Value("${AD_USER_INFO_SELECT_LIST}")
    public void setAdUserInfoSelectList(String adUserInfoSelectList) {
        log.debug("Setting AD user info select list: " + adUserInfoSelectList);
        this.adUserInfoSelectList = adUserInfoSelectList;
    }

    @Value("${SCOPE}")
    public void setScope(String scope) {
        log.debug("Setting scope: " + scope);
        this.scope = scope;
    }
}
