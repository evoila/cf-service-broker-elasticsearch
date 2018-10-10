package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.User;
import de.evoila.cf.broker.util.MapUtils;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import org.assertj.core.util.Sets;
import org.springframework.core.env.Environment;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class ElasticsearchDeploymentManager extends DeploymentManager {

    public static final Set<String> INSTANCE_GROUPS = Sets.newLinkedHashSet(
            "elasticsearch",
            "ingest_nodes",
            "coordinating_nodes",
            "general_nodes",
            "machine_learning_nodes",
            "data_nodes",
            "master_eligible_nodes"
    );

    ElasticsearchDeploymentManager(BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan, Map<String, Object> customParameters) {
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

            instanceGroup
                    .getJobs().stream()
                    .filter(j -> j.getName().equals("smoke-tests"))
                    .findFirst()
                    .ifPresent(j -> {
                        Map<String, Object> jobProperties = j.getProperties();
                        if (jobProperties == null) {
                            jobProperties = new HashMap<>();
                            j.setProperties(jobProperties);
                        }
                        MapUtils.deepInsert(jobProperties, "elasticsearch.username", "elastic");
                        MapUtils.deepInsert(jobProperties, "elasticsearch.password", elasticsearchPassword);
                    });
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

    private String generatePassword() {
        final SecureRandom random = new SecureRandom();
        final String password = new BigInteger(130, random).toString(32);
        return password;
    }
}
