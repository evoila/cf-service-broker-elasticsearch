package de.evoila.cf.model;

/**
 * Created by yremmet on 27.06.17.
 */
public class BackupRequest {

    private DatabaseCredential source;
    private String destinationId;

    public DatabaseCredential getSource() {
        return source;
    }

    public void setSource (DatabaseCredential source) {
        this.source = source;
    }

    public void setDestinationId (String destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationId () {
        return destinationId;
    }
}
