/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom.cluster;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterParameterManager {

	public static final String RESOURCE_NAME = "resource_name";
	public static final String NODE_NUMBER = "node_number";
	
	public static final String VOLUME_SIZE = "volume_size";
	public static final String KEY_NAME = "key_name";
	public static final String IMAGE_ID = "image_id";
	public static final String AVAILABILITY_ZONE = "availability_zone";	
	public static final String NETWORK_ID = "network_id";
	public static final String SECURITY_GROUPS = "security_groups";
	public static final String FLAVOR = "flavor";
	
	public static final String FLAVOUR = "flavor";
	public static final String SERVICE_DB = "service_db";
	public static final String ADMIN_USER = "admin_user";
	public static final String ADMIN_PASSWORD = "admin_password";
	
	public static final String PRIMARY_HOSTNAME = "primary_hostname";
	public static final String PRIMARY_VOLUME_ID  = "primary_volume_id";
	public static final String PRIMARY_IP = "primary_ip";
	public static final String PRIMARY_PORT = "primary_port";
	
	public static final String STANDBY_HOSTNAME = "standby_hostname";
	public static final String STANDBY_VOLUME_ID = "standby_volume_id";
	public static final String STANDBY_IP = "standby_ip";
	public static final String STANDBY_PORT = "standby_port";	
	
	public static final String SECONDARY_1_VOLUME_ID = "secondary1_volume_id";
	public static final String SECONDARY_2_VOLUME_ID = "secondary2_volume_id";
	public static final String SECONDARY_1_PORT = "secondary1_port";
	public static final String SECONDARY_2_PORT = "secondary2_port";
	public static final String SECONDARY_1_IP = "secondary1_ip";
	public static final String SECONDARY_2_IP = "secondary2_ip";

	public static final String SECONDARY_PORTS = "secondary_ports";
	public static final String SECONDARY_VOLUME_IDS = "secondary_volume_ids";
	public static final String SECONDARY_IPS = "secondary_ips";
	
	public static final String CLUSTER = "cluster";
	
	
	public static final String RABBITMQ_VHOST = "rabbitmq_vhost";
	public static final String ERLANG_KEY = "erlang_key";
	public static final String MASTER_VOLUME_ID = "master_volume_id";
	public static final String MASTER_PORT = "master_port";
	public static final String MASTER_IP = "master_ip";
	public static final String MIRROR1_VOLUME_ID = "mirror1_volume_id";
	public static final String MIRROR1_PORT = "mirror1_port";
	public static final String MIRROR1_IP = "mirror1_ip";
	public static final String MIRROR2_VOLUME_ID = "mirror2_volume_id";
	public static final String MIRROR2_PORT = "mirror2_port";
	public static final String MIRROR2_IP = "mirror2_ip";
	public static final String ETC_HOSTS = "etc_hosts";
	public static final String SECONDARY_NUMBER = "secondary_number";


	/** 
	 * 
	 */
	public ClusterParameterManager() {
	
	}

	public static String join(List<String> volumes) {
		StringBuilder b = new StringBuilder();
		for (String volume_id : volumes) {
			b.append(volume_id);
			if(!volumes.get(volumes.size()-1).equals(volume_id)) {
				b.append(",");
			}
		}
		return b.toString();
	}

	public static Map<String, String> copyProperties(Map<String, String> completeList, String... keys) {
		Map<String, String> copiedProps = new HashMap<>();

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			copiedProps.put(key, completeList.get(key));
		}
		return copiedProps;
	}


}