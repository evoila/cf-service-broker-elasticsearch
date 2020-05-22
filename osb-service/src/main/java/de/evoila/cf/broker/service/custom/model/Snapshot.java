package de.evoila.cf.broker.service.custom.model;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {

    private String name;

    private String startDate;

    private String status;

    private List<String> indices;

    public Snapshot(String name, String startDate, String status, List<String> indices) {
        this.name = name;
        this.startDate = startDate;
        this.status = status;
        this.indices = new ArrayList<>(indices);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getIndices() {
        return indices;
    }

    public void setIndices(List<String> indices) {
        this.indices = indices;
    }
}
