package ss.lab.dm3.security2;

import java.io.Serializable;

/**
 * @author Dmitry Goncharov
 */
public class Authentication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5853632521744231792L;
	
	private final UserDetails userDetails;
	
	private AuthorityList authorityList;
	
	/**
	 * @param userDetails
	 */
	public Authentication(UserDetails userDetails) {
		super();
		this.userDetails = userDetails;
	}

	/**
	 * 
	 */
	public AuthorityList getAuthorities() {
		return this.authorityList;
	}

	/**
	 * @return the userDetails
	 */
	public UserDetails getUserDetails() {
		return this.userDetails;
	}

}
