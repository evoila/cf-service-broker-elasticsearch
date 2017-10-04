/**
 * 
 */
package de.evoila.cf.broker.persistence.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.evoila.cf.broker.model.RouteBinding;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface RouteBindingRepository extends MongoRepository<RouteBinding, String> {

}
