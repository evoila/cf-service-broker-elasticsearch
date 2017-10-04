/**
 * 
 */
package de.evoila.cf.broker.persistence.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface ServiceInstanceRepository extends MongoRepository<ServiceInstance, String> {

}
