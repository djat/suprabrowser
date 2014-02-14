package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class UserStatement extends XmlEntityObject{
		
	public static final String ITEM_ROOT_ELEMENT_NAME = "user";
	
	private final ISimpleEntityProperty loginName = super
		.createAttributeProperty( "@login_name" );
	
	private final ISimpleEntityProperty contactName = super
		.createAttributeProperty( "@contact_name" );
	
	private final ISimpleEntityProperty permissionLevel = super
		.createAttributeProperty( "permission_level/@value" );
	
	public UserStatement() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public String getContactName() {
		return this.contactName.getValue();
	}

	
	public void setContactName(String value) {
		this.contactName.setValue( value );
	}
	
	public String getLoginName() {
		return this.loginName.getValue();
	}

	public void setLoginName(String loginName) {
		this.loginName.setValue(loginName);
	}
	
	public String getPermissionLevel() {
		return this.permissionLevel.getValue();
	}

	public void setPermissionLevel(String value) {
		this.permissionLevel.setValue(value);
	}
}
