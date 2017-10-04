package de.evoila.cf.broker.cpi.endpoint.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Controller
@RequestMapping(value = "/v2/endpoint")
public class EndpointController extends BaseController {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(EndpointController.class);
	
	@Autowired 
	private EndpointAvailabilityService endpointAvailabilityService;
	
	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public @ResponseBody Map<String, EndpointServiceState> getCatalog() {
		return endpointAvailabilityService.getServices();
	}
	
}
