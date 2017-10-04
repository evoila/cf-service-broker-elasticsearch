package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.model.BackupRequest;
import de.evoila.cf.model.RestoreRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public interface BackupService {

    ResponseEntity<Object> backupNow(String serviceInstanceId, BackupRequest fileDestination) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> restoreNow (String serviceInstanceId, RestoreRequest fileDestination) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> getJobs (String serviceInstanceId, Pageable pageable);

    ResponseEntity<HashMap> getPlans (String serviceInstanceId, Pageable pageable);

    ResponseEntity<HashMap> deleteJob (String serviceInstanceId, String jobid);

    ResponseEntity<HashMap> postPlan (String serviceInstanceId, HashMap plan) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> deletePlan (String serviceInstanceId, String planid);

    ResponseEntity<HashMap> updatePlan (String serviceInstanceId, String planId, HashMap plan) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> getJob (String serviceInstanceId, String jobid);

    ResponseEntity<HashMap> getPlan (String serviceInstanceId, String planId);

    ResponseEntity<HashMap> getDestinations (String serviceInstanceId, Pageable pageable);

    ResponseEntity<HashMap> getDestination (String serviceInstanceId, String destinationId);

    ResponseEntity<HashMap> postDestination (String serviceInstanceId, HashMap plan);

    ResponseEntity<HashMap> updateDestination (String serviceInstanceId, String destinationId, HashMap plan) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> deleteDestination (String serviceInstanceId, String destinationId);

    ResponseEntity<HashMap> validateDestination (String serviceInstanceId, HashMap plan);
}
