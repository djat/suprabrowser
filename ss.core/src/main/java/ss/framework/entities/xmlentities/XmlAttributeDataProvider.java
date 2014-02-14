package ss.framework.entities.xmlentities;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class XmlAttributeDataProvider {

	private IXmlElementDataProvider parent;
	
	private String name;
	
	/**
	 * @param parent
	 * @param attributeName
	 */
	public XmlAttributeDataProvider(IXmlElementDataProvider parent, String attributeName) {
		super();
		this.parent = parent;
		this.name = attributeName;
	}

	/**
	 * Returns value
	 * @return
	 */
	public final String getValue() {
		return this.parent.isExist() ? this.parent.requireElement().attributeValue( this.name ) : null; 
	}
	
	/**
	 * Sets value
	 * @param value
	 */
	public final void setValue( String value ) {
		if ( value == null && this.parent.isExist() ) {
			final Element element = this.parent.requireElement();
			final Attribute attribute = element.attribute( this.name );
			if ( attribute != null ) {
				element.remove( attribute );
			}
			this.parent.cleanup();			
		}
		else {
			Element element = this.parent.getOrCreateElement();
			element.addAttribute(this.name, value);
		}		
	}

	/**
	 * Returns true if has value
	 */
	public boolean hasValue() {
		return this.parent.isExist() && this.parent.requireElement().attribute( this.name ) != null;
	}
	
	public void removeAllMatched() {
		this.parent.removeAllMatched();
	}
	
}
