/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class RouteBinding implements BaseEntity<String> {

	private String id;

	private String serviceInstanceId;

	private String route;

	private String routeServiceUrl;

	public RouteBinding(String id, String serviceInstanceId, String route) {
		this.id = id;
		this.serviceInstanceId = serviceInstanceId;
		this.route = route;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.model.BaseEntity#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getRouteServiceUrl() {
		return routeServiceUrl;
	}

	public void setRouteServiceUrl(String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
	}

}
