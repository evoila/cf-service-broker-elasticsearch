package de.evoila.cf.broker.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Plan {

	@JsonSerialize
	@JsonProperty("id")
	private String id;

	@JsonSerialize
	@JsonProperty("name")
	private String name;

	@JsonSerialize
	@JsonProperty("description")
	private String description;

	@JsonSerialize
	@JsonProperty("metadata")
	private Map<String, Object> metadata = new HashMap<String, Object>();
	
	@JsonSerialize
	@JsonProperty(value="free", required=false)
	private boolean free = true;

	private int volumeSize;

	private VolumeUnit volumeUnit;

	private Platform platform;

	private String flavorId;

	private int connections;

	public Plan() {
		super();
	}

	public int getVolumeSize() {
		return volumeSize;
	}

	public void setVolumeSize(int volumeSize) {
		this.volumeSize = volumeSize;
	}

	public VolumeUnit getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(VolumeUnit volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public Platform getPlatform() {
		return platform;
	}

	@JsonIgnore
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public void setPlatform(String platform) {
		this.platform = Platform.valueOf(platform);
	}

	public String getFlavorId() {
		return flavorId;
	}

	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public Plan(String id, String name, String description, Platform platform, int volumeSize, VolumeUnit volumeUnit,
			String flavorId, boolean free, int connections) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.platform = platform;
		this.volumeSize = volumeSize;
		this.flavorId = flavorId;
		this.connections = connections;
		this.volumeUnit = volumeUnit;
	}

	public Plan(String id, String name, String description, Map<String, Object> metadata, Platform platform,
			int volumeSize, VolumeUnit volumeUnit, String flavor, boolean free, int connections) {
		this(id, name, description, platform, volumeSize, volumeUnit, flavor, free, connections);
		setMetadata(metadata);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		if (metadata == null) {
			this.metadata = new HashMap<String, Object>();
		} else {
			this.metadata = metadata;
		}
	}

	public boolean isFree() {
		return free;
	}
	
	public boolean getFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

}
