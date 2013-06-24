/**
 * 
 */
package ss.common.domain.model.suprasphere;

import ss.common.domain.model.enums.DeliveryType;

/**
 * @author roman
 *
 */
public class SphereReferenceObject extends SimpleSphereReferenceObject {

	protected DeliveryType delivery;
	
	protected boolean enabled;

	/**
	 * @return the delivery
	 */
	public DeliveryType getDelivery() {
		return this.delivery;
	}

	/**
	 * @param delivery the delivery to set
	 */
	public void setDelivery(DeliveryType delivery) {
		this.delivery = delivery;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
