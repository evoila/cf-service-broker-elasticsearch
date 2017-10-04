package de.evoila.cf.cpi.openstack.custom;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.persistence.mongodb.repository.ClusterStackMapping;
import de.evoila.cf.broker.persistence.mongodb.repository.StackMappingRepository;
import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 28.09.17.
 */
@Service
@ConditionalOnBean(OpenstackBean.class)
public class ElasticsearchCustomStackHandler extends CustomStackHandler {

    private static final String PRE_IP_TEMPLATE = "/openstack/pre-ips.yaml";
    private static final String PRE_VOLUME_TEMPLATE = "/openstack/pre-volume.yaml";

    private static final String MAIN_TEMPLATE = "/openstack/main.yaml";

    private static final String NAME_TEMPLATE = "elasticsearch-%s-%s";

    private static final String PORTS_KEY = "ports_ids";
    private static final String IP_ADDRESS_KEY = "port_ips";
    private static final String VOLUME_KEY = "volume_ids";

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchCustomStackHandler.class);

    private String keyPair;

    @Autowired
    private StackMappingRepository stackMappingRepo;

    @Autowired
    private OpenstackBean openstackBean;

    public ElasticsearchCustomStackHandler() {
        super();
    }

    @PostConstruct
    private void initValues() {
        keyPair = openstackBean.getKeypair();
    }

    @Override
    public void delete(String internalId) {
        ClusterStackMapping stackMapping = stackMappingRepo.findOne(internalId);

        if(stackMapping == null) {
            super.delete(internalId);
        } else {
            List<String> secondaryStacks = stackMapping.getSecondaryStacks();
            for(String stackId : secondaryStacks) {
                super.delete(stackId);
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            super.delete(stackMapping.getPrimaryStack());
            super.delete(stackMapping.getPortsStack());
            stackMapping.getSecondaryStacks().forEach(s -> super.delete(s));
            stackMappingRepo.delete(stackMapping);
        }
    }

    @Override
    public String create(String instanceId, Map<String, String> customParameters) throws InterruptedException, PlatformException {
        log.debug(customParameters.toString());
        if(customParameters.containsKey(ParameterManager.CLUSTER_NAME)) {
            log.debug("Start creating cluster " + instanceId);
            ClusterStackMapping clusterStacks = createCluster(instanceId, customParameters);
            log.debug("End creating cluster " + instanceId);
            stackMappingRepo.save(clusterStacks);
            return clusterStacks.getId();
        }
        log.debug("Not creating a cluster " + instanceId);
        return super.create(instanceId, customParameters);
    }

    private ClusterStackMapping createCluster(String instanceId, Map<String, String> customParemeters) throws PlatformException {
        log.debug("Start create a Elasticsearch cluster");
        customParemeters.putAll(defaultParameters());
        customParemeters.putAll(generateValues(instanceId));

        ClusterStackMapping stackMapping = new ClusterStackMapping();
        stackMapping.setId(instanceId);

        Stack ipStack = createPreIpStack(instanceId, customParemeters);
        stackMapping.setPortsStack(ipStack.getId());
        stackMappingRepo.save(stackMapping);

        List<String>[] responses = extractResponses(ipStack, PORTS_KEY, IP_ADDRESS_KEY);
        List<String> ips = responses[1];
        List<String> ports = responses[0];

        for(int i = 0; i < ips.size(); i++) {
            String ip = ips.get(i);
            stackMapping.addServerAddress(new ServerAddress("node-" + i, ip, 9300));
        }

        ParameterManager.updatePortParameters(customParemeters, ips, ports);
        Stack preVolumeStack = createPreVolumeStack(instanceId, customParemeters);
        stackMapping.setVolumeStack(preVolumeStack.getId());
        stackMappingRepo.save(stackMapping);

        responses = extractResponses(preVolumeStack, VOLUME_KEY);
        List<String> volumes = responses[0];
        ParameterManager.updateVolumeParameters(customParemeters, volumes);

        Stack mainStack = createMainStack(instanceId, customParemeters);
        stackMapping.setPrimaryStack(mainStack.getId());
        stackMappingRepo.save(stackMapping);

        return stackMapping;
    }

    private Map<? extends String, ? extends String> generateValues(String instanceId) {
        HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put(ParameterManager.KEY_NAME, keyPair);
        return valueMap;
    }

    private Stack createPreIpStack(String instanceId, Map<String, String> customParameters) throws PlatformException {
        Map<String, String> parametersPreIp = ParameterManager.copyProperties(customParameters,
                ParameterManager.RESOURCE_NAME,
                ParameterManager.NODE_NUMBER,
                ParameterManager.NETWORK_ID,
                ParameterManager.SECURITY_GROUPS);

        String name = String.format(NAME_TEMPLATE, instanceId, "ip");
        parametersPreIp.put(ParameterManager.RESOURCE_NAME, name);

        String templatePorts = accessTemplate(PRE_IP_TEMPLATE);

        heatFluent.create(name, templatePorts, parametersPreIp, false, 10l);

        Stack preIpStack = stackProgressObserver.waitForStackCompletion(name);
        return preIpStack;
    }

    private Stack createPreVolumeStack(String instanceId, Map<String, String> customParameters) throws PlatformException {
        Map<String, String> parametersPreVolume = ParameterManager.copyProperties(customParameters,
                ParameterManager.RESOURCE_NAME,
                ParameterManager.NODE_NUMBER,
                ParameterManager.VOLUME_SIZE);

        String name = String.format(NAME_TEMPLATE, instanceId, "vol");
        parametersPreVolume.put(ParameterManager.RESOURCE_NAME, name);

        String templatePorts = accessTemplate(PRE_VOLUME_TEMPLATE);
        heatFluent.create(name, templatePorts, parametersPreVolume, false, 10l);
        Stack preVolumeStack = stackProgressObserver.waitForStackCompletion(name);
        return preVolumeStack;
    }

    private Stack createMainStack(String instanceId, Map<String, String> customParameters) throws PlatformException {
        Map<String, String> parametersMain = ParameterManager.copyProperties(customParameters,
                ParameterManager.RESOURCE_NAME,
                ParameterManager.IMAGE_ID,
                ParameterManager.KEY_NAME,
                ParameterManager.FLAVOR,
                ParameterManager.AVAILABILITY_ZONE,

                ParameterManager.NODE0_VOLUME_ID,
                ParameterManager.NODE0_PORT,
                ParameterManager.NODE0_IP,

                ParameterManager.NODE1_VOLUME_ID,
                ParameterManager.NODE1_PORT,
                ParameterManager.NODE1_IP,

                ParameterManager.NODE2_VOLUME_ID,
                ParameterManager.NODE2_PORT,
                ParameterManager.NODE2_IP,

                ParameterManager.CLUSTER_NAME);

        String name = String.format(NAME_TEMPLATE, instanceId, "cl");
        parametersMain.put(ParameterManager.RESOURCE_NAME, name);

        String template = accessTemplate(MAIN_TEMPLATE);
        heatFluent.create(name, template, parametersMain, false, 10l);
        Stack mainStack = stackProgressObserver.waitForStackCompletion(name);
        return mainStack;
    }

    private List<String>[] extractResponses(Stack stack, String... keys) {
        List<String>[] response = new List[keys.length];

        for(Map<String, Object> output : stack.getOutputs()) {
            Object outputKey = output.get("output_key");
            if(outputKey != null && outputKey instanceof String) {
                String key = (String) outputKey;
                for(int i = 0; i < keys.length; i++) {
                    if(key.equals(keys[i])) {
                        response[i] = (List<String>) output.get("output_value");
                    }
                }
            }
        }

        return response;
    }
}