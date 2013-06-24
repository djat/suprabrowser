/**
 * 
 */
package ss.domainmodel.systemstatemessage;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class SystemPropertyObject extends XmlEntityObject {

public static final String ITEM_ROOT_ELEMENT_NAME = "property";
	
	private final ISimpleEntityProperty name = super
	.createAttributeProperty( "@name" );

	private final ISimpleEntityProperty value = super
	.createAttributeProperty( "@value" );
	
	@SuppressWarnings("unchecked")
	public static SystemPropertyObject wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SystemPropertyObject.class);
	}

	@SuppressWarnings("unchecked")
	public static SystemPropertyObject wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SystemPropertyObject.class);
	}
	
	public SystemPropertyObject() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public void setName(String filename) {
		this.name.setValue(filename);
	}
	
	public String getName() {
		return this.name.getValue();
	}
	
	public void setValue(String value) {
		this.value.setValue(value);
	}
	
	public String getValue() {
		return this.value.getValue();
	}
}
