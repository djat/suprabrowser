/**
 * 
 */
package ss.domainmodel.workflow;

import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class ModelMemberEntityObject extends XmlEntityObject {

	public static final String ROOT_ELEMENT_NAME = "member";
	
	private final ISimpleEntityProperty contactName = super
	.createAttributeProperty( "@contact_name" );
	
	private final ISimpleEntityProperty userName = super
	.createAttributeProperty( "@user_name" );
	
	private final ISimpleEntityProperty role = super
	.createAttributeProperty( "@role" );
	
	
	public ModelMemberEntityObject() {
		super(ROOT_ELEMENT_NAME);
	}

	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ModelMemberEntityObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ModelMemberEntityObject.class);
	}

	/**
	 * Create SphereDefinition object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ModelMemberEntityObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ModelMemberEntityObject.class);
	}
	
	public String getContactName() {
		return this.contactName.getValue();
	}
	
	public void setContactName(String value) {
		this.contactName.setValue(value);
	}
	
	public String getUserName() {
		return this.userName.getValue();
	}
	
	public void setUserName(String value) {
		this.userName.setValue(value);
	}
	
	public String getRoleName() {
		return this.role.getValue();
	}
	
	public void setRoleName(String value) {
		this.role.setValue(value);
	}

	/**
	 * @param member
	 * @return
	 */
	public static ModelMemberEntityObject wrap(SphereMember member) {
		ModelMemberEntityObject newMember = new ModelMemberEntityObject();
		newMember.setContactName(member.getMemberContactName());
		newMember.setUserName(member.getMemberLogin());
		return newMember;
	}

	/**
	 * @param defaultRole
	 */
	public void setRoleName(Role defaultRole) {
		this.role.setValue(defaultRole.getTitle());
	}

	/**
	 * @return
	 */
	public ModelMemberEntityObject copy() {
		return ModelMemberEntityObject.wrap(getDocumentCopy());
	}
}
