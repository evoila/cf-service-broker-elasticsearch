/**
 * 
 */
package de.evoila.cf.broker.persistence.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.evoila.cf.broker.model.JobProgress;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface JobProgressRepository extends MongoRepository<JobProgress, String> {

}
