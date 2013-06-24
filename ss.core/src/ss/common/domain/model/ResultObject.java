/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.collections.DeliveryResponseCollection;
import ss.common.domain.model.enums.DeliveryType;

/**
 * @author roman
 *
 */
public class ResultObject extends DomainObject {

	private final DeliveryResponseCollection responses = new DeliveryResponseCollection();
	
	private DeliveryType parentWorkflowType;
	
	private double requiredPercentage;

	/**
	 * @return the parentWorkflowType
	 */
	public DeliveryType getParentWorkflowType() {
		return this.parentWorkflowType;
	}

	/**
	 * @param parentWorkflowType the parentWorkflowType to set
	 */
	public void setParentWorkflowType(DeliveryType parentWorkflowType) {
		this.parentWorkflowType = parentWorkflowType;
	}

	/**
	 * @return the responses
	 */
	public DeliveryResponseCollection getResponses() {
		return this.responses;
	}
	
	public boolean isVotingOn() {
		//TODO
		return true;
	}

	/**
	 * @return the requiredPercentage
	 */
	public double getRequiredPercentage() {
		return this.requiredPercentage;
	}

	/**
	 * @param requiredPercentage the requiredPercentage to set
	 */
	public void setRequiredPercentage(double requiredPercentage) {
		this.requiredPercentage = requiredPercentage;
	}

	/**
	 * @return
	 */
	public String getGiverUsername() {
		return null;
	}
}
