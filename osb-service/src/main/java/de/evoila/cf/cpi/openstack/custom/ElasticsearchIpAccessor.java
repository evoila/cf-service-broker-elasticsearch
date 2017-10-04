package de.evoila.cf.cpi.openstack.custom;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import jersey.repackaged.com.google.common.collect.Lists;
import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 28.09.17.
 */
@Service
@Primary
@ConditionalOnBean(HeatFluent.class)
public class ElasticsearchIpAccessor extends CustomIpAccessor {
    @Autowired
    private HeatFluent heatFluent;

    @Autowired
    private DefaultIpAccessor defaultIpAccessor;

    @Override
    public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException {
        Stack stack = heatFluent.get(HeatFluent.uniqueName(instanceId));
        List<Map<String, Object>> outputs = stack.getOutputs();

        if (outputs == null || outputs.isEmpty()) {
            return defaultIpAccessor.getIpAddresses(instanceId);
        }

        List<ServerAddress> serverAddresses = Lists.newArrayList();
        for (Map<String, Object> output : outputs) {
            Object outputKey = output.get("output_key");
            if (outputKey != null && outputKey instanceof String) {
                String key = (String) outputKey;
                if (key.equals("sec_ips")) {
                    List<String> outputValue = (List<String>) output.get("output_value");

                    for (int i = 0; i < outputValue.size(); i++) {
                        serverAddresses
                                .add(new ServerAddress("sec_ips" + "#" + Integer.toString(i), outputValue.get(i)));
                    }
                } else if (key.equals("prim_ip")) {
                    String outputValue = (String) output.get("output_value");

                    serverAddresses.add(new ServerAddress("prim_ip", outputValue));
                }
            }
        }

        return serverAddresses;
    }
}
