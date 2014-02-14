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
public class WorkflowResponse extends XmlEntityObject {

	
	public static final String ITEM_ROOT_ELEMENT_NAME = "response";
	
	private final ISimpleEntityProperty contactName = super
		.createAttributeProperty( "@contact_name" );
	
	private final ISimpleEntityProperty loginName = super
	.createAttributeProperty( "@login_name" );
	
	private final ISimpleEntityProperty value = super
		.createAttributeProperty( "@value" );
	
	private final ISimpleEntityProperty identifier = super
	.createAttributeProperty( "@id" );
	
	@SuppressWarnings("unchecked")
	public static WorkflowResponse wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, WorkflowResponse.class);
	}

	@SuppressWarnings("unchecked")
	public static WorkflowResponse wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, WorkflowResponse.class);
	}
	
	public WorkflowResponse() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public void setContactName(String value) {
		this.contactName.setValue(value);
	}
	
	public String getContactName() {
		return this.contactName.getValue();
	}
	
	public void setId(String value) {
		this.identifier.setValue(value);
	}
	
	public String getId() {
		return this.identifier.getValue();
	}
	
	public void setValue(String value) {
		this.value.setValue(value);
	}
	
	public String getValue() {
		return this.value.getValue();
	}
	
	public void setLoginName(String value) {
		this.loginName.setValue(value);
	}
	
	public String getLoginName() {
		return this.loginName.getValue();
	}
	
	
}

