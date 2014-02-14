/**
 * 
 */
package ss.server.admin;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 * 
 */
public class CreateServerParams extends XmlEntityObject {

	/**
	 * Create LoginParamsXmlEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static CreateServerParams wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, CreateServerParams.class);
	}

	/**
	 * Create LoginParamsXmlEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static CreateServerParams wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, CreateServerParams.class);
	}

	private final ISimpleEntityProperty databaseName = super
			.createAttributeProperty("databaseName/@value");

	private final ISimpleEntityProperty contactName = super
			.createAttributeProperty("contactName/@value");

	private final ISimpleEntityProperty loginName = super
			.createAttributeProperty("loginName/@value");

	/**
	 * may not be specified, then from command string params will be taken as arg[0]
	 */
	private final ISimpleEntityProperty passphrase = super
			.createAttributeProperty("passphrase/@value");

	private final ISimpleEntityProperty supraSphereName = super
			.createAttributeProperty("supraSphereName/@value");
	
	private final ISimpleEntityProperty mainDomain = super
		.createAttributeProperty("mainDomain/@value");

	public CreateServerParams() {
		super("create-server-params");
	}

	public String getContactName() {
		return this.contactName.getValue();
	}

	public void setContactName( final String value ) {
		this.contactName.setValue(value);
	}

	public String getDatabaseName() {
		return this.databaseName.getValue();
	}

	public void setDatabaseName( final String value ) {
		this.databaseName.setValue(value);
	}

	public String getLoginName() {
		return this.loginName.getValue();
	}

	public void setLoginName( final String value ) {
		this.loginName.setValue(value);
	}

	public String getPassphrase() {
		return this.passphrase.getValue();
	}

	public void setPassphrase( final String value ) {
		this.passphrase.setValue(value);
	}

	public String getSupraSphereName() {
		return this.supraSphereName.getValue();
	}

	public void setSupraSphereName( final String value ) {
		this.supraSphereName.setValue(value);
	}

	public String getMainDomain() {
		return this.mainDomain.getValue();
	}

	public void setMainDomain( final String value ) {
		this.mainDomain.setValue(value);
	}
}
