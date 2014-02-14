/**
 * 
 */
package ss.domainmodel.configuration;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 * 
 */
public class EmailDomain extends XmlEntityObject {
	public static final String ROOT_ELEMENT_NAME = "domain";
	
	private final ISimpleEntityProperty name = super
			.createAttributeProperty("@name");

	public EmailDomain() {
		super();
	}

	/**
	 * Gets the record type
	 * 
	 * @return
	 */
	public final String getDomain() {
		return this.name.getValue();
	}

	/**
	 * Sets the record type
	 */
	public final void setDomain(String value) {
		this.name.setValue(value);
	}
}
