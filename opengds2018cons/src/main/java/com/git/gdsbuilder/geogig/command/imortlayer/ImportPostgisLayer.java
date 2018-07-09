/**
 * 
 */
package com.git.gdsbuilder.geogig.command.imortlayer;

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
import org.springframework.web.client.RestTemplate;

import com.git.gdsbuilder.geogig.type.GeogigTasks;

/**
 * Geogig PostGIS Import Command Execution Class
 * 
 * @author GIT
 *
 */
public class ImportPostgisLayer {

	private static final Log logger = LogFactory.getLog(ImportPostgisLayer.class);

	private static final String command = "postgis/import";
	private static final String param_transactionId = "transactionId=";
	private static final String param_fidAttrib = "fidAttrib=";
	private static final String param_table = "table=";
	private static final String param_host = "host=";
	private static final String param_port = "port=";
	private static final String param_schema = "schema=";
	private static final String param_database = "database=";
	private static final String param_user = "user=";
	private static final String param_password = "password=";

	public GeogigTasks executeCommand(String baseURL, String username, String password, String repository,
			String transactionId, String fidAttrib, String table, String host, String port, String schema,
			String database, String dbUser, String dbPassword) {

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
		String url = baseURL + "/repos/" + repository + "/" + command + "?" + param_transactionId + transactionId + "&"
				+ param_fidAttrib + fidAttrib + "&" + param_table + table + "&" + param_host + host + "&" + param_port
				+ port + "&" + param_schema + schema + "&" + param_database + database + "&" + param_user + dbUser + "&"
				+ param_password + dbPassword;

		// request
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<GeogigTasks> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, GeogigTasks.class);
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
