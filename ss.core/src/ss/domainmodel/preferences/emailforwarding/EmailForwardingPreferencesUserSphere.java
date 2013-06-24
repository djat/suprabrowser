/**
 * 
 */
package ss.domainmodel.preferences.emailforwarding;

import ss.framework.entities.ISimpleEntityProperty;

/**
 * @author zobo
 *
 */
public class EmailForwardingPreferencesUserSphere extends CommonEmailForwardingPreferences {
	
	/**
	 * true if needed overwrite global forwarding settings for specific sphere
	 */
	private final ISimpleEntityProperty setted = super
		.createAttributeProperty("forwarding/setted/@value");

	/**
	 * @return the setted
	 */
	public boolean isSetted() {
		return this.setted.getBooleanValue(false);
	}

	/**
	 * @param setted the setted to set
	 */
	public void setSetted(boolean setted) {
		this.setted.setBooleanValue(setted);
	}	
}
