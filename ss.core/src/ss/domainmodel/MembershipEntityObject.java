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
public class MembershipEntityObject extends XmlEntityObject {
	
	private final ISimpleEntityProperty status = super
	.createAttributeProperty( "status/@value" );
	
	private final ISimpleEntityProperty contact_name = super
	.createAttributeProperty( "contact_name/@value" );
	
	public MembershipEntityObject() {
		super("membership");
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MembershipEntityObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, MembershipEntityObject.class);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MembershipEntityObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, MembershipEntityObject.class);
	}
	
	public String getStatus() {
		return this.status.getValue();
	}
	
	public void setStatus(String value) {
		this.status.setValue(value);
	}
	
	public String getContactName() {
		return this.contact_name.getValue();
	}
	
	public void setConatactName(String value) {
		this.contact_name.setValue(value);
	}

}
