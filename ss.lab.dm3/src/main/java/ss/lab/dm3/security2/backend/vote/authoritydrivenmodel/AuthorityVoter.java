package ss.lab.dm3.security2.backend.vote.authoritydrivenmodel;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.SecurityId;
import ss.lab.dm3.security2.backend.vote.Voter;

public class AuthorityVoter extends Voter {

	private final AuthorityDrivenModel model = new AuthorityDrivenModel();
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.vote.Voter#voteAccess(ss.lab.dm3.security2.Authentication, java.lang.Object, ss.lab.dm3.security2.Permission)
	 */
	@Override
	public Access voteAccess(Authentication authentication, Object object,
			Permission permission) {
		SecurityId id = this.securityIdentifierProvider.getSecurityId( object );
		for( Authority authority : authentication.getAuthorities() ) {
			AuthoryPemissions authorityPemissions = this.model.get( authority );
			Permission authorityPermission = authorityPemissions.get( id );
			if ( authorityPermission != null ) {
				if ( authorityPermission.contains( permission ) ) {
					return Access.GRANTED;
				}
			}
		}
		return Access.ABSTAIN;
	}

	public AuthorityDrivenModel getModel() {
		return this.model;
	}
	
	

}
