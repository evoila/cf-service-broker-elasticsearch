package de.evoila.cf.broker.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import org.springframework.util.Assert;

import java.util.Map;

public class ServiceInstanceView {
    private ServiceInstance serviceInstance;
    private Plan plan;

    public ServiceInstanceView(ServiceInstance si, Plan p){
        Assert.notNull(si, "Service instance can not be null");
        Assert.notNull(p, "Plan can not be null");
        this.plan = p;
        this.serviceInstance = si;
    }

    @JsonProperty("service_instance_id")
    public String getServiceInstanceId(){
        return serviceInstance.getId();
    }

    public String getFlavour(){
        return plan.getFlavorId();
    }

    public String getDashboarUrl(){
        return serviceInstance.getDashboardUrl();
    }

    public String getService(){
        return plan.getDescription();
    }

    public Map<String, String> getParameters() {
        return serviceInstance.getParameters();
    }

    public int getVolumeSize() {
        return plan.getVolumeSize();
    }
    public String getVolumeUnit() {
        return plan.getVolumeUnit().toString();
    }
    public String getPlatform() {
        return plan.getPlatform().toString();
    }
    @JsonProperty(value = "free")
    public boolean isFree() {
        return plan.isFree();
    }
}
