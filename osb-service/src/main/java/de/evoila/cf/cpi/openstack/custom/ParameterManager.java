package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 28.09.17.
 */
public class ParameterManager {

    public static final String RESOURCE_NAME = "resource_name";
    public static final String NODE_NUMBER = "node_number";
    public static final String IMAGE_ID = "image_id";
    public static final String KEY_NAME = "key_name";
    public static final String FLAVOR = "flavor";
    public static final String AVAILABILITY_ZONE = "availability_zone";
    public static final String VOLUME_SIZE = "volume_size";
    public static final String NETWORK_ID = "network_id";
    public static final String SECURITY_GROUPS = "security_groups";
    public static final String PRIMARY_VOLUME_ID = "primary_volume_id";
    public static final String PRIMARY_PORT = "primary_port";
    public static final String PRIMARY_IP = "primary_ip";
    public static final String SECONDARY_PORTS = "secondary_ports";
    public final static String SECONDARY_VOLUME_IDS = "secondary_volume_ids";
    public final static String SECONDARY_IPS = "secondary_ips";
    public final static String NODE0_VOLUME_ID = "node0_volume_id";
    public final static String NODE0_PORT = "node0_port";
    public final static String NODE0_IP = "node0_ip";
    public final static String NODE1_VOLUME_ID = "node1_volume_id";
    public final static String NODE1_PORT = "node1_port";
    public final static String NODE1_IP = "node1_ip";
    public final static String NODE2_VOLUME_ID = "node2_volume_id";
    public final static String NODE2_PORT = "node2_port";
    public final static String NODE2_IP = "node2_ip";
    public final static String CLUSTER_NAME = "cluster_name";

    static void updatePortParameters(Map<String, String> customParameters, List<String> ips, List<String> ports) {
        String primIp = ips.get(0);
        ips.remove(0);
        String primPort = ports.get(0);
        ports.remove(0);

        customParameters.put(PRIMARY_IP, primIp);
        customParameters.put(PRIMARY_PORT, primPort);

        customParameters.put(SECONDARY_IPS, join(ips));
        customParameters.put(SECONDARY_PORTS, join(ports));
    }

    static void updateVolumeParameters(Map<String, String> customParameters, List<String> volumes) {
        String primaryVolume = volumes.get(0);
        volumes.remove(0);

        customParameters.put(PRIMARY_VOLUME_ID, primaryVolume);

        customParameters.put(SECONDARY_VOLUME_IDS, join(volumes));
    }

    private static String join(List<String> list) {
        return String.join(",", list);
    }

    static String getSecondaryNumber(Map<String, String> customParameters) {
        return String.valueOf(2);
    }

    static Map<String, String> copyProperties(Map<String, String> completeList, String... keys) {
        Map<String, String> copiedProps = new HashMap<>();

        for(int i = 0; i < keys.length; i++) {
            String key = keys[i];
            copiedProps.put(key, completeList.get(key));
        }

        return copiedProps;
    }
}
