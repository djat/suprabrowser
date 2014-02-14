package ss.domainmodel;

import org.dom4j.Element;

import ss.framework.entities.xmlentities.IXmlEntityClassResolver;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class XmlEntityClassResolver implements IXmlEntityClassResolver {

	private String elementName;
	
	private Class<XmlEntityObject> entityClass;
		
	/**
	 * @param elementName
	 * @param entityClass
	 */
	public XmlEntityClassResolver(String elementName, Class<XmlEntityObject> entityClass) {
		super();
		if ( elementName == null ) {
			throw new NullPointerException( "elementName is null" );
		}
		if ( entityClass == null ) {
			throw new NullPointerException( "entityClass is null" );
		}
		this.elementName = elementName;
		this.entityClass = entityClass;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlEntityClassResolver#getEntityClass()
	 */
	public Class<XmlEntityObject> getEntityClass() {
		return this.entityClass;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlEntityClassResolver#match(org.dom4j.Element)
	 */
	public boolean match(Element element) {
		return element.getName().equals( this.elementName );
	}

	
}
