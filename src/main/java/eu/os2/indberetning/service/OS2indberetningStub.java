package eu.os2.indberetning.service;

import eu.os2.indberetning.model.APIOrganizationDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OS2indberetningStub {

	@Autowired
	private RestTemplate restTemplate;

	@SneakyThrows
	public void updateOrganization(String baseUrl, String apiKey, APIOrganizationDTO apiOrganization)
	{
		var request = new HttpEntity<>(apiOrganization,getHeaders(apiKey));

		var response = restTemplate.exchange(baseUrl + "/api/UpdateOrganization", HttpMethod.POST, request, String.class);
		if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 299) {
			throw new Exception("Failed to update organizatoin. Status: " + response.getStatusCode().toString() + " " + response.getStatusCode().getReasonPhrase() + " " + response.getBody());
		}
	}

	private HttpHeaders getHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apiKey", apiKey);
		headers.add("Content-Type", "application/json");

		return headers;
	}
}
