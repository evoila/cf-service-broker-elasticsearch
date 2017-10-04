package de.evoila.cf.broker.persistence.mongodb.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;




/**
 * @author Christian Brinker, evoila.
 *
 */
@ConditionalOnProperty(prefix = "openstack", name = { "endpoint" }, havingValue = "")
public interface StackMappingRepository extends MongoRepository<ClusterStackMapping, String> {

}
