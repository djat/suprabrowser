/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.common.ReflectionUtils;
import ss.common.domain.model.enums.DeliveryType;

/**
 * @author roman
 *
 */
public class DeliveryDescriptor {

	private final DeliveryType type;
	
	private final Class<AbstractDeliveryObject> deliveryClass;

	@SuppressWarnings("unchecked")
	public DeliveryDescriptor(final DeliveryType type, final Class deliveryClass) {
		super();
		this.type = type;
		this.deliveryClass = deliveryClass;
	}

	/**
	 * @return the type
	 */
	public DeliveryType getType() {
		return this.type;
	}
	
	
	/**
	 * @return the deliveryClass
	 */
	@SuppressWarnings("unchecked")
	Class getDeliveryClass() {
		return this.deliveryClass;
	}

	@SuppressWarnings("unchecked")
	public AbstractDeliveryObject createDelivery() {
		return (AbstractDeliveryObject) ReflectionUtils.create( this.deliveryClass );
	}

	/**
	 * @return
	 */
	public String getDefaultDisplayName() {
		return createDelivery().getDefaultDisplayName();
	}
}
