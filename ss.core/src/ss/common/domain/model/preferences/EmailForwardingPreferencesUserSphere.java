/**
 * 
 */
package ss.common.domain.model.preferences;


/**
 * @author roman
 *
 */
public class EmailForwardingPreferencesUserSphere extends CommonEmailPreferences {

	private boolean setted;

	/**
	 * @return the setted
	 */
	public boolean isSetted() {
		return this.setted;
	}

	/**
	 * @param setted the setted to set
	 */
	public void setSetted(boolean setted) {
		this.setted = setted;
	}
}
