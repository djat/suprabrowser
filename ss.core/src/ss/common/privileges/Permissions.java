package ss.common.privileges;

import java.util.*;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class Permissions {

	Logger logger = SSLogger.getLogger( this.getClass() );
	
	public static final Permission PERMISSION_DENY = new Permission( "permission_deny", "Deny" );

	public static final Permission PERMISSION_ALLOW_TO_SELF = new Permission( "permission_allow_to_self", "Allow to self" );

	public static final Permission PERMISSION_ALLOW_TO_USERS = new Permission( "permission_allow_to_users", "Allow to all users" );

	public static final Permission PERMISSION_FULL_CONTROL = new Permission( "permission_full_control", "Full control" );

	/**
	 * @return vector of privilges that can be appied
	 */
	public List<Permission> getPermissionsThatCanBeApplied() {
		List<Permission> permissions = new LinkedList<Permission>();
		permissions.add( PERMISSION_ALLOW_TO_SELF );
		permissions.add( PERMISSION_ALLOW_TO_USERS ); 
		permissions.add( PERMISSION_DENY );		
		return permissions; 
	}

	/**
	 * @return list of all privileges
	 */
	private List<Permission> getAllPermissions() {
		List<Permission> permissions = getPermissionsThatCanBeApplied();
		permissions.add( PERMISSION_FULL_CONTROL );		
		return permissions; 
	}

	/**
	 * Parse user permission if permission was found in this permissions or null if 
	 * no permission was found
	 */
	public Permission Parse(String userPermission) {
		if ( userPermission == null ) {
			this.logger.debug( "user permission string is null" );
			return null;
		}
	 	for( Permission permission : getAllPermissions() ) {
	 		if ( permission.getLevel().equals( userPermission ) ){
	 			return permission;
	 		}
	 	}
	 	this.logger.debug( String.format( "user permission %s not found", userPermission ) );
	 	return null;
	}
}
