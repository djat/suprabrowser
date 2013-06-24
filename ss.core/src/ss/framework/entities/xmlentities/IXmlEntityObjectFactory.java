package ss.framework.entities.xmlentities;

import org.dom4j.Element;

public interface IXmlEntityObjectFactory<E extends XmlEntityObject> {

	/**
	 * Returns object model by part of xml document, that represented object model data
	 */
	E createBlankObject( Element element ) throws CannotCreateEntityException;
}
