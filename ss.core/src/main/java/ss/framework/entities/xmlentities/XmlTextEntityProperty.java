package ss.framework.entities.xmlentities;

public class XmlTextEntityProperty extends AbstractSimpleEntityProperty  {

	private final IXmlElementDataProvider xmlElementDataProvider;
	
	
	/**
	 * @param xmlElementDataProvider
	 */
	public XmlTextEntityProperty(final IXmlElementDataProvider xmlElementDataProvider) {
		super();
		this.xmlElementDataProvider = xmlElementDataProvider;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.AbstractSimpleEntityProperty#getValue()
	 */
	@Override
	public String getValue() {
		final String value = this.xmlElementDataProvider.isExist() ? this.xmlElementDataProvider.requireElement().getText() : null;
		return value != null ? value.trim() : null;
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.AbstractSimpleEntityProperty#isValueDefined()
	 */
	@Override
	public boolean isValueDefined() {
		return this.xmlElementDataProvider.isExist(); 
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.AbstractSimpleEntityProperty#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.xmlElementDataProvider.getOrCreateElement().setText( value != null ? value : "" );	
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#removeValue()
	 */
	public void removeAllMatched() {
		this.xmlElementDataProvider.removeAllMatched();		
	}

	

}
