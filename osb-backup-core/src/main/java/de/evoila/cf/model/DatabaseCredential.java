package de.evoila.cf.model;

import de.evoila.cf.model.enums.DatabaseType;

public class DatabaseCredential {

    int port;
    String hostname;
    String username;
    String password;
    String context;
    DatabaseType type;

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContext() {
        return context;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setPort (int port) {
        this.port = port;
    }

    public void setHostname (String hostname) {
        this.hostname = hostname;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public void setContext (String context) {
        this.context = context;
    }

    public void setType (DatabaseType type) {
        this.type = type;
    }

}