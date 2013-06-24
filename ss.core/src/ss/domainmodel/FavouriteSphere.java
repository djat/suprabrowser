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
public class FavouriteSphere extends XmlEntityObject {

	
	public static final String ITEM_ROOT_ELEMENT_NAME = "sphere";
	
	private final ISimpleEntityProperty displayName = super
		.createAttributeProperty( "@display_name" );
	
	private final ISimpleEntityProperty systemName = super
		.createAttributeProperty( "@system_name" );
	
	@SuppressWarnings("unchecked")
	public static FavouriteSphere wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, FavouriteSphere.class);
	}

	@SuppressWarnings("unchecked")
	public static FavouriteSphere wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, FavouriteSphere.class);
	}
	
	public FavouriteSphere() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public String getDisplayName() {
		return this.displayName.getValue();
	}
	
	public void setDisplayName(String value) {
		this.displayName.setValue(value);
	}
	
	public String getSystemName() {
		return this.systemName.getValue();
	}
	
	public void setSystemName(String value) {
		this.systemName.setValue(value);
	}
}
