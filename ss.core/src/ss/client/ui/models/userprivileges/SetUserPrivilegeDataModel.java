package ss.client.ui.models.userprivileges;

import java.util.List;

import ss.client.networking.DialogsMainCli;
import ss.common.VerifyAuth;
import ss.common.privileges.*;

public class SetUserPrivilegeDataModel {

	private DialogsMainCli client;
	
	private VerifyAuth verifyAuth;

	private String userLoginName;

	private Permission userPermission;
	
	private Privilege setDeliveryOptionPrivilege;
	/**
	 * @param verifyAuth
	 */
	public SetUserPrivilegeDataModel(DialogsMainCli client, String userLoginName) {
		super();
		this.client = client;
		this.verifyAuth = client.getVerifyAuth();
		this.userLoginName =userLoginName;
		this.setDeliveryOptionPrivilege = this.verifyAuth.getPrivilegesManager().getSetDeliveryOptionPrivilege();
		this.userPermission = this.setDeliveryOptionPrivilege.getUserPermission( getUserContactName(), userLoginName );
	}

	/**
	 * Returns user contact name 
	 */
	public String getUserContactName() {
		return this.verifyAuth.getRealName( this.userLoginName );
	}

	/**
	 * Gets current user permissions
	 */
	public Permission getUserPermission() {
		return this.userPermission;
	}

	/**
	 * Sets current user permissions
	 * @param userPermission new user permission
	 */	
	public void setUserPermission( Permission userPermission ) {
		this.userPermission = userPermission;
	}
	
	/**
	 * Returns list of avaliable user permissions
	 */
	public List<Permission> getAllAvaliableUserPermissions() {
		return this.setDeliveryOptionPrivilege.getPermissions().getPermissionsThatCanBeApplied();
	}

	/**
	 * Save user permissions
	 */
	public void saveUserPermission() {
		this.client.saveUserPrivilege(this.userLoginName, this.userPermission != null ? this.userPermission.getLevel() : "" );
	}

	/**
	 * Returns user privilege selection index or -1 if user privilege is not defined.    
	 * @return
	 */
	public int getUserPermissionSelectionIndex() {
		List<Permission> allPrivileges = getAllAvaliableUserPermissions();
		for( int n = 0; n < allPrivileges.size(); n ++ ){
			if ( allPrivileges.get( n ) == getUserPermission() ) {
				return n;
			}
		}
		return -1;
	}
}
