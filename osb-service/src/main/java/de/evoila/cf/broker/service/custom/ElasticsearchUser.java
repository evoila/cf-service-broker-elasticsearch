package de.evoila.cf.broker.service.custom;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;

public class ElasticsearchUser implements Serializable {
    private String password;
    private List<String> roles;

    public ElasticsearchUser() {
    }

    public ElasticsearchUser(String password, String... roles) {
        this.password = password;
        this.roles = asList(roles);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
