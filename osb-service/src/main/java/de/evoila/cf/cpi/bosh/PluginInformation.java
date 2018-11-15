package de.evoila.cf.cpi.bosh;

/**
 * @author Michael Hahn
 */
public class PluginInformation {
    private String guid;
    private String name;
    private String source;

    public PluginInformation() {
    }

    public PluginInformation(String guid, String name, String source) {
        this.guid = guid;
        this.name = name;
        this.source = source;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "PluginInformation{" +
                "guid='" + guid + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
