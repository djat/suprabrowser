/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.suprasphere.SphereReferenceObject;

/**
 * @author roman
 *
 */
public class TabOrderObject extends DomainObject {

	private int order;
	
	private final DomainReference<SphereReferenceObject> sphereRef = DomainReference.create(SphereReferenceObject.class);

	/**
	 * @return the order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the sphereRef
	 */
	public DomainReference<SphereReferenceObject> getSphereRef() {
		return this.sphereRef;
	}

	/**
	 * @return the sphere
	 */
	public SphereReferenceObject getSphere() {
		return this.sphereRef.get();
	}

	/**
	 * @param sphere the sphere to set
	 */
	public void setSphere(SphereReferenceObject sphere) {
		this.sphereRef.set(sphere);
	}
}
