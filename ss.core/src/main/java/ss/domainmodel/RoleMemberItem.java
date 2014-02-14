/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class RoleMemberItem extends XmlEntityObject {
	
	private final ISimpleEntityProperty userName = super
	.createAttributeProperty( "@username" );
	
	private final ISimpleEntityProperty contactName = super
	.createAttributeProperty( "@contact_name" );

	public static final String ITEM_ROOT_ELEMENT_NAME = "member";
	
	@SuppressWarnings("unchecked")
	public static RoleMemberItem wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, RoleMemberItem.class);
	}

	@SuppressWarnings("unchecked")
	public static RoleMemberItem wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, RoleMemberItem.class);
	}
	
	public RoleMemberItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public void setUserName(String value) {
		this.userName.setValue(value);
	}
	
	public String getUserName() {
		return this.userName.getValue();
	}
	
	public void setContactName(String value) {
		this.contactName.setValue(value);
	}
	
	public String getContactName() {
		return this.contactName.getValue();
	}
}
