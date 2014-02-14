package ss.framework.entities.xmlentities;

import org.dom4j.Element;

public interface IXmlEntityClassResolver {

	/**
	 * Returns true if entity can created on element
	 */
	boolean match( Element element );

	/**
	 * Returns class of entity that resolver creates
	 */
	Class<XmlEntityObject> getEntityClass();
	
}
