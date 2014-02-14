/**
 * 
 */
package ss.domainmodel.admin;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 *
 */
public class AdminItem extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "supra";
	
	private final ISimpleEntityProperty contact = super
		.createAttributeProperty( "@contact_name" );

	private final ISimpleEntityProperty login = super
		.createAttributeProperty( "@login_name" );

	private final ISimpleEntityProperty main = super
		.createAttributeProperty( "@main" );
	
	public AdminItem(){
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public String getContact() {
		return this.contact.getValue();
	}

	public void setContact(String value) {
		this.contact.setValue(value);
	}

	public String getLogin() {
		return this.login.getValue();
	}

	public void setLogin(String value) {
		this.login.setValue(value);
	}

	public boolean isMain() {
		return this.main.getBooleanValue(false);
	}

	public void setMain(boolean value) {
		this.main.setBooleanValue(value);
	}

	@Override
	public String toString() {
		return "Admin: login: " + getLogin() + ", contactName: " + getContact() + (isMain() ? " (MAIN)" : "");
	}
}
