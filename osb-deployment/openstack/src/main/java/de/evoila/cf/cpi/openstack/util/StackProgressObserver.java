/**
 * 
 */
package de.evoila.cf.cpi.openstack.util;

import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;

/**
 * @author Christian Mueller, evoila
 *
 */
@Service
@ConditionalOnBean(OpenstackBean.class)
public class StackProgressObserver {
	public static final String CREATE_IN_PROGRESS = "CREATE_IN_PROGRESS";

	public static final String CREATE_FAILED = "CREATE_FAILED";
	public static final String DELETE_FAILED = "DELETE_FAILED";
	public static final String DELETE_IN_PROGRESS = "DELETE_IN_PROGRESS";
	

	@Autowired
	private HeatFluent heatFluent;
	
	/**
	 * @param name
	 * @return
	 * @throws PlatformException
	 */
	public Stack waitForStackCompletion(String name) throws PlatformException {
		Stack stack;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new PlatformException(e);
		}

		stack = heatFluent.get(name);

		if (stack != null && stack.getStatus().equals(CREATE_FAILED))
			throw new PlatformException(stack.getStackStatusReason());
		
		while (stack.getStatus().equals(CREATE_IN_PROGRESS)) {
			stack = heatFluent.get(name);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new PlatformException(e);
			}
		}
		return stack;
	}
	
	/**
	 * @param name
	 * @return
	 * @throws PlatformException
	 */
	public Stack waitForStackDeletion(String name) throws PlatformException {
		Stack stack;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new PlatformException(e);
		}

		stack = heatFluent.get(name);

		if (stack != null && stack.getStatus().equals(DELETE_FAILED))
			throw new PlatformException(stack.getStackStatusReason());
		
		while (stack != null &&
				stack.getStatus() != null &&
				stack.getStatus().equals(DELETE_IN_PROGRESS)) {
			stack = heatFluent.get(name);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new PlatformException(e);
			}
		}
		return stack;
	}

}
