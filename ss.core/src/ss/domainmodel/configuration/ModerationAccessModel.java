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
public class ModerationAccessModel extends XmlEntityObject {

	public static final String ROOT_ELEMENT_NAME = "clubdeal";
	
	private final ISimpleEntityProperty system_name = super
			.createAttributeProperty("@system_name");

	private final ISimpleEntityProperty display_name = super
			.createAttributeProperty("@display_name");

	private final ModerateAccessMemberList memberList = super
			.bindListProperty(new ModerateAccessMemberList());

	public ModerationAccessModel() {
		super(ROOT_ELEMENT_NAME);
	}
	
	public String getSystemName() {
		return this.system_name.getValue();
	}
	
	public void setSystemName(final String value) {
		this.system_name.setValue(value);
	}

	public String getDisplayName() {
		return this.display_name.getValue();
	}
	
	public void setDisplayName(final String value) {
		this.display_name.setValue(value);
	}

	public ModerateAccessMemberList getMemberList() {
		return this.memberList;
	}
}
