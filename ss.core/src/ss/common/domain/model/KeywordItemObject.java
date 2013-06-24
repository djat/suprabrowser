/**
 * 
 */
package ss.common.domain.model;

import java.util.Date;

/**
 * @author roman
 *
 */
public class KeywordItemObject extends DomainObject {
	
	private long uniqueId;
	
	private String value;
	
	private Date moment;
	
	private long currentLocation;
	
	private int multiple;
	
	private String contactName;

	/**
	 * @return the uniqueId
	 */
	public long getUniqueId() {
		return this.uniqueId;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the moment
	 */
	public Date getMoment() {
		return this.moment;
	}

	/**
	 * @param moment the moment to set
	 */
	public void setMoment(Date moment) {
		this.moment = moment;
	}

	/**
	 * @return the currentLocation
	 */
	public long getCurrentLocation() {
		return this.currentLocation;
	}

	/**
	 * @param currentLocation the currentLocation to set
	 */
	public void setCurrentLocation(long currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * @return the multiple
	 */
	public int getMultiple() {
		return this.multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return this.contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
