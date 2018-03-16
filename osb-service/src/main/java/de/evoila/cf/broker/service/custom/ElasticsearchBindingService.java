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

    protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
                                                    List<ServerAddress> hosts) {
        String password = serviceInstance.getPassword();

        String formattedHosts = "";
        for (ServerAddress host : hosts) {
            if (formattedHosts != "")
                formattedHosts += ",";
            formattedHosts += String.format("%s:%d", host.getIp(), host.getPort());
        }

        String dbURL = String.format("elasticsearch://%s", formattedHosts);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("uri", dbURL);
        credentials.put("password", password);
        credentials.put("host", formattedHosts);
        credentials.put("port", hosts.get(0).getPort());

        return credentials;
    }

    @Override
    protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) {
        log.debug("unbind Service");
    }

    @Override
    public ServiceInstanceBinding getServiceInstanceBinding(String id) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.evoila.cf.broker.service.impl.BindingServiceImpl#createCredentials(
     * java.lang.String, de.evoila.cf.broker.model.ServiceInstance,
     * de.evoila.cf.broker.model.ServerAddress)
     */
    @Override
    protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
                                                    ServerAddress host, Plan plan) {
        List<ServerAddress> hosts = new ArrayList<>();
        hosts.add(host);

        return createCredentials(bindingId, serviceInstance, hosts);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindRoute(de.evoila.
     * cf.broker.model.ServiceInstance, java.lang.String)
     */
    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

}
