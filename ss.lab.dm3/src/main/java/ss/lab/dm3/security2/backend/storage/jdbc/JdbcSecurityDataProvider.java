package ss.lab.dm3.security2.backend.storage.jdbc;

import java.beans.PropertyVetoException;
import java.util.Set;


import com.mchange.v2.c3p0.ComboPooledDataSource;

import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.Authority;
import ss.lab.dm3.security2.UserDetails;
import ss.lab.dm3.security2.backend.configuration.SecurityConfiguration;
import ss.lab.dm3.security2.backend.storage.ISecurityDataProvider;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.DaoProvider;
import ss.lab.dm3.security2.backend.storage.jdbc.dao.DaoProviderRegistry;

public class JdbcSecurityDataProvider implements ISecurityDataProvider {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private ComboPooledDataSource cpds = new ComboPooledDataSource();
	
	private final DaoProviderRegistry daoProviderRegistry;
	
	/**
	 * @param configuration
	 */
	public JdbcSecurityDataProvider(SecurityConfiguration configuration) {
		try {
			this.cpds.setDriverClass( "com.mysql.jdbc.Driver" );
		}
		catch (PropertyVetoException ex) {
			this.log.error( "Can't update jdbc driver class", ex );
		}             
		this.cpds.setJdbcUrl( configuration.getDbUrl() );
		this.cpds.setUser( configuration.getDbUser() );                                  
		this.cpds.setPassword( configuration.getDbPassword() );
		// the optional settings
		this.cpds.setMinPoolSize(5);                                     
		this.cpds.setAcquireIncrement(5);
		this.cpds.setMaxPoolSize(20);
		this.daoProviderRegistry = new DaoProviderRegistry( this.cpds );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.backend.storage.ISecurityDataProvider#getAuthentication(java.lang.String)
	 */
	public Authentication getAuthentication(String accountName) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			UserDetails userDetails = getUserDetails(accountName);
			Authentication authentication = new Authentication(userDetails);
			authentication.getAuthorities().set(getUserAuthorities(authentication));
			return authentication;
		}
		finally {
			daoProvider.release();
		}
	}
	
	public void deleteAccount(String accountName) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			daoProvider.get( UserDetailsDao.class ).delete( accountName );
		}
		finally {
			daoProvider.release();
		}
	}
	
	public void addAuthority(Authentication authentication, Authority authority ) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			daoProvider.get( UserAuthorityDao.class ).addUserAuthority( authentication.getUserDetails().getId(), authority );
			authentication.getAuthorities().add(authority);
		}
		finally {
			daoProvider.release();
		}
	}
	
	public void removeAuthority(Authentication authentication, Authority authority ) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			daoProvider.get( UserAuthorityDao.class ).removeUserAuthority( authentication.getUserDetails().getId(), authority );
			authentication.getAuthorities().remove(authority);
		}
		finally {
			daoProvider.release();
		}
	}
	
	
	public UserDetails getUserDetails(String accountName) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			return daoProvider.get( UserDetailsDao.class ).get( accountName );
		}
		finally {
			daoProvider.release();
		}
	}	

	public Set<Authority> getUserAuthorities(Authentication authentication) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			return daoProvider.get( UserAuthorityDao.class ).getUserAuthorities( authentication.getUserDetails().getId() );
		}
		finally {
			daoProvider.release();
		}
	}

	protected void deleteUserDetails(String accountName) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			daoProvider.get( UserDetailsDao.class ).delete( accountName );
		}
		finally {
			daoProvider.release();
		}		
	}
		
	public Authentication createAccount(String accountName) {
		return new Authentication( createUserDetails(accountName) );
	}
	
	public UserDetails createUserDetails(String accountName) {
		DaoProvider daoProvider = getCurrentDaoProvider();
		try {
			return daoProvider.get( UserDetailsDao.class ).create( accountName );
		}
		finally {
			daoProvider.release();
		}
	}


	/**
	 * @return
	 */
	public DaoProvider getCurrentDaoProvider() {
		return this.daoProviderRegistry.getCurrentDaoProvider();
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.security2.backend.storage.ISecurityDataProvider#dispose()
	 */
	public void dispose() {
		this.cpds.close();
	}

	
}
