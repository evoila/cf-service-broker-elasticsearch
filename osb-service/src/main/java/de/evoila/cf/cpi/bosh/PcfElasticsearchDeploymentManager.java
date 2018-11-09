package de.evoila.cf.cpi.bosh;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.CustomInstanceGroupConfig;
import de.evoila.cf.broker.model.Plan;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author Michael Hahn
 */
@Profile("pcf")
@Component
public class PcfElasticsearchDeploymentManager extends BaseElasticsearchDeploymentManager {

    PcfElasticsearchDeploymentManager(Catalog catalog, BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);

        catalog.getServices().forEach(s -> s.getPlans().forEach(this::handleCustomParameters));
    }


    private void handleCustomParameters(Plan plan) {
        Object nodesRaw = plan.getMetadata().getCustomParameters().get("nodes");

        if(nodesRaw instanceof String) {
            NodeInformation nodeInformation = null;
            try {
                nodeInformation = new ObjectMapper().readValue((String)nodesRaw, NodeInformation.class);
            } catch (IOException e) {
                Log.error("Could not parse node information in custom parameters");
            }

            if(nodeInformation != null) {
                String value = nodeInformation.getValue();
                LinkedHashMap<String, Object> selectedOption = nodeInformation.getSelectedOption();

                if(value.equals("dedicate_nodes")) {
                    handleMultipleInstanceGroups(selectedOption, plan);
                } else {
                    handleSingleInstanceGroup(value, selectedOption, plan);
                }
            }
        }

        //TODO: handle plugins
        Object pluginsRaw = plan.getMetadata().getCustomParameters().get("plugins");
        if(pluginsRaw instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> pluginsAsMap = (LinkedHashMap<String, Object>) pluginsRaw;


        }
    }

    private void handleSingleInstanceGroup(String nodeName, LinkedHashMap<String, Object> selectedOption, Plan plan) {
        if(selectedOption != null) {
            Object nodeNumberRaw = selectedOption.get("node_number");

            int instances = 0;
            if(nodeNumberRaw instanceof String) {
                instances = Integer.parseInt((String) nodeNumberRaw);
            }
            updateNodeCount(nodeName, instances, plan);

            Object persistentDiskType = selectedOption.get("node_persistentdisktype");
            updatePersistentDiskType("node_persistentdisktype", persistentDiskType, plan);

            Object vmType = selectedOption.get("node_vmtype");
            updateVmType("node_vmtype", vmType, plan);
        }
    }

    private void handleMultipleInstanceGroups(LinkedHashMap<String, Object> selectedOption, Plan plan) {
        if(selectedOption != null) {

            selectedOption.forEach((key, value) -> {
                if (value instanceof String && key.contains("_nodes")) {
                    int nodeCountAsInt = Integer.parseInt((String) value);
                    updateNodeCount(key, nodeCountAsInt, plan);
                }

                if (value instanceof Integer && key.contains("_nodes")) {
                    int nodeCountAsInt = (Integer) value;
                    updateNodeCount(key, nodeCountAsInt, plan);
                }

                updatePersistentDiskType(key, value, plan);
                updateVmType(key, value, plan);
            });
        }

    }

    private void updateNodeCount(String nodeName, int count, Plan plan) {
        CustomInstanceGroupConfig instanceGroupConfig = new CustomInstanceGroupConfig();
        instanceGroupConfig.setName(nodeName);
        instanceGroupConfig.setNodes(count);

        plan.getMetadata().getInstanceGroupConfig().add(instanceGroupConfig);
    }

    private void updatePersistentDiskType(String key, Object diskType, Plan plan) {
        if (diskType instanceof String) {
            String diskTypeAsString = (String) diskType;

            if (key.equals("node_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("general_nodes"))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("coordinating_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("coordinating_nodes"))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("data_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("data_nodes"))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("master_eligible_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("master_eligible_nodes"))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("ingest_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("ingest_nodes"))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
        }
    }

    private void updateVmType(String key, Object vmType, Plan plan) {
        if (vmType instanceof String) {
            String vmTypeAsString = (String) vmType;

            if (key.equals("node_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("general_nodes"))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("coordinating_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("coordinating_nodes"))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("data_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("data_nodes"))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("master_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("master_eligible_nodes"))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("ingest_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals("ingest_nodes"))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
        }
    }
}
