/**
 * 
 */
package de.evoila.cf.broker.persistence.mongodb.repository;

import de.evoila.cf.broker.model.ServiceInstanceBinding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface MongoDBBindingRepository extends MongoRepository<ServiceInstanceBinding, String> {

    List<ServiceInstanceBinding> findByServiceInstanceId (String serviceInstanceId);
}
