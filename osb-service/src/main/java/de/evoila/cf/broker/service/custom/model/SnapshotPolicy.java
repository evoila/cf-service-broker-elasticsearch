package de.evoila.cf.broker.service.custom.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public class SnapshotPolicy {

    private String schedule;

    private String name;

    private String repository;

    private Config config;

    private Retention retention;

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static class Config {
        private List<String> indices;

        public List<String> getIndices() {
            return indices;
        }

        public void setIndices(List<String> indices) {
            this.indices = indices;
        }
    }

    public Retention getRetention() {
        return retention;
    }

    public void setRetention(Retention retention) {
        this.retention = retention;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Retention {

        private String expireAfter;

        private int minCount;

        private int maxCount;

        public String getExpireAfter() {
            return expireAfter;
        }

        public void setExpireAfter(String expireAfter) {
            this.expireAfter = expireAfter;
        }

        public int getMinCount() {
            return minCount;
        }

        public void setMinCount(int minCount) {
            this.minCount = minCount;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }
    }
}
