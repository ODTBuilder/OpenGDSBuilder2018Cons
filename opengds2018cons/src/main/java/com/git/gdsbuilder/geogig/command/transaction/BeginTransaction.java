/**
 * 
 */
package com.git.gdsbuilder.geogig.command.transaction;

import java.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.git.gdsbuilder.geogig.type.GeogigTransaction;

/**
 * Geogig BeginTransaction Command Execution Class
 * 
 * @author GIT
 *
 */
public class BeginTransaction {

	private static final Log logger = LogFactory.getLog(BeginTransaction.class);

	private static final String command = "beginTransaction";

	public GeogigTransaction executeCommand(String baseURL, String username, String password, String repository) {

		// restTemplate
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(5000);
		factory.setConnectTimeout(3000);

		factory.setReadTimeout(5000);
		factory.setConnectTimeout(3000);
		CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(100).setMaxConnPerRoute(5).build();
		factory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(factory);

		// header
		HttpHeaders headers = new HttpHeaders();
		String user = username + ":" + password;
		String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(user.getBytes());
		headers.setContentType(MediaType.APPLICATION_XML);
		headers.add("Authorization", encodedAuth);

		// url
		String url = baseURL + "/repos/" + repository + "/" + command;

		// request
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<GeogigTransaction> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, GeogigTransaction.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (responseEntity != null) {
			HttpStatus statusCode = responseEntity.getStatusCode();
			logger.info(responseEntity.getStatusCodeValue() + ":" + statusCode.getReasonPhrase());
			return responseEntity.getBody();
		} else {
			return null;
		}
	}
}
