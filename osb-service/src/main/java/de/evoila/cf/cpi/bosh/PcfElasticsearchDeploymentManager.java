package de.evoila.cf.cpi.bosh;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.plan.CustomInstanceGroupConfig;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.util.MapUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Michael Hahn
 */
@Profile("pcf")
@Component
public class PcfElasticsearchDeploymentManager extends BaseElasticsearchDeploymentManager {

    PcfElasticsearchDeploymentManager(Catalog catalog, BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);

        catalog.getServices().forEach(s -> s.getPlans().forEach(this::parseInstanceGroups));
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::parsePlugins));
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::determineAndSetEgressInstanceGroup));
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::determineAndSetIngressInstanceGroup));
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::setDatabaseProvidersAndConsumers));
    }


    private void parseInstanceGroups(Plan plan) {
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
    }

    private void parsePlugins(Plan plan) {
        Object pluginsRaw = plan.getMetadata().getCustomParameters().get("plugins");

        if (pluginsRaw instanceof String) {
            final String pluginsAsString = (String) pluginsRaw;
            final ObjectMapper mapper = new ObjectMapper();

            List<PluginInformation> pluginList = null;
            try {
                pluginList = mapper.readValue(pluginsAsString, new TypeReference<List<PluginInformation>>(){});
            } catch (IOException e) {
                Log.error("Could not parse plugin information in custom parameters");
            }

            if (pluginList != null) {
                pluginList.forEach(p -> {
                    final String pluginName = p.getName();
                    if (pluginName != null && !pluginName.isEmpty()) {

                        final String pluginSource = (p.getSource() == null || p.getSource().isEmpty()) ? pluginName : p.getSource();

                        MapUtils.deepInsert(plan.getMetadata().getProperties(), "elasticsearch.plugins." + pluginName, pluginSource);
                    }
                });
            }
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

            updateNodeCount(GENERAL_NODES, 0, plan);
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
                        .filter(g -> g.getName().equals(GENERAL_NODES))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("coordinating_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(COORDINATING_NODES))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("data_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(DATA_NODES))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("master_eligible_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(MASTER_ELIGIBLE_NODES))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
            if (key.equals("ingest_persistentdisktype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(INGEST_NODES))
                        .findFirst().ifPresent(config -> config.setPersistentDiskType(diskTypeAsString));
            }
        }
    }

    private void updateVmType(String key, Object vmType, Plan plan) {
        if (vmType instanceof String) {
            String vmTypeAsString = (String) vmType;

            if (key.equals("node_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(GENERAL_NODES))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("coordinating_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(COORDINATING_NODES))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("data_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(DATA_NODES))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("master_eligible_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(MASTER_ELIGIBLE_NODES))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
            if (key.equals("ingest_vmtype")) {
                plan.getMetadata().getInstanceGroupConfig().stream()
                        .filter(g -> g.getName().equals(INGEST_NODES))
                        .findFirst().ifPresent(config -> config.setVmType(vmTypeAsString));
            }
        }
    }

    private void determineAndSetEgressInstanceGroup(Plan plan) {
        CustomInstanceGroupConfig coordinatingNodes = plan.getMetadata().getInstanceGroupConfig()
                .stream()
                .filter(g -> g.getName().equals(COORDINATING_NODES) && g.getNodes() > 0)
                .findFirst()
                .orElse(null);

        if (coordinatingNodes != null) {
            plan.getMetadata().setEgressInstanceGroup(COORDINATING_NODES);
            return;
        }

        CustomInstanceGroupConfig generalNodes = plan.getMetadata().getInstanceGroupConfig()
                .stream()
                .filter(g -> g.getName().equals(GENERAL_NODES) && g.getNodes() > 0)
                .findFirst()
                .orElse(null);

        if (generalNodes != null) {
            plan.getMetadata().setEgressInstanceGroup(GENERAL_NODES);
            return;
        }

        plan.getMetadata().setEgressInstanceGroup(DATA_NODES);
    }

    private void determineAndSetIngressInstanceGroup(Plan plan) {
        CustomInstanceGroupConfig coordinatingNodes = plan.getMetadata().getInstanceGroupConfig()
                .stream()
                .filter(g -> g.getName().equals(INGEST_NODES) && g.getNodes() > 0)
                .findFirst()
                .orElse(null);

        if (coordinatingNodes != null) {
            plan.getMetadata().setIngressInstanceGroup(INGEST_NODES);
            return;
        }

        CustomInstanceGroupConfig generalNodes = plan.getMetadata().getInstanceGroupConfig()
                .stream()
                .filter(g -> g.getName().equals(GENERAL_NODES) && g.getNodes() > 0)
                .findFirst()
                .orElse(null);

        if (generalNodes != null) {
            plan.getMetadata().setIngressInstanceGroup(GENERAL_NODES);
            return;
        }

        plan.getMetadata().setIngressInstanceGroup(DATA_NODES);

    }

    private void setDatabaseProvidersAndConsumers(Plan plan) {
        final CustomInstanceGroupConfig generalNodes = plan.getMetadata().getInstanceGroupConfig()
                .stream()
                .filter(c -> c.getName().equals(GENERAL_NODES))
                .findFirst()
                .orElse(null);

        if (generalNodes != null && generalNodes.getNodes() == 0) {
            final CustomInstanceGroupConfig masterEligibleNodes = plan.getMetadata().getInstanceGroupConfig()
                    .stream()
                    .filter(c -> c.getName().equals(MASTER_ELIGIBLE_NODES))
                    .findFirst()
                    .orElse(null);

            setProvidesForInstanceGroup(generalNodes, GENERAL_NODES);
            setProvidesForInstanceGroup(masterEligibleNodes, "discovery_nodes");
        }
    }

    private void setProvidesForInstanceGroup(CustomInstanceGroupConfig instanceGroupConfig, String as) {
        if (instanceGroupConfig != null && as != null) {
            final LinkedHashMap<String, Object> asGeneralNodes = new LinkedHashMap<>();
            asGeneralNodes.put("as", as);

            final LinkedHashMap<String, Object> database = new LinkedHashMap<>();
            database.put("database", asGeneralNodes);

            instanceGroupConfig.setProvides(database);
        }
    }
}
