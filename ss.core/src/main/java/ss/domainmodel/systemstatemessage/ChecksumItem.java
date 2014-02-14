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
public class ChecksumItem extends XmlEntityObject {

	public static final String ITEM_ROOT_ELEMENT_NAME = "checksum";
	
	private final ISimpleEntityProperty filename = super
	.createAttributeProperty( "@filename" );

	private final ISimpleEntityProperty value = super
	.createAttributeProperty( "@value" );
	
	@SuppressWarnings("unchecked")
	public static ChecksumItem wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ChecksumItem.class);
	}

	@SuppressWarnings("unchecked")
	public static ChecksumItem wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ChecksumItem.class);
	}
	
	public ChecksumItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public void setFilename(String filename) {
		this.filename.setValue(filename);
	}
	
	public String getFilename() {
		return this.filename.getValue();
	}
	
	public void setValue(String value) {
		this.value.setValue(value);
	}
	
	public String getValue() {
		return this.value.getValue();
	}
}
