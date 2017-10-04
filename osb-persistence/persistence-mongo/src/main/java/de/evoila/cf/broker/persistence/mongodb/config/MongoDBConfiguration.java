/**
 * 
 */
package de.evoila.cf.broker.persistence.mongodb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Configuration
@EnableMongoRepositories(basePackages="de.evoila.cf.broker.persistence.mongodb.repository")
public class MongoDBConfiguration {

}
