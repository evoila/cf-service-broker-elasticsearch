package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import org.assertj.core.util.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    ElasticsearchDeploymentManager(BoshProperties boshProperties) {
        super(boshProperties);
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

        this.updateInstanceGroupConfiguration(manifest, plan);
    }
}
