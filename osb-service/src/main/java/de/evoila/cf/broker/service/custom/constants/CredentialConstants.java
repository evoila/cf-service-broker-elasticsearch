package de.evoila.cf.broker.service.custom.constants;

/**
 * This class contains constants like built-in user names and other
 * credential related constants.
 *
 * @author Michael Hahn
 */
public class CredentialConstants {

    /**
     * A built-in superuser.
     */
    public static String SUPER_ADMIN = "elastic";

    /**
     * The user Kibana uses to connect and communicate with Elasticsearch.
     */
    public static String KIBANA_USER = "kibana";

    /**
     * The user Logstash uses when storing monitoring information in Elasticsearch.
     */
    public static String LOGSTASH_USER = "logstash_system";

    /**
     * The user the Beats use when storing monitoring information in Elasticsearch.
     */
    public static String BEATS_USER = "beats_system";

    /**
     * The user the APM server uses when storing monitoring information in Elasticsearch.
     */
    public static String APM_USER = "apm_system";

    /**
     * The user Metricbeat uses when collecting and storing monitoring information in Elasticsearch.
     */
    public static String REMOTE_MONITORING_USER = "remote_monitoring_user";

    /**
     * The user the drain monitor uses.
     */
    public static String DRAIN_MONITOR_USER = "drain_monitor";

    /**
     * The user for connecting to the backup agent.
     */
    public static String BACKUP_AGENT_USER = "backup";
}
