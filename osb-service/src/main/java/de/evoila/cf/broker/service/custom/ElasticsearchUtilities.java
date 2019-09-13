package de.evoila.cf.broker.service.custom;

import de.evoila.cf.broker.model.catalog.plan.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hahn
 */
public class ElasticsearchUtilities {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchUtilities.class);
    private static final String PROPERTIES_HTTPS_ENABLED = "elasticsearch.xpack.security.http.ssl.enabled";
    private static final String PROPERTIES_X_PACK_ENABLED = "elasticsearch.xpack.security.enabled";

    /**
     * Checks if x-pack is enabled in elasticsearch properties.
     *
     * @param plan the plan
     * @return true if x-pack is enabled, false otherwise
     */
    public static boolean planContainsXPack(Plan plan) {
        Object XPackProperyRaw;
        try {
            XPackProperyRaw = extractProperty(plan.getMetadata().getProperties(), PROPERTIES_X_PACK_ENABLED);
        } catch (IllegalArgumentException e) {
            log.error("Property " + PROPERTIES_X_PACK_ENABLED + " is missing for plan " + plan.getName(), e);
            return false;
        }

        if (XPackProperyRaw instanceof String) {
            return Boolean.parseBoolean((String) XPackProperyRaw);
        }

        return XPackProperyRaw instanceof Boolean && ((Boolean) XPackProperyRaw);
    }

    /**
     * Checks if HTTPS/TLS is enabled in elasticsearch properties.
     *
     * @param plan the plan
     * @return true if HTTPS/TLS is enabled, false otherwise
     */
    public static boolean isHttpsEnabled(Plan plan) {
        Object pluginsRaw;
        try {
            pluginsRaw = extractProperty(plan.getMetadata().getProperties(), PROPERTIES_HTTPS_ENABLED);
        } catch (IllegalArgumentException e) {
            log.error("Property " + PROPERTIES_HTTPS_ENABLED + " is missing for plan " + plan.getName() + ". Using default: HTTP");
            return false;
        }

        if (pluginsRaw instanceof String) {
            return Boolean.parseBoolean((String) pluginsRaw);
        }

        return pluginsRaw instanceof Boolean && (Boolean) pluginsRaw;
    }

    private static Object extractProperty(Object elasticsearchPropertiesRaw, String key) {
        final List<String> keyElements = Arrays.asList(key.split("\\."));

        return extractProperty(elasticsearchPropertiesRaw, keyElements);
    }

    private static Object extractProperty(Object elasticsearchPropertiesRaw, List<String> keyElements) {
        if (elasticsearchPropertiesRaw instanceof Map) {
            final Map<String, Object> elasticsearchProperties = ((Map<String, Object>) elasticsearchPropertiesRaw);

            final Object pluginPropertiesRaw = elasticsearchProperties.get(keyElements.get(0));


            if (keyElements.size() == 1) {
                return pluginPropertiesRaw;
            } else {

                if (pluginPropertiesRaw == null) {
                    throw new IllegalArgumentException("Could not access property. Intermediate map is missing.");
                }

                final List<String> nextStepSelectors = keyElements.subList(1, keyElements.size());
                return extractProperty(pluginPropertiesRaw, nextStepSelectors);
            }
        }
        throw new IllegalArgumentException("Wrong parameter format. Argument was not of type Map.");
    }
}
