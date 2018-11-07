package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Michael Hahn
 */
@Profile("!pcf")
@Component
public class ElasticsearchDeploymentManager extends BaseElasticsearchDeploymentManager {
    ElasticsearchDeploymentManager(BoshProperties boshProperties, Environment env) {
        super(boshProperties, env);
    }
}
