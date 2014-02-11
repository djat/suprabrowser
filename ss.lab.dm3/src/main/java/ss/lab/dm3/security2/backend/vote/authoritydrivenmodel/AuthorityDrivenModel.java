package ss.lab.dm3.security2.backend.vote.authoritydrivenmodel;

import java.util.Hashtable;

import ss.lab.dm3.security2.Authority;

public class AuthorityDrivenModel {

	private final Hashtable<Authority,AuthoryPemissions> authorityToPermissions = new Hashtable<Authority, AuthoryPemissions>();

	/**
	 * @param authority
	 * @return
	 */
	public AuthoryPemissions get(Authority authority) {
		return this.authorityToPermissions.get( authority );
	}

	public void put(Authority authority, AuthoryPemissions permissions) {
		this.authorityToPermissions.put(authority, permissions);
	}
	
}
