/**
 * 
 */
package ss.server.admin.gui;

/**
 * @author zobo
 *
 */
public class SupraServerCreateStartGUIParameters {

	final String contactName;
	final String loginName;
	final String supraSphereName;
	final String mainDomain;
	final int port; 
	final String passphrase;
	final String databaseName;
	final String databaseUserName;
	final String databasePassword;
	
	/**
	 * @param contactName
	 * @param loginName
	 * @param supraSphereName
	 * @param mainDomain
	 * @param port
	 * @param passphrase
	 * @param databaseName
	 * @param databaseUserName
	 * @param databasePassword
	 */
	public SupraServerCreateStartGUIParameters(final String contactName, final String loginName, final String supraSphereName, final String mainDomain, final int port, final String passphrase, final String databaseName, final String databaseUserName, final String databasePassword) {
		super();
		this.contactName = contactName;
		this.loginName = loginName;
		this.supraSphereName = supraSphereName;
		this.mainDomain = mainDomain;
		this.port = port;
		this.passphrase = passphrase;
		this.databaseName = databaseName;
		this.databaseUserName = databaseUserName;
		this.databasePassword = databasePassword;
	}

	public String getContactName() {
		return this.contactName;
	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public String getMainDomain() {
		return this.mainDomain;
	}

	public String getPassphrase() {
		return this.passphrase;
	}

	public String getSupraSphereName() {
		return this.supraSphereName;
	}
	
	/**
	 * @return the databasePassword
	 */
	public String getDatabasePassword() {
		return this.databasePassword;
	}

	/**
	 * @return the databaseUserName
	 */
	public String getDatabaseUserName() {
		return this.databaseUserName;
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	public String[] getArgs(){
		String[] args = new String[6];
		args[0] = new String(this.contactName);
		args[1] = new String(this.loginName);
		args[2] = new String(this.passphrase);
		args[3] = new String(this.databaseName);
		args[4] = new String(this.supraSphereName);
		args[5] = new String(this.mainDomain);
		return args;
	}
}
