/**
 * 
 */
package de.evoila.cf.broker.model.fixture;

import de.evoila.cf.broker.model.Catalog;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class CatalogFixture {

	public static Catalog getCatalog() {
		return new Catalog(ServiceFixture.getAllServices());
	}
	
}
