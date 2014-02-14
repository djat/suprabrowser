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
public class RoleItem extends XmlEntityObject {
	
	private final ISimpleEntityProperty value = super
					.createAttributeProperty( "@value" );
	
	private final ISimpleEntityProperty abbr = super
					.createAttributeProperty( "@abbr" );
	
	private final RoleMemberCollection members = super.bindListProperty(new RoleMemberCollection());

	public static final String ITEM_ROOT_ELEMENT_NAME = "role";
	
	public RoleItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	public static RoleItem wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, RoleItem.class);
	}

	@SuppressWarnings("unchecked")
	public static RoleItem wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, RoleItem.class);
	}
	
	public void setValue(String value) {
		this.value.setValue(value);
	}
	
	public String getValue() {
		return this.value.getValue();
	}
	
	public void setAbbr(String value) {
		this.abbr.setValue(value);
	}
	
	public String getAbbr() {
		return this.abbr.getValue();
	}
	
	public int getMemberCount() {
		return this.members.getCount();
	}
	
	public RoleMemberCollection getMemberCollection() {
		return this.members;
	}
}
