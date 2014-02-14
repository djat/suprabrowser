package ss.common.protocolobjects;

import java.util.Hashtable;

public class SaveUserPrivilegeProtocolObject extends AbstractProtocolObject {

	public final static String USER_LOGIN_VALUE_KEY = "login_value_key";
	
	public final static String USER_PRIVILEGES_VALUE_KEY = "privileges_value_key";
	
	/**
	 * 
	 */
	public SaveUserPrivilegeProtocolObject() {
		super();
	}


	public SaveUserPrivilegeProtocolObject( Hashtable valuesMap ) {
		super( valuesMap );		
	}


	/**
	 * Sets user login
	 */
	public void setUserLogin(String userLogin) {
		this.putNotNullValue( USER_LOGIN_VALUE_KEY, userLogin );
	}


	/**
	 * Gets user login
	 */
	public String getUserLogin() {
		return this.getStringValue( USER_LOGIN_VALUE_KEY );
	}

	/**
	 * Sets user privileges
	 */
	public void setUserPrivileges(String userPrivileges) {
		this.putNotNullValue( USER_PRIVILEGES_VALUE_KEY, userPrivileges );
	}

	/**
	 * Gets user privileges
	 */
	public String getUserPermission() {
		return this.getStringValue( USER_PRIVILEGES_VALUE_KEY );
	}
	
	
}
