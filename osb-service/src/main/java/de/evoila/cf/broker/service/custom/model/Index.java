package de.evoila.cf.broker.service.custom.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michael Hahn
 */
public class Index {
    @JsonProperty("index")
    private String name;

    private String uuid;

    private String health;

    private String status;

    @JsonProperty(value = "docs.count")
    private long docsCount;

    @JsonProperty(value = "docs.deleted")
    private long docsDeleted;

    @JsonProperty(value = "pri.store.size")
    private String priStoreSize;

    @JsonProperty(value = "store.size")
    private String storeSize;

    private long pri;

    private long rep;

    public Index(String name, String health, String uuid, String status, long docsCount, long docsDeleted, String priStoreSize, String storeSize, long pri, long rep) {
        this.name = name;
        this.health = health;
        this.status = status;
        this.docsCount = docsCount;
        this.docsDeleted = docsDeleted;
        this.priStoreSize = priStoreSize;
        this.storeSize = storeSize;
        this.pri = pri;
        this.rep = rep;
    }

    public Index() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDocsCount() {
        return docsCount;
    }

    public void setDocsCount(long docsCount) {
        this.docsCount = docsCount;
    }

    public long getDocsDeleted() {
        return docsDeleted;
    }

    public void setDocsDeleted(long docsDeleted) {
        this.docsDeleted = docsDeleted;
    }

    public String getPriStoreSize() {
        return priStoreSize;
    }

    public void setPriStoreSize(String priStoreSize) {
        this.priStoreSize = priStoreSize;
    }

    public String getStoreSize() {
        return storeSize;
    }

    public void setStoreSize(String storeSize) {
        this.storeSize = storeSize;
    }

    public long getPri() {
        return pri;
    }

    public void setPri(long pri) {
        this.pri = pri;
    }

    public long getRep() {
        return rep;
    }

    public void setRep(long rep) {
        this.rep = rep;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Index{" +
                "name='" + name + '\'' +
                ", health='" + health + '\'' +
                ", status='" + status + '\'' +
                ", docsCount=" + docsCount +
                '}';
    }
}