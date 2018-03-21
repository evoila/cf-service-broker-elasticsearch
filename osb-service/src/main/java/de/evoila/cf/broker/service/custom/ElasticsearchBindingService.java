/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class ElasticsearchBindingService extends BindingServiceImpl {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static String URI = "uri";
    private static String USERNAME = "user";
    private static String PASSWORD = "password";
    private static String DATABASE = "database";
    private static String HOST = "host";
    private static String PORT = "port";

    protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
                                                    List<ServerAddress> hosts) {
        String formattedHosts = "";
        for (ServerAddress host : hosts) {
            if (formattedHosts.length() > 0)
                formattedHosts = formattedHosts.concat(",");
            formattedHosts += String.format("%s:%d", host.getIp(), host.getPort());
        }

        String dbURL = String.format("elasticsearch://%s", formattedHosts);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put(URI, dbURL);
        credentials.put(HOST, formattedHosts);
        credentials.put(PORT, hosts.get(0).getPort());

        return credentials;
    }

    @Override
    protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) {}

    @Override
    public ServiceInstanceBinding getServiceInstanceBinding(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
                                                    ServerAddress host, Plan plan) {
        List<ServerAddress> hosts = new ArrayList<>();
        hosts.add(host);

        return createCredentials(bindingId, serviceInstance, hosts);
    }

    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

}
