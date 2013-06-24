package ss.common.privileges;

import ss.common.UserSession;

public class Privilege {

	private PrivilegesManager manager;
	
	private String name;
	
	private Permissions permissions = new Permissions();   
	
	/**
	 * @param name
	 */
	public Privilege( PrivilegesManager manager, String name) {
		super();
		this.manager = manager;
		this.name = name;
	}

	/**
	 * @return the name of privilege
	 */
	protected String getName() {
		return this.name;
	}
	

	/**
	 * Retruns true if user can modify permissions for set email forwarding rules (delivery option)
	 * @return
	 */
	public boolean canModifyPermissionForOtherUsers() {
		UserSession session = this.manager.getSessionWrapper();
		Permission userPrivilege  = getUserPermission( session.getUserContactName(), session.getUserLogin() );
		//TODO: remove next line stub
		return userPrivilege == Permissions.PERMISSION_FULL_CONTROL;
	}
		
	/**
	 * Set permission for user. Only admin can set permissions. 
	 * Permissions can be set for admin.
	 * @param userName login user name 
	 * @param contactName contact user name  
	 * @param permission permission level, see PermissionConstants for details   
	 */
	public void setUserPermission( String contactName, String loginName, Permission permission ) {
		UserSession session =  this.manager.getSessionWrapper();
		if ( !session.isAdmin() ) {
			//TODO: resolve what to do, currently simply skip
			return;
		}		
		if ( this.manager.isAdmin( contactName, loginName ) ) {
			// We dont allow to change privelegs for admin
			//TODO: resolve what to do, currently simply skip
			return;
		}
		this.manager.setUserPermissionForPrivilege( getName(), contactName, loginName, permission );	
	}
	
	/**
	 * Returns user permissions
	 * @param contactName
	 * @param loginName
	 * @return
	 */
	public Permission getUserPermission( String contactName, String loginName ) {
		if( this.manager.isAdmin( contactName, loginName ) ) {
			return Permissions.PERMISSION_FULL_CONTROL;
		}
		String privilegeName = this.manager.getUserPermissionForPrivilege( getName(), contactName, loginName );
		Permission userPrivilege = this.getPermissions().Parse( privilegeName );  
		return userPrivilege  != null ? userPrivilege  : getDefaultPermissionLevel();		
	}
	
	/**
     * TODO: make modificable
	 * @return Returns default permission level
	 */
	public Permission getDefaultPermissionLevel() {
		return Permissions.PERMISSION_ALLOW_TO_SELF;
	}

	/**
	 * Returns avaliable permissions for this privilege
	 * @return
	 */
	public Permissions getPermissions() {
		return this.permissions;
	}

	/**
	 * Returns true if user can modify set email forwarding rules (delivery option)
	 * in current sphere, otherwise false   
	 */
	public boolean hasModifyPermissionInCurrentSphere() {
		final UserSession sessionWrapper = this.manager.getSessionWrapper();
		Permission permission = this.getUserPermission(sessionWrapper.getUserContactName(), sessionWrapper.getUserLogin() );
		/// In all cases except deny returns true 
		if ( permission == Permissions.PERMISSION_FULL_CONTROL ||
			permission == Permissions.PERMISSION_ALLOW_TO_USERS || 
			permission == Permissions.PERMISSION_ALLOW_TO_SELF ) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns true if user has mofidy permissions for this privilege
	 */
	public boolean hasModifyPermissionForSphere(String sphereId) {
		// TODO implement
		return true;
	}
		
	
}
