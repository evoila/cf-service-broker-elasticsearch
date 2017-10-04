package de.evoila.cf.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.model.enums.RetentionStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yremmet on 18.07.17.
 */
public class BackupPlan {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private DatabaseCredential source;
    private String frequency;
    private String retentionStyle;
    private int retentionPeriod;
    private String destinationId;
    private List<String> jobIds;

    public String getId () {
        return id;
    }
    public void setId (String id) {
        this.id = id;
    }

    public String getFrequency () {
        return frequency;
    }

    public void setFrequency (String frequency) {
        this.frequency = frequency;
    }

    @JsonProperty
    public RetentionStyle getRetentionStyle () {
        return RetentionStyle.valueOf(retentionStyle);
    }

    @JsonIgnore
    public void setRetentionStyle (RetentionStyle retentionStyle) {
        this.retentionStyle = retentionStyle.toString();
    }
    @JsonProperty
    public void setRetentionStyle (String retentionStyle) {
        this.retentionStyle = retentionStyle;
    }

    public int getRetentionPeriod () {
        return retentionPeriod;
    }
    public void setRetentionPeriod (int retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public DatabaseCredential getSource () {
        return source;
    }

    public void setSource (DatabaseCredential source) {
        this.source = source;
    }

    public String getDestinationId () {
        return destinationId;
    }

    public void setDestinationId (String destinationId) {
        this.destinationId = destinationId;
    }

    @JsonIgnore
    public List<String> getJobIds () {
        if (jobIds == null)
            jobIds = new ArrayList();
        return jobIds;
    }

    public void setJobIds (List<String> jobIds) {
        this.jobIds = jobIds;
    }

    public void update (BackupPlan plan) {
        if (plan.frequency != null) {
            this.frequency = plan.frequency;
        }
        if (plan.retentionStyle != null) {
            this.retentionStyle = plan.retentionStyle;
        }
        if (plan.destinationId != null) {
            this.destinationId = plan.destinationId;
        }
        if (plan.getRetentionPeriod() > 0) {
            this.retentionPeriod = plan.retentionPeriod;
        }
    }
}
