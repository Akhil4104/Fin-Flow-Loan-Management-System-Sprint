package com.finflow.document_service.client;

import com.finflow.document_service.client.dto.ApplicationOwnerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationClient {

    private final RestTemplate restTemplate;
    private final String applicationServiceBaseUrl;

    public ApplicationClient(
            RestTemplate restTemplate,
            @Value("${services.application-service.base-url:http://application-service}") String applicationServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.applicationServiceBaseUrl = applicationServiceBaseUrl;
    }

    public ApplicationOwnerResponse getApplication(Long applicationId, Long userId, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", String.valueOf(userId));
        headers.set("X-User-Role", role);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = applicationServiceBaseUrl + "/applications/" + applicationId;
        return restTemplate.exchange(url, HttpMethod.GET, request, ApplicationOwnerResponse.class).getBody();
    }
}
