/**
 * 
 */
package ss.lab.dm3.security2.backend;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;

/**
 *
 */
public interface ISecurityManagerBackEnd {

	/**
	 * 
	 */
	public static final String SYSTEM_ACCOUNT_NAME = "#system";
	
	public static final String RUN_AS_ACCOUNT_NAME = "#runas";
	
	/**
	 * @param accountName
	 */
	Authentication trustedAuthenticate(String accountName);

	boolean hasAccess(Authentication authentication, Object object, Permission permission);

	/**
	 * @param accountName
	 */
	void checkAccountIsFree(String accountName) throws SecurityException;

	/**
	 * 
	 */
	Authentication createAccount(String accountName, Authority ... authorities);
}
