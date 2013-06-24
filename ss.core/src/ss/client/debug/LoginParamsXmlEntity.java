package ss.client.debug;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class LoginParamsXmlEntity extends XmlEntityObject {
	
	/**
	 * Create LoginParamsXmlEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static LoginParamsXmlEntity wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, LoginParamsXmlEntity.class);
	}
	
	/**
	 * Create LoginParamsXmlEntity object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static LoginParamsXmlEntity wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, LoginParamsXmlEntity.class);
	}

	private final ISimpleEntityProperty login = super
		.createAttributeProperty( "@login" );
	
	private final ISimpleEntityProperty password = super
		.createAttributeProperty( "@password" );
	
	private final ISimpleEntityProperty sphereUrl = super
		.createAttributeProperty( "@sphere-url" );

	/**
	 * 
	 */
	public LoginParamsXmlEntity() {
		super( "login-params" );
	}

	public String getSphereUrl() {
		return this.sphereUrl.getValue();
	}

	public String getLogin() {
		return this.login.getValue();
	}

	public String getPassword() {
		return this.password.getValue();
	}

	/**
	 * @param login
	 */
	public void setLogin(String login) {
		this.login.setValue(login);
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password.setValue(password);
	}

	/**
	 * @param sphereUrl
	 */
	public void setSphereUrl(String sphereUrl) {
		this.sphereUrl.setValue(sphereUrl);
	}	
	
}
