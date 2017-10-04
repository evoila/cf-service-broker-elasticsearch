package de.evoila.cf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.model.enums.DestinationType;
import de.evoila.cf.model.interfaces.SwiftConfig;


public class FileDestination implements SwiftConfig {

    private String authUrl;
    private String username;
    @JsonProperty()
    private String password;
    private String domain;
    private String containerName;
    private String projectName;
    private DestinationType type;
    private String name;
    private String instanceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filename;
    private String id;

    public String getInstanceId () {
        return instanceId;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public void setInstanceId (String instanceId) {
        this.instanceId = instanceId;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getFilename () {
        return filename;
    }

    public void setFilename (String filename) {
        this.filename = filename;
    }

    public String getAuthUrl () {
        return authUrl;
    }

    public void setAuthUrl (String authUrl) {
        this.authUrl = authUrl;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }
    @JsonIgnore
    public String getPassword () {
        return password;
    }
    @JsonProperty("password")
    public void setPassword (String password) {
        this.password = password;
    }

    public String getDomain () {
        return domain;
    }

    public void setDomain (String domain) {
        this.domain = domain;
    }

    public String getContainerName () {
        return containerName;
    }

    public void setContainerName (String containerName) {
        this.containerName = containerName;
    }

    public String getProjectName () {
        return projectName;
    }

    public void setProjectName (String projectName) {
        this.projectName = projectName;
    }

    public DestinationType getType () {
        return type;
    }

    public void setType (DestinationType type) {
        this.type = type;
    }
}