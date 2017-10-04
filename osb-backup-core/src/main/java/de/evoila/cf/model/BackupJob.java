package de.evoila.cf.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.evoila.cf.model.enums.DestinationType;
import de.evoila.cf.model.enums.JobStatus;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yremmet on 06.07.17.
 */
public class BackupJob {


  public static final String BACKUP_JOB = "Backup Job";
  public static final String RESTORE_JOB = "Restore Job";
  private String id;
  private Date startDate;
  private String instanceId;
  private JobStatus status;
  private String jobType;
  private BackupDestination destination;
  private List<String> logs;

  public String getId () {
    return id;
  }

  public void setId (String id) {
    this.id = id;
  }

  public Date getStartDate () {
    return startDate;
  }

  public String getInstanceId () {
    return instanceId;
  }

  public void setInstanceId (String instance) {
    this.instanceId = instance;
  }

  public JobStatus getStatus () {
    return status;
  }

  public void setStatus (JobStatus status) {
    this.status = status;
  }

  public String getJobType () {
    return jobType;
  }

  public void setJobType (String jobType) {
    this.jobType = jobType;
  }

  public void setStartDate (Date startDate) {
    this.startDate = startDate;
  }

  public List<String> getLogs () {
    if(logs == null){
      logs = new LinkedList();
    }
    return logs;
  }

  public void setLogs (List<String> logs) {
    this.logs = logs;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public BackupDestination getDestination () {
    return destination;
  }

  public void setBackupFile (FileDestination destination) {
    this.destination = new BackupDestination(destination);
  }
  public void setDestination (BackupDestination destination) {
    this.destination = destination;
  }

    public synchronized void appendLog (String msg) {
      this.getLogs().add(msg);
    }


    public static class BackupDestination {
    private String type;
    private String project;
    private String container;
    private String filename;
      private String authUrl;

      public BackupDestination(){
      super();
    }

    public void setType (String type) {
      this.type = type;
    }

    public void setType (DestinationType type) {
      this.type = type.toString();
    }


    public void setProject (String project) {
      this.project = project;
    }

    public void setContainer (String container) {
      this.container = container;
    }

    public void setFilename (String filename) {
      this.filename = filename;
    }

    private BackupDestination (FileDestination destination) {
      setType(DestinationType.Swift);
      setProject(destination.getProjectName());
      setContainer(destination.getContainerName());
      setFilename(destination.getFilename());
      setAuthUrl(destination.getAuthUrl());
    }

    public String getType () {
      return type;
    }

    public String getProject () {
      return project;
    }

    public String getContainer () {
      return container;
    }

    public String getFilename () {
      return filename;
    }

      public void setAuthUrl (String authUrl) {
        this.authUrl = authUrl;
      }

      public String getAuthUrl () {
        return authUrl;
      }
    }
}
