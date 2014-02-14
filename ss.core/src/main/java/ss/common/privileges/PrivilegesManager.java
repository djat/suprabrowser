package ss.common.privileges;

import ss.common.UserSession;
import ss.common.VerifyAuth;
import ss.common.VerifyAuthOld;

public class PrivilegesManager {

	private static final String SET_DELIVERY_OPTIONS_PRIVILEGE_NAME = "set_delivery_options";

	private VerifyAuth verifyAuth;
	private PrivilegesXmlData privilegesXmlData;
	private Privilege setDeliveryOptionPrivilege;

	public PrivilegesManager(VerifyAuth verifyAuth) {
		super();
		this.verifyAuth = verifyAuth;
		this.privilegesXmlData = new PrivilegesXmlData(VerifyAuthOld
				.requiredOldVerifyAuth(verifyAuth));
		this.setDeliveryOptionPrivilege = new Privilege(this,
				SET_DELIVERY_OPTIONS_PRIVILEGE_NAME);
	}

	/**
	 * @return the Set Delivery Option privilege
	 */
	public Privilege getSetDeliveryOptionPrivilege() {
		return this.setDeliveryOptionPrivilege;
	}

	/**
	 * Returns user permissions
	 * 
	 * @param contactName
	 * @param loginName
	 * @return
	 */
	public Permission getUserPrivilege(String contactName, String loginName) {
		return this.getSetDeliveryOptionPrivilege().getUserPermission(
				contactName, loginName);
	}

	/**
	 * Returns session wrapper
	 */
	UserSession getSessionWrapper() {
		return this.verifyAuth.getUserSession();
	}

	/**
	 * Returns true if user with specified contact name and login name is admin
	 */
	boolean isAdmin(String contactName, String loginName) {
		return this.verifyAuth.isAdmin(contactName, loginName);
	}

	/**
	 * Sets permission level for specified privilege and user
	 */
	void setUserPermissionForPrivilege(String privilegeName,
			String contactName, String loginName, Permission permissionLevel) {
		this.privilegesXmlData.setUserPermissionLevelForPrivilege(
				privilegeName, contactName, loginName,
				permissionLevel != null ? permissionLevel.getLevel() : null);
	}

	/**
	 * Gets permission level for specified privilege and user
	 */
	String getUserPermissionForPrivilege(String privilegeName,
			String contactName, String loginName) {
		return this.privilegesXmlData.getUserPermissionLevelForPrivilege(
				privilegeName, contactName, loginName);
	}
}
