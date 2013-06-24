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
public class IdItem extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "sphere";

	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("@sphereId");

	private final ISimpleEntityProperty messageId = super
			.createAttributeProperty("@messageId");

	@SuppressWarnings("unchecked")
	public static IdItem wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, IdItem.class);
	}

	@SuppressWarnings("unchecked")
	public static IdItem wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, IdItem.class);
	}

	public IdItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public String getMessageId() {
		return this.messageId.getValue();
	}
	
	public void setMessageId(String value) {
		this.messageId.setValue(value);
	}
	
	public String getSphereId() {
		return this.sphereId.getValue();
	}
	
	public void setSphereId(String value) {
		this.sphereId.setValue(value);
	}
}
