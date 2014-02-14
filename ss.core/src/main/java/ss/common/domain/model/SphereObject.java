/**
 * 
 */
package ss.common.domain.model;

import ss.common.domain.model.collections.LocationCollection;
import ss.common.domain.model.collections.MemberCollection;
import ss.common.domain.model.enums.ExpirationTime;
import ss.common.domain.model.enums.MessageType;
import ss.common.domain.model.enums.SphereType;
import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.message.MessageTypeCollection;
import ss.common.domain.model.trash.UiObject;

/**
 * @author roman
 *
 */
public class SphereObject extends DomainObject {

	private String systemName;
	
	private String displayName;
	
	private SphereType sphereType;
	
	private final DomainReference<UiObject> uiObject = DomainReference.create(UiObject.class);
	
	private ExpirationTime expiration;
	
	private DeliveryType defaultDelivery;
	
	private MessageType defaultType;
	
	private final MessageTypeCollection threadTypes = new MessageTypeCollection();
	
	private final LocationCollection locations = new LocationCollection();
	
	private final MemberCollection members = new MemberCollection();

	/**
	 * @return the systemName
	 */
	public String getSystemName() {
		return this.systemName;
	}

	/**
	 * @param systemName the systemName to set
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the sphereType
	 */
	public SphereType getSphereType() {
		return this.sphereType;
	}

	/**
	 * @param sphereType the sphereType to set
	 */
	public void setSphereType(SphereType sphereType) {
		this.sphereType = sphereType;
	}

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
	 * @return the uiObject
	 */
	public DomainReference<UiObject> getUiObject() {
		return this.uiObject;
	}

	/**
	 * @return the threadTypes
	 */
	public MessageTypeCollection getThreadTypes() {
		return this.threadTypes;
	}

	/**
	 * @return the locations
	 */
	public LocationCollection getLocations() {
		return this.locations;
	}

	/**
	 * @return the members
	 */
	public MemberCollection getMembers() {
		return this.members;
	}
}
