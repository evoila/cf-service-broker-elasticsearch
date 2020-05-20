package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.CustomInstanceGroupConfig;
import de.evoila.cf.broker.model.catalog.plan.InstanceGroupConfig;
import de.evoila.cf.broker.model.catalog.plan.Metadata;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.custom.constants.CredentialConstants;
import de.evoila.cf.broker.util.MapUtils;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;
import de.evoila.cf.security.credentials.CredentialStore;
import de.evoila.cf.security.credentials.DefaultCredentialConstants;
import de.evoila.cf.security.credentials.credhub.CredhubClient;
import org.assertj.core.util.Sets;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    public final static String BACKUP_AGENT_JOB_NAME = "backup-agent";

    private final CredentialStore credentialStore;

    BaseElasticsearchDeploymentManager(BoshProperties boshProperties, Environment env, CredentialStore credentialStore) {
        super(boshProperties, env);
        this.credentialStore = credentialStore;
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan, Map<String, Object> customParameters,
                                     boolean isUpdate) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.putAll(plan.getMetadata().getCustomParameters());

        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        if(isUpdate) {
            if(customParameters != null && !customParameters.isEmpty()) {
                Map<String, Object> elasticsearch = (Map<String, Object>) customParameters.get("elasticsearch");
                Map<String, Object> backup = (Map<String, Object>) elasticsearch.get("backup");

                if(!backup.isEmpty()) {
                    credentialStore.createUser(serviceInstance, CredentialConstants.S3_BACKUP_CREDENTIALS, backup.get("access_key").toString(), backup.get("secret_key").toString());

                    manifest.getInstanceGroups().forEach(instanceGroup -> {
                        final Map<String, Object> instanceGroupProperties = instanceGroup.getProperties();
                        MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.backup.s3.client.default.access_key", "((" + CredentialConstants.S3_BACKUP_CREDENTIALS + ".username))");
                        MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.backup.s3.client.default.secret_key", "((" + CredentialConstants.S3_BACKUP_CREDENTIALS + ".password))");
                        MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.backup.s3.client.default.bucket_name", backup.get("bucket_name").toString());
                        MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.backup.s3.client.default.repository_name", backup.get("repository_name").toString());
                    });
                } else {
                    manifest.getInstanceGroups().forEach(instanceGroup ->  {
                        final Map<String, Object> instanceGroupProperties = instanceGroup.getProperties();
                        Map<String, Object> deployedElasticsearchProperties = (Map<String, Object>) instanceGroupProperties.get("elasticsearch");
                        deployedElasticsearchProperties.remove(backup);
                    });
                }
            }

            this.updateInstanceGroupConfiguration(manifest, plan);
        } else {
            this.extractPlugins(plan);
            this.updateInstanceGroupConfiguration(manifest, plan);

            // Add user to credential store
            credentialStore.createUser(serviceInstance, CredentialConstants.SUPER_ADMIN, CredentialConstants.SUPER_ADMIN);
            credentialStore.createUser(serviceInstance, CredentialConstants.KIBANA_USER, CredentialConstants. KIBANA_USER);
            credentialStore.createUser(serviceInstance, CredentialConstants.LOGSTASH_USER, CredentialConstants.LOGSTASH_USER);
            credentialStore.createUser(serviceInstance, CredentialConstants.DRAIN_MONITOR_USER, CredentialConstants.DRAIN_MONITOR_USER);
            credentialStore.createUser(serviceInstance, DefaultCredentialConstants.BACKUP_AGENT_CREDENTIALS, CredentialConstants.BACKUP_AGENT_USER);
            credentialStore.createUser(serviceInstance, DefaultCredentialConstants.BACKUP_CREDENTIALS, CredentialConstants.SUPER_ADMIN);

            if (credentialStore instanceof CredhubClient) {
                manifest.getInstanceGroups().forEach(instanceGroup -> {
                    final Map<String, Object> instanceGroupProperties = instanceGroup.getProperties();
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.cluster_name", "elasticsearch-" + serviceInstance.getId());
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.elastic.password", "((" + CredentialConstants.SUPER_ADMIN + ".password))");
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.kibana.password", "((" + CredentialConstants.KIBANA_USER + ".password))");
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.logstash_system.password", "((" + CredentialConstants.LOGSTASH_USER + ".password))");
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.drain-monitor.password", "((" + CredentialConstants.DRAIN_MONITOR_USER + ".password))");

                    // Add Backup Agent credentials to manifest
                    if (instanceGroup.getJob(BACKUP_AGENT_JOB_NAME).isPresent()) {
                        JobV2 backupAgentJob = instanceGroup.getJob(BACKUP_AGENT_JOB_NAME).get();

                        MapUtils.deepInsert(backupAgentJob.getProperties(), "backup_agent.username", "((" + DefaultCredentialConstants.BACKUP_AGENT_CREDENTIALS + ".username))");
                        MapUtils.deepInsert(backupAgentJob.getProperties(), "backup_agent.password", "((" + DefaultCredentialConstants.BACKUP_AGENT_CREDENTIALS + ".password))");
                    }
                });
            } else {
                manifest.getInstanceGroups().forEach(instanceGroup -> {
                    final Map<String, Object> instanceGroupProperties = instanceGroup.getProperties();
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.cluster_name", "elasticsearch-" + serviceInstance.getId());
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.elastic.password", credentialStore.getPassword(serviceInstance, CredentialConstants.SUPER_ADMIN));
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.kibana.password", credentialStore.getPassword(serviceInstance, CredentialConstants.KIBANA_USER));
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.logstash_system.password", credentialStore.getPassword(serviceInstance, CredentialConstants.LOGSTASH_USER));
                    MapUtils.deepInsert(instanceGroupProperties, "elasticsearch.xpack.users.reserved.drain-monitor.password", credentialStore.getPassword(serviceInstance, CredentialConstants.DRAIN_MONITOR_USER));

                    // Add Backup Agent credentials to manifest
                    if (instanceGroup.getJob(BACKUP_AGENT_JOB_NAME).isPresent()) {
                        JobV2 backupAgentJob = instanceGroup.getJob(BACKUP_AGENT_JOB_NAME).get();

                        MapUtils.deepInsert(backupAgentJob.getProperties(), "backup_agent.username", CredentialConstants.BACKUP_AGENT_USER);
                        MapUtils.deepInsert(backupAgentJob.getProperties(), "backup_agent.password", credentialStore.getPassword(serviceInstance, DefaultCredentialConstants.BACKUP_AGENT_CREDENTIALS));
                    }
                });
            }
        }
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
}
