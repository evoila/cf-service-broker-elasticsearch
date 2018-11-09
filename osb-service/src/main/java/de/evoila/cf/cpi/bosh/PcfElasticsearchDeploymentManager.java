package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @author Michael Hahn
 */
@Profile("pcf")
@Component
public class PcfElasticsearchDeploymentManager extends BaseElasticsearchDeploymentManager {

    PcfElasticsearchDeploymentManager(BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);
    }

    @Override
    protected void handleCustomParameters(Manifest manifest, Plan plan) {
        Object nodesRaw = plan.getMetadata().getCustomParameters().get("nodes");

        if(nodesRaw instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> nodesAsMap = (LinkedHashMap<String, Object>) nodesRaw;
            Object valueRaw = nodesAsMap.get("value");

            if(valueRaw instanceof String) {
                String valueAsString = (String) valueRaw;
                Object selectedOptionRaw = nodesAsMap.get("selected_option");

                if(valueAsString.equals("dedicate_nodes")) {
                    handleMultipleInstanceGroups(selectedOptionRaw, manifest);
                } else {
                    handleSingleInstanceGroup(valueAsString, selectedOptionRaw, manifest);
                }
            }
        }

        //TODO: handle plugins
        Object pluginsRaw = plan.getMetadata().getCustomParameters().get("plugins");
        if(pluginsRaw instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> pluginsAsMap = (LinkedHashMap<String, Object>) pluginsRaw;
        }
    }

    private void handleSingleInstanceGroup(String nodeName, Object selectedOption, Manifest manifest) {
        int instances = 0;
        if(selectedOption instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> selectedOptionMap = (LinkedHashMap<String, Object>) selectedOption;

            Object nodeNumberRaw = selectedOptionMap.get("node_number");

            if(nodeNumberRaw instanceof String) {
                instances = Integer.parseInt((String) nodeNumberRaw);
            }
        }

        updateNodeCount(nodeName, instances, manifest);

        manifest.getInstanceGroups().stream()
                .filter((g -> !g.getName().equals(nodeName)))
                .forEach(i -> i.setInstances(0));
    }

    private void handleMultipleInstanceGroups(Object selectedOption, Manifest manifest) {
        if(selectedOption instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> selectedOptionMap = (LinkedHashMap<String, Object>) selectedOption;

            selectedOptionMap.forEach((key, value) -> {
                if (value instanceof String) {
                    int nodeCountAsInt = Integer.parseInt((String) value);
                    updateNodeCount(key, nodeCountAsInt, manifest);
                }

                if (value instanceof Integer) {
                    int nodeCountAsInt = (Integer) value;
                    updateNodeCount(key, nodeCountAsInt, manifest);
                }
            });
        }

    }

    private void updateNodeCount(String nodeName, int count, Plan plan) {
        CustomInstanceGroupConfig instanceGroupConfig = new CustomInstanceGroupConfig();
        instanceGroupConfig.setName(nodeName);
        instanceGroupConfig.setNodes(count);

        plan.getMetadata().getInstanceGroupConfig().add(instanceGroupConfig);
    }
    }
}
