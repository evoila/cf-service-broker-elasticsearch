package de.evoila.cf.broker.exception;
/**
 * 
 * @author Dennis Mueller, evoila GmbH, Sep 7, 2015
 *
 */
public class AsyncRequiredException extends Exception {

	private static final long serialVersionUID = 545744662141910897L;

	@Override
	public String getMessage() {
		return "This service plan requires client support for asynchronous service operations.";
	}
}
