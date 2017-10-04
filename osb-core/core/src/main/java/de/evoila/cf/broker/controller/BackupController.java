package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.service.BackupService;
import de.evoila.cf.model.BackupRequest;
import de.evoila.cf.model.RestoreRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/** @author Yannic Remmet. */
@RestController
@RequestMapping(value = "/v2/manage/backup")
@ConditionalOnBean(name="backupService") // Needs to be the Bean Name in this case otherwise Spring tries to create the controller
class BackupController extends BaseController {
    private BackupService backupService;

    public BackupController(BackupService service){
        Assert.notNull(service, "The backup service can not be null");
        this.backupService = service;
    }

    @PostMapping(value = "/{serviceInstanceId}/backup")
    public ResponseEntity<Object> backupNow(@PathVariable String serviceInstanceId, @RequestBody BackupRequest fileDestination) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<Object> response = backupService.backupNow(serviceInstanceId, fileDestination);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/restore")
    public ResponseEntity<HashMap> restoreNow(@PathVariable String serviceInstanceId, @RequestBody RestoreRequest fileDestination) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.restoreNow(serviceInstanceId, fileDestination);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{serviceInstanceId}/jobs")
    public ResponseEntity<HashMap> getJobs(@PathVariable String serviceInstanceId,
                                            @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<HashMap> response = backupService.getJobs(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{serviceInstanceId}/jobs/{jobid}")
    public ResponseEntity<HashMap> getJobs(@PathVariable String serviceInstanceId, @PathVariable String jobid) {
        ResponseEntity<HashMap> response = backupService.getJob(serviceInstanceId, jobid);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/jobs/{jobid}")
    public ResponseEntity<HashMap> deleteJobs(@PathVariable String serviceInstanceId, @PathVariable String jobid) {
        ResponseEntity<HashMap> response = backupService.deleteJob(serviceInstanceId, jobid);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    // PLANS
    @GetMapping(value = "/{serviceInstanceId}/plans")
    public ResponseEntity<HashMap> getPlans(@PathVariable String serviceInstanceId,
                                            @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<HashMap> response = backupService.getPlans(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/plans")
    public ResponseEntity<HashMap> postPlan(@PathVariable String serviceInstanceId, @RequestBody HashMap plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.postPlan(serviceInstanceId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }


    @GetMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<HashMap> getPlan(@PathVariable String serviceInstanceId,
                                             @PathVariable String planId) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.getPlan(serviceInstanceId, planId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<HashMap> patchPlan(@PathVariable String serviceInstanceId,
                                             @PathVariable String planId,
                                             @RequestBody HashMap plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.updatePlan(serviceInstanceId, planId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<HashMap> deletePlan(@PathVariable String serviceInstanceId,
                                              @PathVariable String planId) {
        ResponseEntity<HashMap> response = backupService.deletePlan(serviceInstanceId, planId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    // DESTINATIONS
    @GetMapping(value = "/{serviceInstanceId}/destinations")
    public ResponseEntity<HashMap> getDestinations(@PathVariable String serviceInstanceId,
                                           @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<HashMap> response = backupService.getDestinations(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/destinations")
    public ResponseEntity<HashMap> postDestination(@PathVariable String serviceInstanceId, @RequestBody HashMap plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.postDestination(serviceInstanceId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }


    @GetMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<HashMap> getDestination(@PathVariable String serviceInstanceId,
                                           @PathVariable String destinationId) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.getDestination(serviceInstanceId, destinationId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<HashMap> putDestinaton(@PathVariable String serviceInstanceId,
                                             @PathVariable String destinationId,
                                             @RequestBody HashMap plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.updateDestination(serviceInstanceId, destinationId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<HashMap> deleteDestination(@PathVariable String serviceInstanceId,
                                              @PathVariable String destinationId) {
        ResponseEntity<HashMap> response = backupService.deleteDestination(serviceInstanceId, destinationId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/destinations/validate")
    public ResponseEntity<HashMap> validateDestination(@PathVariable String serviceInstanceId, @RequestBody HashMap plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.validateDestination(serviceInstanceId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

}