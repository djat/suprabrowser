package ss.framework.entities.xmlentities;
import org.dom4j.Element;


public final class XmlElementDataProvider extends AbstractXmlElementDataProvider {

	private IXmlElementDataProvider parent;	
	
	private String elementName;
	
	/**
	 * @param parent
	 * @param name
	 */
	public XmlElementDataProvider(IXmlElementDataProvider parent, String name) {
		super();
		this.parent = parent;
		this.elementName = name;
	}

	/* (non-Javadoc)
	 * @see xmlentities.IXmlElementDataProvider#isExist()
	 */
	public boolean isExist() {
		if ( this.parent.isExist() ) {
			return this.parent.requireElement().element( this.elementName ) != null;
		}
		else {		
			return false;		
		}
	}

	/* (non-Javadoc)
	 * @see xmlentities.IXmlElementDataProvider#getElement()
	 */
	public Element getElement() {
		if ( isExist() ) {
			return this.parent.requireElement().element( this.elementName );
		}
		else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see xmlentities.IXmlElementDataProvider#getOrCreateElement()
	 */
	public Element getOrCreateElement() {
		Element element = getElement();
		if ( element == null ) {
			Element parentElement = this.parent.getOrCreateElement();
			element = parentElement.addElement( this.elementName );
		}
		return element;
	}

	/* (non-Javadoc)
	 * @see xmlentities.IXmlElementDataProvider#requireElement()
	 */
	public Element requireElement() throws ElementNotFoundException {
		Element element = getElement();
		if ( element == null ) {
			throw new ElementNotFoundException( String.format( "Cannot find required element", new Object[] { this } ) );
		}
		return element;
	}

	/**
	 * @return the name
	 */
	final String getElementName() {
		return this.elementName;
	}
	
	
}