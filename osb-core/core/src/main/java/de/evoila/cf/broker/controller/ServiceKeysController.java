package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** @author Yannic Remmet. */
@RestController


@RequestMapping(value = "/v2/manage/servicekeys/{serviceInstanceId}")
class ServiceKeysController extends BaseController {

    BindingRepository bindingRepository;
    BindingService bindingService;
    ServiceInstanceRepository serviceInstanceRepository;

    ServiceKeysController (BindingRepository repository, BindingService service, ServiceInstanceRepository serviceInstanceRepository){
        Assert.notNull(repository, "BindingRepository should not be null");
        Assert.notNull(service, "Binding Service should not be null");
        this.bindingRepository = repository;
        this.bindingService = service;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @GetMapping(value = "")
    public ResponseEntity<Page<ServiceInstanceBinding>> getGeneralInformation (@PathVariable String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
        List<ServiceInstanceBinding> bindings = bindingRepository.getBindingsForServiceInstance(serviceInstanceId);
        return new ResponseEntity<>(new PageImpl<ServiceInstanceBinding>(bindings), HttpStatus.OK);
    }

    @GetMapping(value = "/{serviceBindingId}")
    public ResponseEntity<ServiceInstanceBinding> getServiceKey (@PathVariable String serviceInstanceId, @PathVariable String serviceBindingId) throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
        ServiceInstanceBinding binding = bindingRepository.findOne(serviceBindingId);
        return new ResponseEntity<>(binding, HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<ServiceInstanceBinding> createServiceKey (@PathVariable String serviceInstanceId) throws ServiceInstanceDoesNotExistException, ServiceBrokerException, ServiceInstanceBindingExistsException, ServiceDefinitionDoesNotExistException {
        ServiceInstance instance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        if(instance == null){
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }
        String bindingId = UUID.randomUUID().toString();
        ServiceInstanceBindingResponse bindingResponse = bindingService.createServiceInstanceBinding(bindingId, serviceInstanceId, null, instance.getPlanId(), true, null);
        ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
        return new ResponseEntity<>(binding, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{serviceBindingId}")
    public ResponseEntity delete (@PathVariable String serviceInstanceId, @PathVariable String serviceBindingId) throws ServerviceInstanceBindingDoesNotExistsException, ServiceBrokerException {
        bindingService.deleteServiceInstanceBinding(serviceBindingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
