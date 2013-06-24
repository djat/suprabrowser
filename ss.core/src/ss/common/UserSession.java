package ss.common;

import ss.common.privileges.Permission;
import ss.global.SSLogger;
import ss.server.networking.SC;
import ss.util.SessionConstants;

import java.util.*;

import org.apache.log4j.Logger;

public class UserSession {

	public static final UserSession NullSession = new UserSession();

	@SuppressWarnings("unused")
	private Logger logger = SSLogger.getLogger(this.getClass());
	
	private VerifyAuth verifyAuth;

	private Hashtable session;

	public UserSession(VerifyAuth verifyAuth, Hashtable session) {
		super();
		if ( verifyAuth == null ) {
			throw new NullPointerException( "verifyAuth is null" );
		}
		if ( session == null ) {
			session = new Hashtable();
		}
		this.verifyAuth = verifyAuth;
		this.session = session;
	}
	
	/**
	 * Create nully session
	 */
	private UserSession() {
		//TODO:  think about verifyAuth
		this.session = new Hashtable();
	}

	/**
	 * @return login name from session
	 */
	public String getUserLogin() {
		return this.getStringValue(SessionConstants.USERNAME);
	}

	/**
	 * @return contact name from session
	 */
	public String getUserContactName() {
		return this.verifyAuth.getRealName(getUserLogin());
	}

	/**
	 * Returns string value or null if value is not found or not string
	 * 
	 * @param key
	 *            key for value
	 * @return
	 */
	private String getStringValue(String key) {
		Object value = this.session.get(key);
		return value instanceof String ? (String) value : null;
	}

	/**
	 * @return current user privilege
	 */
	public Permission getUserPrivilege() {
		return this.verifyAuth.getPrivilegesManager().getUserPrivilege(
				getUserContactName(), getUserLogin());
	}

	/**
	 * @return true if current user is administrator
	 */
	public boolean isAdmin() {
		return this.verifyAuth.isAdmin(getUserContactName(),getUserLogin());
	}

	/**
	 * Returns current shpere id 
	 */
	public String getSphereId() {
		return getStringValue( SC.SPHERE_ID);
	}

	/**
	 * Returns current real name 
	 */
	public String getRealName() {
		return (String) this.session.get("real_name");
	}

	/**
	 * Create and retruns Generic Xml Document 
	 */
	public GenericXMLDocument CreateGenericXMLDocument() {
		return new GenericXMLDocument( this.session );
	}

	/**
	 * Returns hastable that implements session
	 */
	public Hashtable getImeplementationHashtable() {
		return this.session;
	}

	/**
	 * Returns supra_sphere value
	 */
	public String getSupraSphere() {
		return this.getStringValue( "supra_sphere" );
	}	
	
}
