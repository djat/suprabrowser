/**
 * 
 */
package ss.domainmodel.workflow;

import ss.common.ReflectionUtils;

/**
 * @author roman
 *
 */
public final class DeliveryDescriptor {

	private final String type;
	
	private final Class deliveryClass;

	@SuppressWarnings("unchecked")
	public DeliveryDescriptor(final String type, final Class deliveryClass) {
		super();
		this.type = type;
		this.deliveryClass = deliveryClass;
	}

	/**
	 * @return the type
	 */
	public String getType() {
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
	public AbstractDelivery createDelivery() {
		return (AbstractDelivery) ReflectionUtils.create( this.deliveryClass );
	}

	/**
	 * @return
	 */
	public String getDefaultDisplayName() {
		return createDelivery().getDefaultDisplayName();
	}
	
}
