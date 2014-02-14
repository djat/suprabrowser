package ss.framework.entities.xmlentities;


public final class XmlSimpleEntityProperty extends AbstractSimpleEntityProperty {
	
	private final XmlAttributeDataProvider xmlAttributeDataProvider;
	
	/**
	 * @param xmlAttributeDataProvider
	 */
	public XmlSimpleEntityProperty(XmlAttributeDataProvider xmlAttributeDataProvider) {
		super();
		this.xmlAttributeDataProvider = xmlAttributeDataProvider;
	}

	/* (non-Javadoc)
	 * @see entities.ISimpleEntityProperty#getSurname()
	 */
	@Override
	public final String getValue() {
		return this.xmlAttributeDataProvider.getValue();
	}

	/* (non-Javadoc)
	 * @see entities.ISimpleEntityProperty#setSurname(java.lang.String)
	 */
	@Override
	public final void setValue(String value) {
		this.xmlAttributeDataProvider.setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#hasValue()
	 */
	@Override
	public final boolean isValueDefined() {
		return this.xmlAttributeDataProvider.hasValue();
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.ISimpleEntityProperty#removeValue()
	 */
	public void removeAllMatched() {
		this.xmlAttributeDataProvider.removeAllMatched();
	}	

}