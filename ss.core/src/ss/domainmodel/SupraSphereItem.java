package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SupraSphereItem extends XmlEntityObject {

	private final ISimpleEntityProperty type = super
				.createAttributeProperty( "type/@value" );

	
	public SupraSphereItem(String desiredRootElementName) {
		super(desiredRootElementName);
	}
	
	/**
	 * Gets the record type
	 * 
	 * @return
	 */
	public final String getType() {
		return this.type.getValue();
	}

	/**
	 * Sets the record type
	 */
	public final void setType(String value) {
		this.type.setValue(value);
	}
	

	/**
	 * 
	 */
	public boolean hasValidType() {
		return getType() != null;
	}
	
}
