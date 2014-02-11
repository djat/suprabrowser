/**
 * 
 */
package ss.lab.dm3.security2.backend;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.Permission;
import ss.lab.dm3.security2.UserDetails;
import ss.lab.dm3.security2.backend.configuration.SecurityConfiguration;
import ss.lab.dm3.security2.backend.storage.ISecurityDataProvider;
import ss.lab.dm3.security2.backend.storage.jdbc.JdbcSecurityDataProvider;

/**
 * 
 */
public class SecurityManagerBackEnd implements ISecurityManagerBackEnd {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final ISecurityDataProvider securityDataProvider;
	
	private final Hashtable<String, Authentication> builtInAuthentication = new Hashtable<String, Authentication>();

	private final AccessDecisionStrategy accessDecisionStrategy = new AccessDecisionStrategy();

	private final AtomicLong idProvider = new AtomicLong();
	/**
	 * 
	 */
	public SecurityManagerBackEnd( SecurityConfiguration configuration ) {
		super();
		addBuiltIn(1L, SYSTEM_ACCOUNT_NAME);
		this.accessDecisionStrategy.setVoters(configuration.getVoters());
		this.securityDataProvider = new JdbcSecurityDataProvider( configuration );
	}

	/**
	 * @param id
	 * @param accountName
	 */
	private void addBuiltIn(long id, String accountName) {
		this.builtInAuthentication.put(accountName, new Authentication(
			new UserDetails(1L, SYSTEM_ACCOUNT_NAME)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.security2.backend.ISecurityManagerBackEnd#authenticate(java.lang.String)
	 */
	public synchronized Authentication trustedAuthenticate(String accountName) {
		if (accountName == null) {
			throw new NullPointerException("accountName");
		}
		Authentication builtInAuthentication = this.builtInAuthentication.get(accountName);
		if (builtInAuthentication != null) {
			return builtInAuthentication;
		} else {
			// TODG turn on real security mech
			// return this.securityDataProvider.getAuthentication( accountName );
			return new Authentication( new UserDetails( this.idProvider.incrementAndGet(), accountName ) );
		}
	}

	public boolean hasAccess(Authentication authentication, Object object, Permission permission) {
		return this.accessDecisionStrategy.hasAccess(authentication, object, permission);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.backend.ISecurityManagerBackEnd#checkAccountIsFree(java.lang.String)
	 */
	public void checkAccountIsFree(String accountName) throws SecurityException {
		// TODO Auto-generated method stub
		// 
		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.backend.ISecurityManagerBackEnd#createAccount(java.lang.String, ss.lab.dm3.security2.Authority[])
	 */
	public Authentication createAccount(String accountName, Authority... authorities) {
		Authentication authentication = this.securityDataProvider.createAccount(accountName);
		try {
			for( Authority authority : authorities ) {
				this.securityDataProvider.addAuthority(authentication, authority );
			}
		}
		catch(RuntimeException ex) {
			try {
				this.securityDataProvider.deleteAccount(accountName);
			}
			catch( RuntimeException deleteEx ) {
				this.log.error( "Can't delete authentication " + accountName, deleteEx );
			}
			throw ex;
		}
		return authentication;
	}

}
