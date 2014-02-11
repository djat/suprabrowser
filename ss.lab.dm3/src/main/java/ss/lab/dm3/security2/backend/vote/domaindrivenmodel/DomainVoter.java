package ss.lab.dm3.security2.backend.vote.domaindrivenmodel;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.SecurityId;
import ss.lab.dm3.security2.backend.vote.Voter;

/**
 * TODO implement voting base on domain state
 * @author Dmitry Goncharov
 *
 */
public class DomainVoter extends Voter {

	private final DomainDrivenModel domainDrivenModel = new DomainDrivenModel();
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.vote.Voter#voteAccess(ss.lab.dm3.security2.Authentication, java.lang.Object, ss.lab.dm3.security2.Permission)
	 */
	@Override
	public Access voteAccess(Authentication authentication, Object object,
			Permission permission) {
		if ( object instanceof MappedObject /*DomainObject || object instanceof DataObject*/ ) {
			SecurityId id = this.securityIdentifierProvider.getSecurityId(object);
			PermissionHolder permissionHolder = this.domainDrivenModel.getPermissionHolder( id );
			if ( permissionHolder != null ) {
				for( Authority authority : authentication.getAuthorities() ) {
					Permission allowedPermission = permissionHolder.getAllowedPermissions( authority );
					if ( allowedPermission != null ) {
						if ( allowedPermission.contains(permission) ) {
							return Access.GRANTED;
						}
					}
					Permission deniedPermission = permissionHolder.getDeniedPermissions( authority );
					if ( deniedPermission != null ) {
						if ( deniedPermission.contains(permission) ) {
							return Access.DENIED;
						}
					}
				}
			}
		}
		return Access.ABSTAIN;
	}

}
