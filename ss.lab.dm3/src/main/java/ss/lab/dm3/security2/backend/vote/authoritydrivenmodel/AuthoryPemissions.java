package ss.lab.dm3.security2.backend.vote.authoritydrivenmodel;

import java.util.Hashtable;

import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.SecurityId;

public class AuthoryPemissions {

	private final Hashtable<SecurityId, Permission> securityIdToPermission = new Hashtable<SecurityId, Permission>();
	
	/**
	 * @param id
	 * @return
	 */
	public Permission get(SecurityId id) {
		while( id != null ) {
			Permission permission = this.securityIdToPermission.get( id );
			if ( permission != null ) {
				return permission;
			}
			id = id.getParent();
		}
		return null;
	}
	
	public void put(SecurityId id, Permission permission) {
		this.securityIdToPermission.put( id, permission );
	}

}
