/**
 * 
 */
package ss.framework.entities.xmlentities;

import org.dom4j.Element;

/**
 *
 */
class ListItemRootXmlElementDataProvider extends RootXmlElementDataProvider {

	private IXmlElementDataProvider parentElementProvider;
	
	/**
	 * @param element
	 * @param parentElementProvider 
	 */
	public ListItemRootXmlElementDataProvider(Element element, IXmlElementDataProvider parentElementProvider) {
		super( element );		
		this.parentElementProvider = parentElementProvider;
	}

	/**
	 * @param parentElementProvider
	 */
	public void unbind(IXmlElementDataProvider parentElementProvider) {
		if ( this.parentElementProvider == parentElementProvider ) {
			this.parentElementProvider = null;
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.AbstractXmlElementDataProvider#cleanup(org.dom4j.Element)
	 */
	@Override
	protected void cleanup(Element element) {
		if ( !isBinded() ){
			super.cleanup(element);
		}
	}

	/**
	 * @return
	 */
	private boolean isBinded() {
		return this.parentElementProvider != null;
	}


	
}
