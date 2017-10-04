/**
 * 
 */
package de.evoila.cf.broker.service;


import de.evoila.cf.broker.bean.HAProxyConfiguration;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.HABackendResponse;
import de.evoila.cf.broker.model.HAProxyServerAddress;
import de.evoila.cf.broker.model.Mode;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.config.security.AcceptSelfSignedClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Christian Brinker, Sebastian Boeing, evoila.
 *
 */
public abstract class HAProxyService {

	private static final String APPLICATION_JSON = "application/json";

	private static final String CONTENT_TYPE = "Content-Type";

	private static final String X_AUTH_TOKEN_HEADER = "X-Auth-Token";
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private HAProxyConfiguration haProxyConfiguration;
	
	private String haProxy;
	
	private String authToken;

	private RestTemplate restTemplate = new RestTemplate();

	private HttpHeaders headers = new HttpHeaders();

	@PostConstruct
	private void initHeaders() {
		haProxy = haProxyConfiguration.getUri();
		authToken = haProxyConfiguration.getAuthToken();
		headers.add(X_AUTH_TOKEN_HEADER, authToken);
		headers.add(CONTENT_TYPE, APPLICATION_JSON);
		
	}

	@ConditionalOnBean(AcceptSelfSignedClientHttpRequestFactory.class)
	@Autowired(required = false)
	private void selfSignedRestTemplate(AcceptSelfSignedClientHttpRequestFactory requestFactory) {
		restTemplate.setRequestFactory(requestFactory);
	}

	public List<ServerAddress> appendAgent(List<ServerAddress> internalAddresses, String bindingId, String instanceId) throws ServiceBrokerException {
		List<ServerAddress> externalAddresses = internalAddresses.stream().map(in -> new HAProxyServerAddress(in, getMode(in), getOptions(in))).map(in -> appendSingleAgent(in, bindingId, instanceId))
				.filter(in -> in != null).collect(Collectors.toList());

		if (externalAddresses.size() < internalAddresses.size())
			throw new ServiceBrokerException("Could not provide external IPs for all parts of the service instance.");

		return externalAddresses;
	}

	private ServerAddress appendSingleAgent(HAProxyServerAddress internalAddress, String bindingId, String instanceId) {
		HAProxyServerAddress bindingAddress = new HAProxyServerAddress(internalAddress, bindingId);
		
		HttpEntity<HAProxyServerAddress> entity = new HttpEntity<>(bindingAddress, headers);
		try {
			HABackendResponse response = restTemplate.exchange(haProxy, HttpMethod.PUT, entity, HABackendResponse.class)
					.getBody();
			
			
			log.info("Called: " + haProxy);
			log.info("Response is: " + response);
			
			if (response != null) {
				ServerAddress serverAddress = new ServerAddress(internalAddress.getName(), response.getIp(), response.getPort());
				return serverAddress;
			}
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void removeAgent(List<ServerAddress> internalAddresses, String bindingId) throws ServiceBrokerException {
		for (ServerAddress internalAddress : internalAddresses) {
			removeSingleAgent(internalAddress, bindingId);
		}
	}

	private void removeSingleAgent(ServerAddress internalAddress, String bindingId) throws ServiceBrokerException {
		ServerAddress bindingAddress = new ServerAddress(bindingId, internalAddress.getIp(), internalAddress.getPort());
		HttpEntity<ServerAddress> entity = new HttpEntity<>(bindingAddress, headers);
		try {
			HttpStatus statusCode = restTemplate.exchange(haProxy, HttpMethod.DELETE, entity, Object.class)
					.getStatusCode();
			if (!statusCode.equals(HttpStatus.NO_CONTENT))
				throw new ServiceBrokerException("Could not remove external IP " + internalAddress.getName() + " - "
						+ internalAddress.getIp() + ":" + internalAddress.getPort());
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new ServiceBrokerException("Could not remove external IP " + internalAddress.getName() + " - "
					+ internalAddress.getIp() + ":" + internalAddress.getPort());
		}
	}
	
	public abstract Mode getMode(ServerAddress serverAddress);
	
	public abstract List<String> getOptions(ServerAddress serverAddress);
}
