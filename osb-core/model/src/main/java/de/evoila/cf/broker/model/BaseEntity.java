/**
 * 
 */
package de.evoila.cf.broker.model;

import java.io.Serializable;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface BaseEntity<ID extends Serializable> {
	
	public ID getId();
}
