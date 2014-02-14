/**
 * 
 */
package ss.framework.entities.xmlentities;

import org.dom4j.Element;

import ss.common.XmlDocumentUtils;

/**
 *
 */
public abstract class AbstractXmlElementDataProvider implements IXmlElementDataProvider {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractXmlElementDataProvider.class);
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlElementDataProvider#cleanup(org.dom4j.Element)
	 */
	public final void cleanup() {
		Element element = getElement();
		if ( element == null ) {
			return;
		}
		if ( element.isTextOnly() && element.attributeCount() == 0 ) {
			cleanup(element);
		}
	}

	/**
	 * @param element
	 * @return
	 */
	protected void cleanup(Element element) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Detach element " + element );
		}
		element.detach();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Element element = getElement();
		return element != null ? XmlDocumentUtils.toPrettyString(element) : "[null]";
	}

	public void removeAllMatched() {
		Element element = getElement();
		while( element != null ) {
			element.detach();
			element = getElement();
		}		
	}

	
}
