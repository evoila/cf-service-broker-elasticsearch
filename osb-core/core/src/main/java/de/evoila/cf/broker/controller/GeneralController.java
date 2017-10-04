package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.view.ServiceInstanceView;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** @author Yannic Remmet. */
@RestController

class GeneralController extends BaseController {

    ServiceInstanceRepository repository;
    ServiceDefinitionRepository serviceDefinitionRepository;

    GeneralController(ServiceInstanceRepository repository, ServiceDefinitionRepository sdRepository){
        Assert.notNull(repository, "ServiceInstanceRepository is null");
        Assert.notNull(sdRepository, "ServiceDefinitionRepository is null");
        this.repository = repository;
        this.serviceDefinitionRepository = sdRepository;
    }

    @GetMapping(value = "/v2/manage/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceView> getGeneralInformation (@PathVariable String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
        return new ResponseEntity<ServiceInstanceView>(getServiceInstanceView(serviceInstanceId), HttpStatus.OK);
    }

    private ServiceInstanceView getServiceInstanceView (String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
        ServiceInstance serviceInstance = repository.getServiceInstance(serviceInstanceId);

        if(serviceInstance == null){
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

        return new ServiceInstanceView(serviceInstance, plan);
    }
}
