/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * 
 */
public class MemberReference extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "member";
	
	public static final String NO_TYPE = "no type";
	
	private final ISimpleEntityProperty contactName = super
			.createAttributeProperty("@contact_name");

	private final ISimpleEntityProperty loginName = super
			.createAttributeProperty("@login_name");
	
	private final ISimpleEntityProperty type = super
	.createAttributeProperty("@type");

	/**
	 * Create Contact object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MemberReference wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, MemberReference.class);
	}
	
	/**
	 * Create Contact object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MemberReference wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, MemberReference.class);
	}
	
	/**
	 * 
	 */
	public MemberReference() {
		super( ITEM_ROOT_ELEMENT_NAME );
	}


	/**
	 * Gets the contact name
	 */
	public final String getContactName() {
		return this.contactName.getValue();
	}

	/**
	 * Sets the contact name
	 */
	public final void setContactName(String value) {
		this.contactName.setValue(value);
	}

	/**
	 * Gets the contact login
	 */
	public final String getLoginName() {
		return this.loginName.getValue();
	}

	/**
	 * Sets the contact login
	 */
	public final void setLoginName(String value) {
		this.loginName.setValue(value);
	}
	
	/**
	 * Gets the contact type
	 */
	public final String getType() {
		return this.type.getValueOrDefault(NO_TYPE);
	}

	/**
	 * Sets the contact type
	 */
	public final void setType(String value) {
		if(value==null) {
			value = NO_TYPE;
		}
		this.type.setValue(value);
	}

}