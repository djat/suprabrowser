/**
 * 
 */
package ss.common.networking2;

import java.util.Hashtable;

import ss.common.MapUtils;
import ss.util.SessionConstants;

/**
 *
 */
public class ProtocolStartUpInformation {

	private final String userLogin;
	
	private final String userDisplayName;

	private final String supraSphereId;
		
	/**
	 * @param rawSession
	 */
	public ProtocolStartUpInformation(Hashtable rawSession) {
		this.userLogin = MapUtils.requireValue(rawSession, SessionConstants.USERNAME );
		this.userDisplayName = MapUtils.requireValue(rawSession, SessionConstants.REAL_NAME );
		this.supraSphereId = MapUtils.requireValue(rawSession, SessionConstants.SUPRA_SPHERE );
	}

	/**
	 * @return the userLogin
	 */
	public String getUserLogin() {
		return this.userLogin;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Presence info" 
		+ ", user login " + this.userLogin;
	}

	/**
	 * @return the userDisplayName
	 */
	public String getUserDisplayName() {
		return this.userDisplayName;
	}

	/**
	 * @return
	 */
	public String getSupraSphereId() {
		return this.supraSphereId;
	}
	
	public String generateProtocolDisplayName( String groupName ) {
		return ProtocolUtils.generateProtocolDisplayName( groupName, getUserLogin() );
	}
	
	
	

}
