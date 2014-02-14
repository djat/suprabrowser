/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;
import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.enums.ExpirationTime;
import ss.common.domain.model.enums.MessageType;

/**
 * @author roman
 *
 */
public class WorkflowConfigurationObject extends DomainObject {

	private ExpirationTime expiration;
	
	private MessageType defaultType;
	
	private DeliveryType defaultDelivery;
	
	private final DomainReference<DecisiveDeliveryObject> decisiveDeliveryRef = DomainReference.create(DecisiveDeliveryObject.class);
	
	private final DomainReference<NormalDeliveryObject> normalDeliveryRef = DomainReference.create(NormalDeliveryObject.class);
	
	private final DomainReference<ConfirmReceiptDeliveryObject> confirmReceiptDeliveryRef = DomainReference.create(ConfirmReceiptDeliveryObject.class);
	
	private final DomainReference<PollDeliveryObject> pollDeliveryRef = DomainReference.create(PollDeliveryObject.class);

	/**
	 * @return the expiration
	 */
	public ExpirationTime getExpiration() {
		return this.expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(ExpirationTime expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the defaultType
	 */
	public MessageType getDefaultType() {
		return this.defaultType;
	}

	/**
	 * @param defaultType the defaultType to set
	 */
	public void setDefaultType(MessageType defaultType) {
		this.defaultType = defaultType;
	}

	/**
	 * @return the defaultDelivery
	 */
	public DeliveryType getDefaultDelivery() {
		return this.defaultDelivery;
	}

	/**
	 * @param defaultDelivery the defaultDelivery to set
	 */
	public void setDefaultDelivery(DeliveryType defaultDelivery) {
		this.defaultDelivery = defaultDelivery;
	}

	/**
	 * @return the decisiveDeliveryRef
	 */
	public DomainReference<DecisiveDeliveryObject> getDecisiveDeliveryRef() {
		return this.decisiveDeliveryRef;
	}
	
	public DecisiveDeliveryObject getDecisiveDelivery() {
		return getDecisiveDeliveryRef().get();
	}
	
	public void setDecisiveDelivery(DecisiveDeliveryObject decisiveDelivery) {
		getDecisiveDeliveryRef().set(decisiveDelivery);
	}

	/**
	 * @return the normalDeliveryRef
	 */
	public DomainReference<NormalDeliveryObject> getNormalDeliveryRef() {
		return this.normalDeliveryRef;
	}
	
	public NormalDeliveryObject getNormalDelivery() {
		return getNormalDeliveryRef().get();
	}
	
	public void setNormalDelivery(NormalDeliveryObject normalDelivery) {
		getNormalDeliveryRef().set(normalDelivery);
	}

	/**
	 * @return the confirmReceiptDeliveryRef
	 */
	public DomainReference<ConfirmReceiptDeliveryObject> getConfirmReceiptDeliveryRef() {
		return this.confirmReceiptDeliveryRef;
	}
	
	public ConfirmReceiptDeliveryObject getConfirmReceiptDelivery() {
		return getConfirmReceiptDeliveryRef().get();
	}
	
	public void setConfirmReceiptDelivery(ConfirmReceiptDeliveryObject confirmDelivery) {
		getConfirmReceiptDeliveryRef().set(confirmDelivery);
	}

	/**
	 * @return the pollDeliveryRef
	 */
	public DomainReference<PollDeliveryObject> getPollDeliveryRef() {
		return this.pollDeliveryRef;
	}
	
	public PollDeliveryObject getPollDelivery() {
		return getPollDeliveryRef().get();
	}
	
	public void setPollDelivery(PollDeliveryObject pollDelivery) {
		getPollDeliveryRef().set(pollDelivery);
	}

	/**
	 * @param displayName
	 * @return
	 */
	public AbstractDeliveryObject getDeliveryByDisplayName(final String displayName) {
		if(displayName==null) {
			return null;
		}
		if(this.normalDeliveryRef.get().getDisplayName().equals(displayName)) {
			return this.normalDeliveryRef.get();
		}
		if(this.confirmReceiptDeliveryRef.get().getDisplayName().equals(displayName)) {
			return this.confirmReceiptDeliveryRef.get();
		}
		if(this.pollDeliveryRef.get().getDisplayName().equals(displayName)) {
			return this.pollDeliveryRef.get();
		}
		if(this.decisiveDeliveryRef.get().getDisplayName().equals(displayName)) {
			return this.decisiveDeliveryRef.get();
		}
		return null;
	}

	/**
	 * @param deliveryType
	 * @return
	 */
	public AbstractDeliveryObject getDeliveryByTypeOrNormal(DeliveryType deliveryType) {
		if(deliveryType.equals(DeliveryType.CONFIRM_RECEIPT)) {
			return this.confirmReceiptDeliveryRef.get();
		}
		if(deliveryType.equals(DeliveryType.POLL)) {
			return this.pollDeliveryRef.get();
		}
		if(deliveryType.equals(DeliveryType.DECISIVE)) {
			return this.decisiveDeliveryRef.get();
		}
		return this.normalDeliveryRef.get();
	}
}
