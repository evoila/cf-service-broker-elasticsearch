package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/** @author Yannic Remmet */
@Service
@ConfigurationProperties(prefix="backup")
//@ConditionalOnProperty(prefix = "backup", name = {"uri", "user", "password"}, havingValue = "")
public class BackupConfiguration {

    private String authToken;
    private String uri;
    private String password;
    private String user;
    private Queue queue;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Queue getQueue () {
        return queue;
    }

    public void setQueue (Queue queue) {
        this.queue = queue;
    }

    public static class Queue{
        private String exchange;
        private String routingKey;
        private String queue;

        public String getExchange () {
            return exchange;
        }

        public void setExchange (String exchange) {
            this.exchange = exchange;
        }

        public String getRoutingKey () {
            return routingKey;
        }

        public void setRoutingKey (String routingKey) {
            this.routingKey = routingKey;
        }

        public String getQueue () {
            return queue;
        }

        public void setQueue (String queue) {
            this.queue = queue;
        }
    }
}
