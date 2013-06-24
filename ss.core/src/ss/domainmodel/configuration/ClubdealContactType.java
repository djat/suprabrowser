/**
 * 
 */
package ss.domainmodel.configuration;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author roman
 *
 */
public class ClubdealContactType extends XmlEntityObject {

	public static final String ROOT_ELEMENT_NAME = "type";

	private final ISimpleEntityProperty name = super
		.createAttributeProperty("@name");
	
	public ClubdealContactType() {
		super(ROOT_ELEMENT_NAME);
	}
	
	@SuppressWarnings("unchecked")
	public static ClubdealContactType wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ClubdealContactType.class);
	}

	@SuppressWarnings("unchecked")
	public static ClubdealContactType wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ClubdealContactType.class);
	}

	public String getName() {
		return this.name.getValue();
	}
	
	public void setName(final String value) {
		this.name.setValue(value);
	}
	
	public static String getDefaultName(){
		return "no type";
	}
}
