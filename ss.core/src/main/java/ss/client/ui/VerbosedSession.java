/**
 * 
 */
package ss.client.ui;

import java.util.Hashtable;

import ss.common.ArgumentNullPointerException;
import ss.util.SessionConstants;

/**
 *
 */
public final class VerbosedSession {

	private Hashtable rawSession;
	
	public VerbosedSession() {
		this( new Hashtable());
	}
	
	/**
	 * @param mainRawSession
	 */
	public VerbosedSession(Hashtable rawSession) {
		if (rawSession == null) {
			throw new ArgumentNullPointerException("rawSession");
		}
		this.rawSession = rawSession;
	}

	/**
	 * @return
	 */
	public String getUserLogin() { 
		return getStringValue(SessionConstants.USERNAME);
	}

	/**
	 * @return
	 */
	public String getUserContactName() {
		return getStringValue( SessionConstants.REAL_NAME);
	}

	/**
	 * @param real_name
	 * @return
	 */
	private String getStringValue(String key) {
		return (String) this.rawSession.get(key);
	}

	/**
	 * @return
	 */
	public Hashtable getRawSession() {
		return this.rawSession;
	}

	/**
	 * @param rawSession
	 */
	public void setRawSession(Hashtable rawSession) {
		this.rawSession = rawSession;
	}

	/**
	 * @return
	 */
	public String getUniqueId() {
		return getStringValue( "unique_id" );
	}

	/**
	 * @return
	 */
	public String getProfileId() {
		return getStringValue( SessionConstants.PROFILE_ID );
	}

	/**
	 * @return
	 */
	public Hashtable rawClone() {
		return (Hashtable) this.rawSession.clone();
	}

	/**
	 * @return
	 */
	public String getSurpaSphere() {
		return getStringValue( SessionConstants.SUPRA_SPHERE );
	}

}
