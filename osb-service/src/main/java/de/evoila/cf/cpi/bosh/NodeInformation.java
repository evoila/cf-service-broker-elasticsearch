package de.evoila.cf.cpi.bosh;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

/**
 * @author Michael Hahn
 */
public class NodeInformation {
    private String value;

    @JsonProperty(value = "selected_option")
    private LinkedHashMap<String, Object> selectedOption;

    public NodeInformation() {
    }

    public NodeInformation(String value, LinkedHashMap<String, Object> selectedOption) {
        this.value = value;
        this.selectedOption = selectedOption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LinkedHashMap<String, Object> getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(LinkedHashMap<String, Object> selectedOption) {
        this.selectedOption = selectedOption;
    }

    @Override
    public String toString() {
        return "NodeInformation{" +
                "value='" + value + '\'' +
                ", selectedOption=" + selectedOption +
                '}';
    }
}
