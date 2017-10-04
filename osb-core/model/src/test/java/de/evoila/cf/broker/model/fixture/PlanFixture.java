/**
 * 
 */
package de.evoila.cf.broker.model.fixture;

import java.util.ArrayList;
import java.util.List;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.VolumeUnit;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class PlanFixture {

	public static List<Plan> getAllPlans() {
		List<Plan> plans = new ArrayList<Plan>();
		plans.add(getPlanOne());
		plans.add(getPlanTwo());
		return plans;
	}

	public static Plan getPlanOne() {
		return new Plan("plan-one-id", "Plan One", "Description for Plan One", Platform.DOCKER, 0, VolumeUnit.M, "1", true, 0);
	}

	public static Plan getPlanTwo() {
		return new Plan("plan-two-id", "Plan Two", "Description for Plan Two", Platform.DOCKER, 0, VolumeUnit.M, "1", true, 0);
	}

}
