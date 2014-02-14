/**
 * 
 */
package ss.client.ui.clubdealmanagement;

/**
 * @author roman
 *
 */
public class ChangesDetector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangesDetector.class);
	
	private boolean typesChanged = false;
	
	private boolean contactsChanged = false;
	
	private boolean clubdealChanged = false;

	private boolean accessChanged = false;
	
	public ChangesDetector() {
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isTypeChanged() {
		return this.typesChanged;
	}
	
	public void setTypesChanged(final boolean value) {
		this.typesChanged = value;
	}
	
	@SuppressWarnings("unused")
	private boolean isContactChanged() {
		return this.contactsChanged;
	}
	
	public void setContactsChanged(final boolean value) {
		this.contactsChanged = value;
	}
	
	@SuppressWarnings("unused")
	private boolean isClubdealChanged() {
		return this.clubdealChanged;
	}
	
	public void setClubdealChanged(final boolean value) {
		this.clubdealChanged = value;
	}
	
	public boolean hasChanges() { 
		// TODO: Temporary removed, should be investigated more on wrong working of changes detector.
		return false;//isClubdealChanged() || isContactChanged() || isTypeChanged() || isAccessChanged();
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isAccessChanged() {
		return this.accessChanged;
	}
	
	public void setAccessChanged(final boolean value) {
		this.accessChanged = value;
	}
}