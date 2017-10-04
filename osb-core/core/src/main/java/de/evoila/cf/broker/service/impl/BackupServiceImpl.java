package de.evoila.cf.broker.service.impl;


import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.bean.conditional.BackupServiceCondition;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.service.BackupService;
import de.evoila.cf.broker.service.InstanceCredentialService;
import de.evoila.cf.model.BackupRequest;
import de.evoila.cf.model.DatabaseCredential;
import de.evoila.cf.model.RestoreRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@ConditionalOnBean(BackupServiceCondition.class)
public class BackupServiceImpl implements BackupService {
    private static final Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);
    private final RestTemplate rest;
    private final HttpHeaders headers;
    InstanceCredentialService credentialService;
    BackupConfiguration config;
    RabbitTemplate template;

    public BackupServiceImpl (BackupConfiguration config, InstanceCredentialService credentialService, RabbitTemplate rabbitTemplate) {
        Assert.notNull(config, "BackupConfiguration can not be null");
        Assert.notNull(credentialService, "InstanceCredentialService can not be null");
        Assert.notNull(rabbitTemplate, "RabbitTemplate can not be null");

        this.config = config;
        this.credentialService = credentialService;
        this.rest = new RestTemplate();
        this.template = rabbitTemplate;

        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("Authorization", encodeCredentials());

        template.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    private String encodeCredentials () {
        String str = config.getUser()+":"+config.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(str.getBytes());
    }

    @Override
    public ResponseEntity<Object> backupNow (String serviceInstanceId, BackupRequest body) throws ServiceInstanceDoesNotExistException {
        DatabaseCredential credential = credentialService.getCredentialsForInstanceId(serviceInstanceId);
        body.setSource(credential);

        BackupConfiguration.Queue queue = this.config.getQueue();
        if (queue != null){
            template.convertAndSend(queue.getExchange(), queue.getRoutingKey(),body);
        } else {
            String msg = "Backup RabbitMQ config is null. Please check configuration";
            logger.error(msg);
            return new ResponseEntity<>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new HashMap(),HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HashMap> restoreNow (String serviceInstanceId, RestoreRequest body) throws ServiceInstanceDoesNotExistException {
        DatabaseCredential credentials = credentialService.getCredentialsForInstanceId(serviceInstanceId);
        body.setDestination(credentials);

        template.convertAndSend(this.config.getQueue().getExchange(),
                                this.config.getQueue().getRoutingKey(),body);

        return new ResponseEntity<HashMap>(new HashMap(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HashMap> getJobs (String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<String, String>();
        uriParams.put("serviceInstanceId", serviceInstanceId);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<HashMap> response = rest
                .exchange(buildUri("/jobs/byInstance/{serviceInstanceId}", pageable).buildAndExpand(uriParams).toUri(),
                    HttpMethod.GET, entity, new ParameterizedTypeReference<HashMap>() {});

        return response;
    }

    private UriComponentsBuilder buildUri(String path, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(config.getUri() + path);

        builder.queryParam("page", pageable.getPageNumber());
        builder.queryParam("page_size", pageable.getPageSize());
        if (pageable.getSort() != null) {
            Iterator<Sort.Order> sortIterator = pageable.getSort().iterator();
            while (sortIterator.hasNext()) {
                Sort.Order order = sortIterator.next();
                builder.queryParam("sort", order.getProperty() + "," + order.getDirection().toString());
            }
        }

        return builder;
    }

    @Override
    public ResponseEntity<HashMap> getPlans (String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<String, String>();
        uriParams.put("serviceInstanceId", serviceInstanceId);


        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<HashMap> response = rest
                .exchange(buildUri("/plans/byInstance/{serviceInstanceId}", pageable).buildAndExpand(uriParams).toUri(),
                        HttpMethod.GET, entity, new ParameterizedTypeReference<HashMap>() {});

        return response;
    }

    @Override
    public ResponseEntity<HashMap> deleteJob (String serviceInstanceId, String jobid) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = rest.exchange(config.getUri() + "/jobs/" + jobid,
                                                HttpMethod.DELETE, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> postPlan (String serviceInstanceId, HashMap plan) throws ServiceInstanceDoesNotExistException {
        DatabaseCredential credentials = credentialService.getCredentialsForInstanceId(serviceInstanceId);
        plan.put("source", credentials);
        HttpEntity entity = new HttpEntity(plan, headers);
        ResponseEntity response = rest.exchange(config.getUri() + "/plans",
                                                HttpMethod.POST, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> deletePlan (String serviceInstanceId, String planid) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = rest.exchange(config.getUri() + "/plans/" + planid,
                                                HttpMethod.DELETE, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> updatePlan (String serviceInstanceId, String planId, HashMap plan) throws ServiceInstanceDoesNotExistException {
        DatabaseCredential credentials = credentialService.getCredentialsForInstanceId(serviceInstanceId);
        plan.put("source", credentials);
        HttpEntity entity = new HttpEntity(plan, headers);
        ResponseEntity response = rest.exchange(config.getUri() + "/plans/" + planId,
                                                HttpMethod.PUT, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> getJob (String serviceInstanceId, String jobid) {
        HttpEntity entity = new HttpEntity(headers);
        return rest.exchange(config.getUri()+"/jobs/"+jobid,HttpMethod.GET,entity,HashMap.class);
    }

    @Override
    public ResponseEntity<HashMap> getPlan (String serviceInstanceId, String planId) {
        HttpEntity entity = new HttpEntity(headers);
        return rest.exchange(config.getUri()+"/plans/"+planId,HttpMethod.GET,entity,HashMap.class);
    }

    @Override
    public ResponseEntity<HashMap> getDestinations (String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<String, String>();
        uriParams.put("serviceInstanceId", serviceInstanceId);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<HashMap> response = rest
                                 .exchange(buildUri("/destinations/byInstance/{serviceInstanceId}", pageable)
                                 .buildAndExpand(uriParams).toUri(),
                                       HttpMethod.GET, entity, new ParameterizedTypeReference<HashMap>() {});
        return response;
    }

    @Override
    public ResponseEntity<HashMap> getDestination (String serviceInstanceId, String destinationId) {
        HttpEntity entity = new HttpEntity(headers);
        return rest.exchange(config.getUri()+"/destinations/"+destinationId,HttpMethod.GET,entity,HashMap.class);
    }

    @Override
    public ResponseEntity<HashMap> postDestination (String serviceInstanceId, HashMap dest) {
        HttpEntity entity = new HttpEntity(dest, headers);
        dest.put("instanceId", serviceInstanceId);
        ResponseEntity response = rest.exchange(config.getUri() + "/destinations",
                                                HttpMethod.POST, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> updateDestination (String serviceInstanceId, String destinationId, HashMap dest)  {
        HttpEntity entity = new HttpEntity(dest, headers);
        dest.put("instanceId", serviceInstanceId);
        ResponseEntity response = rest.exchange(config.getUri() + "/destinations/" + destinationId,
                                                HttpMethod.PUT, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> deleteDestination (String serviceInstanceId, String destinationId) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = rest.exchange(config.getUri() + "/destinations/" + destinationId,
                                                HttpMethod.DELETE, entity, HashMap.class
        );
        return response;
    }

    @Override
    public ResponseEntity<HashMap> validateDestination (String serviceInstanceId, HashMap dest) {
        HttpEntity entity = new HttpEntity(dest, headers);
        dest.put("instanceId", serviceInstanceId);
        ResponseEntity response = rest.exchange(config.getUri() + "/destinations/validate",
                                                HttpMethod.POST, entity, HashMap.class
        );
        return response;
    }


}
