package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix="messaging.rabbitmq")
public class RabbitMQCredentials {
    private String host;
    private String vhost;
    private String username;
    private String password;
    private int port;

    public String getHost () {
        return host;
    }

    public void setHost (String host) {
        this.host = host;
    }

    public String getVhost () {
        return vhost;
    }

    public void setVhost (String vhost) {
        this.vhost = vhost;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public int getPort () {
        return port;
    }

    public void setPort (int port) {
        this.port = port;
    }
}
