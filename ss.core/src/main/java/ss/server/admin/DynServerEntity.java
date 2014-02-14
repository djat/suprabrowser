/**
 * 
 */
package ss.server.admin;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 *
 */
public class DynServerEntity extends XmlEntityObject {

	private final ISimpleEntityProperty ipAddress = super
			.createAttributeProperty("ip_address/@value");

	private final ISimpleEntityProperty port = super
			.createAttributeProperty("port/@value");

	private final ISimpleEntityProperty dbUser = super
			.createAttributeProperty("mysql/@db_user");

	private final ISimpleEntityProperty dbPass = super
			.createAttributeProperty("mysql/@db_pass");

	private final ISimpleEntityProperty dbName = super
		.createAttributeProperty("mysql/@db_name");
	
	private final ISimpleEntityProperty dbSphere = super
		.createAttributeProperty("mysql/@db_sphere");
	
	private final ISimpleEntityProperty url = super
		.createAttributeProperty("mysql/@url");
	
	private final ISimpleEntityProperty versionsStub = super
		.createAttributeProperty("versions/@stub");
	
	
	/**
	 */
	public DynServerEntity() {
		super("dyn_server");
	}
	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return this.dbName.getValue();
	}
	/**
	 * @return the dbPass
	 */
	public String getDbPass() {
		return this.dbPass.getValue();
	}
	/**
	 * @return the dbSphere
	 */
	public String getDbSphere() {
		return this.dbSphere.getValue();
	}
	/**
	 * @return the dbUser
	 */
	public String getDbUser() {
		return this.dbUser.getValue();
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return this.ipAddress.getValue();
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return this.port.getValue();
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url.getValue();
	}
	
	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName.setValue(dbName);
	}
	/**
	 * @param dbPass the dbPass to set
	 */
	public void setDbPass(String dbPass) {
		this.dbPass.setValue(dbPass);
	}
	/**
	 * @param dbSphere the dbSphere to set
	 */
	public void setDbSphere(String dbSphere) {
		this.dbSphere.setValue(dbSphere);
	}
	/**
	 * @param dbUser the dbUser to set
	 */
	public void setDbUser(String dbUser) {
		this.dbUser.setValue(dbUser);
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress.setValue(ipAddress);
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port.setIntValue(port);
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url.setValue(url);
	}
	
	/**
	 */
	private void addVersionsStub() {
		this.versionsStub.setValue("stub");
	}
	
	public void initalize( int port, String dbName, String dbUser, String dbPass ) {
		addVersionsStub();
		setIpAddress( "127.0.0.1" );
		setPort(port);
		setDbName(dbName);
		setDbUser(dbUser);
		setDbPass(dbPass);
		setUrl( SupraServerCreator.createDbURL( dbName, dbUser, dbPass ) );
	}
	
	
	
	
	/*
	<dyn_server> 
	  <ip_address value="127.0.0.1"/>  
	  <port value="3000"/>  
	  <!-- mysql db_user="dima" db_pass="" db_name="supradevelopment" db_sphere="ds" url="jdbc:mysql://127.0.0.1/supradevelopment?autoReconnect=true&amp;characterEncoding=Cp1252&amp;user=dima"/ -->  
	  <mysql db_user="dima" db_pass="" db_name="ds" db_sphere="ds" url="jdbc:mysql://127.0.0.1/ds?autoReconnect=true&amp;characterEncoding=Cp1252&amp;user=dima"/>  
	  <versions> 
	    <!--     <asset name="supra.3074_testing.jar" current_version="1.5" port="3091" suprajar="true" location="/"/> --> 
	  </versions> 
	</dyn_server>
	*/
}
