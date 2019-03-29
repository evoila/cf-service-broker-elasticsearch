package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.User;
import de.evoila.cf.broker.model.catalog.plan.CustomInstanceGroupConfig;
import de.evoila.cf.broker.model.catalog.plan.InstanceGroupConfig;
import de.evoila.cf.broker.model.catalog.plan.Metadata;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.util.MapUtils;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;
import org.assertj.core.util.Sets;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public abstract class BaseElasticsearchDeploymentManager extends DeploymentManager {

    public static final Set<String> INSTANCE_GROUPS = Sets.newLinkedHashSet(
            "elasticsearch",
            "ingest_nodes",
            "coordinating_nodes",
            "general_nodes",
            "machine_learning_nodes",
            "data_nodes",
            "master_eligible_nodes"
    );

    public final static String MASTER_ELIGIBLE_NODES = "master_eligible_nodes";
    public final static String COORDINATING_NODES = "coordinating_nodes";
    public final static String DATA_NODES = "data_nodes";
    public final static String INGEST_NODES = "ingest_nodes";
    public final static String MACHINE_LEARNING_NODES = "machine_learning_nodes";
    public final static String GENERAL_NODES = "general_nodes";

    BaseElasticsearchDeploymentManager(BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan, Map<String, Object> customParameters,
                                     boolean isUpdate) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.putAll(plan.getMetadata().getCustomParameters());

        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        Map<String, Object> manifestProperties = manifest.getInstanceGroups()
                .stream()
                .filter(i -> INSTANCE_GROUPS.contains(i.getName()))
                .findAny().get().getProperties();

        this.extractPlugins(plan);
        this.updateInstanceGroupConfiguration(manifest, plan);

        final String elasticsearchPassword = generatePassword();
        final String kibanaPassword = generatePassword();
        final String logstashSystemPassword = generatePassword();
        final String drainMonitoringPassword = generatePassword();

        final List<User> users = serviceInstance.getUsers();
        users.add(new User("elastic", elasticsearchPassword));
        users.add(new User("kibana", kibanaPassword));
        users.add(new User("logstash_system", logstashSystemPassword));

        manifest.getInstanceGroups().forEach(instanceGroup -> {
            final Map<String, Object> instanceGroupProperties = instanceGroup.getProperties();
            MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.cluster_name", "elasticsearch-" + serviceInstance.getId());
            MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.elastic.password", elasticsearchPassword);
            MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.kibana.password", kibanaPassword);
            MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.logstash_system.password", logstashSystemPassword);
            MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.drain-monitor.password", drainMonitoringPassword);
        });
    }

    private void extractPlugins(Plan plan) {
        final Object elasticsearchPropertiesRaw = plan.getMetadata().getProperties().get("elasticsearch");
        if (elasticsearchPropertiesRaw instanceof  Map) {
            final Map<String, Object> elasticsearchProperties = (Map<String, Object>) elasticsearchPropertiesRaw;

            final Object pluginsRaw =  elasticsearchProperties.get("plugins");
            if (pluginsRaw instanceof Map) {
                final Map<String, Object> pluginsMap = (Map<String, Object>) pluginsRaw;

                for (Object key : pluginsMap.keySet()) {
                    if(pluginsMap.get(key) instanceof Map) {
                        elasticsearchProperties.put("plugins", pluginsMap.get(key));
                    } else {
                        elasticsearchProperties.put("plugins", pluginsMap);
                    }
                }
            }
        }
    }

    private int extractMinimumMasterNodes(Plan plan) {
        final Object elasticsearchPropertiesRaw = plan.getMetadata().getProperties().get("elasticsearch");
        if (elasticsearchPropertiesRaw instanceof  Map) {
            final Map<String, Object> elasticsearchProperties = (Map<String, Object>) elasticsearchPropertiesRaw;

            final Object discoveryRaw =  elasticsearchProperties.get("discovery");
            if (discoveryRaw instanceof Map) {
                final Map<String, Object> discoveryMap = (Map<String, Object>) discoveryRaw;

                return (int) discoveryMap.get("minimum_master_nodes");
            }
        }
        return 1;
    }

    @Override
    protected void updateInstanceGroupConfiguration(Manifest manifest, Plan plan) {
        super.updateInstanceGroupConfiguration(manifest, plan);

        Metadata metadata = plan.getMetadata();

        for(InstanceGroup instanceGroup : manifest.getInstanceGroups()) {
            if(metadata != null) {
                updateProvidesAndConsumes(instanceGroup, metadata);


                InstanceGroupConfig instanceGroupConfig = metadata.getInstanceGroupConfig().stream()
                        .filter(i -> i.getName() != null && i.getName().equals(instanceGroup.getName()))
                        .findFirst()
                        .orElse(null);

                if(metadata.getInstanceGroupConfig() != null && instanceGroupConfig != null) {
                    updateProvidesAndConsumes(instanceGroup, instanceGroupConfig);
                }
            }
        }
    }

    private void updateProvidesAndConsumes(InstanceGroup instanceGroup, InstanceGroupConfig instanceGroupConfig) {
        if(instanceGroupConfig instanceof CustomInstanceGroupConfig) {
            CustomInstanceGroupConfig customInstanceGroupConfig = (CustomInstanceGroupConfig) instanceGroupConfig;

            if (customInstanceGroupConfig.getProvides() != null) {
                instanceGroup.getJobs().stream()
                        .filter(i -> i.getName() != null && i.getName().equals("elasticsearch"))
                        .findFirst()
                        .get()
                        .setProvides(customInstanceGroupConfig.getProvides());

                // change consumes of smoke-test
                HashMap<String, Object> consumesMap = new HashMap<>();

                customInstanceGroupConfig.getProvides().entrySet().forEach(m -> {
                    HashMap<String, Object> fromMap = new HashMap<>();

                    if (m.getValue() instanceof HashMap && m.getValue() != null) {
                        HashMap<String, Object> asMap = (HashMap<String, Object>) m.getValue();
                        fromMap.put("from", asMap.get("as"));

                        consumesMap.put(m.getKey(), fromMap);
                    }
                });

                JobV2 smokeTests = instanceGroup.getJobs().stream()
                        .filter(i -> i.getName() != null && i.getName().equals("smoke-tests"))
                        .findFirst()
                        .orElse(null);

                smokeTests.setConsumes(consumesMap);
            }

            if (customInstanceGroupConfig.getConsumes() != null)
                instanceGroup.getJobs().forEach(m -> m.setConsumes(customInstanceGroupConfig.getConsumes()));
        }
    }

    private String generatePassword() {
        final SecureRandom random = new SecureRandom();
        final String password = new BigInteger(130, random).toString(32);
        return password;
    }
}
