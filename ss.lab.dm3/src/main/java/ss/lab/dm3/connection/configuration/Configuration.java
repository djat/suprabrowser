/**
 * 
 */
package ss.lab.dm3.connection.configuration;

import ss.lab.dm3.persist.backend.ReadWriterLockProvider;
import ss.lab.dm3.persist.backend.search.SearchConfiguration;
import ss.lab.dm3.blob.configuration.BlobConfiguration;

/**
 * 
 */
public class Configuration {

	private String dbUrl = null;

	private String dbUser = null;

	private String dbPassword = null;

	private final DomainDataClassList domainDataClasses = new DomainDataClassList();

	private final InternalDataClassList internalDataClassList = new InternalDataClassList();

	private final ScriptHandlerList scriptHandlerList = new ScriptHandlerList();

	private final DomainPackageList domainPackages = new DomainPackageList();
	
	private final SearchConfiguration searchConfiguration = new SearchConfiguration();

	private final BlobConfiguration blobConfiguration = new BlobConfiguration(); 
	
	private ReadWriterLockProvider lockProvider = new ReadWriterLockProvider();

	/**
	 * 
	 */
	public Configuration() {
		super();
	}

	/**
	 * @return the domainDataClasses
	 */
	public DomainDataClassList getDomainDataClasses() {
		return this.domainDataClasses;
	}

	/**
	 * @return the dbUrl
	 */
	public String getDbUrl() {
		return this.dbUrl;
	}

	/**
	 * @return the dbUserName
	 */
	public String getDbUser() {
		return this.dbUser;
	}

	/**
	 * @return the dbPassword
	 */
	public String getDbPassword() {
		return this.dbPassword;
	}

	/**
	 * @param dbUrl
	 *            the dbUrl to set
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	/**
	 * @param dbUserName
	 *            the dbUserName to set
	 */
	public void setDbUser(String dbUserName) {
		this.dbUser = dbUserName;
	}

	/**
	 * @param dbPassword
	 *            the dbPassword to set
	 */
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	/**
	 * Not used any more
	 * @return
	 */
	@Deprecated
	public DomainPackageList getDomainPackages() {
		return this.domainPackages;
	}

	/**
	 * @return
	 */
	public String getBaseHibernateConfigurationName() {
		return "/base.hibernate.cfg.xml";
	}

	/**
	 * 
	 */
	public ScriptHandlerList getScriptHandlers() {
		return this.scriptHandlerList;
	}

	public InternalDataClassList getInternalDataClassList() {
		return this.internalDataClassList;
	}

	public SearchConfiguration getSearchConfiguration() {
		return this.searchConfiguration;
	}

	/**
	 * @return
	 */
	public BlobConfiguration getBlobConfiguration() {
		return this.blobConfiguration;
	}

	public ReadWriterLockProvider getLockProvider() {
		return lockProvider;
	}

	public void setLockProvider(ReadWriterLockProvider lockProvider) {
		this.lockProvider = lockProvider;
	}

	

}
