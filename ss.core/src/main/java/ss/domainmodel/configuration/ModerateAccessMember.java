/**
 * 
 */
package ss.domainmodel.configuration;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class ModerateAccessMember extends XmlEntityObject {

	public static final String ROOT_ELEMENT_NAME = "member";

	private final ISimpleEntityProperty login_name = super
		.createAttributeProperty("@system_name");

	private final ISimpleEntityProperty contact_name = super
		.createAttributeProperty("@display_name");
	
	private final ISimpleEntityProperty access_allowed = super
	.createAttributeProperty("@access_allowed");
	
	public ModerateAccessMember() {
		super(ROOT_ELEMENT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	public static ModerateAccessMember wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ModerateAccessMember.class);
	}

	@SuppressWarnings("unchecked")
	public static ModerateAccessMember wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ModerateAccessMember.class);
	}

//	public String getLoginName() {
//		return this.login_name.getValue();
//	}
	
	public void setLoginName(final String value) {
		this.login_name.setValue(value);
	}


	public String getContactName() {
		return this.contact_name.getValue();
	}
	
	public void setContactName(final String value) {
		this.contact_name.setValue(value);
	}

	public boolean isModerator() {
		return this.access_allowed.getBooleanValue(false);
	}
	
	public void setModerator(final boolean value) {
		this.access_allowed.setBooleanValue(value);
	}
}
