package ss.framework.entities.xmlentities;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;
import ss.common.StringUtils;
import ss.common.UnexpectedRuntimeException;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.IEntityObject;
import ss.framework.entities.ISimpleEntityProperty;
import ss.global.SSLogger;

public class XmlEntityObject implements IEntityObject, Serializable {

	@SuppressWarnings("unused")
	private Logger logger = SSLogger.getLogger(this.getClass());

	private StableXmlElementDataProvider stableDataProvider;	
	
	private static final long serialVersionUID = 290818558104945682L;
	
	/**
	 * 
	 */
	public XmlEntityObject() {
		this(null);
	}

	/**
	 * @param desiredRootElementName
	 */
	public XmlEntityObject(String desiredRootElementName) {
		super();
		if (desiredRootElementName == null || desiredRootElementName.equals("")) {
			desiredRootElementName = getClass().getName();
		}
		this.stableDataProvider = new StableXmlElementDataProvider(
				desiredRootElementName);
	}
	/**
	 * Creates text property by xpath.
	 * @param path xpath to parent of text.   
	 * Examples  
	 * /element <root><element>property value</element></root>
	 * etc.
	 */
	protected final ISimpleEntityProperty createTextProperty( String path ) {
		IXmlElementDataProvider elementProvider = this.stableDataProvider;
		for(String pathPart : XmlEntityPathParser.parsePath(path, false ) ) {
			elementProvider = new XmlElementDataProvider( elementProvider, pathPart );
		}
		return new XmlTextEntityProperty( elementProvider );
	}
	
	/**
	 * Creates siple property by xpath.
	 * @param path xpath to xml attribute.   
	 * Examples  
	 * @somename <root somename="value of property"/>
	 * element/@value - <root><element value="value of property"/></root>
	 * element/@somename - <root><element somename="value of property"/></root>
	 * element1/element2/@somename - <root><element1><element2 somename="value of property"/></element1></root>
	 * etc.
	 */
	protected final ISimpleEntityProperty createAttributeProperty(
			final String path ) {
		final String[] elemPath = XmlEntityPathParser.parsePath(path, true );
		final int length = elemPath.length;
		IXmlElementDataProvider elementProvider = this.stableDataProvider;
		for(int i=0; i<length-1; i++) {
			elementProvider = new XmlElementDataProvider( elementProvider, elemPath[i] );
		}
		return new XmlSimpleEntityProperty( new XmlAttributeDataProvider(
				elementProvider, elemPath[length-1] ) );
	}
	
	/**
	 * Creates complex entity property
	 * 
	 * @param name
	 *            name of property in xml tree
	 * @param entityClass
	 *            not null property type.
	 */
	protected final <T extends XmlEntityObject> IComplexEntityProperty<T> createComplexProperty(
			String name, Class<T> entityClass)
			throws UnexpectedRuntimeException {
		try {
			return new XmlComplexEntityProperty<T>(new XmlElementDataProvider(
					this.stableDataProvider, name), entityClass.newInstance());
		} catch (InstantiationException e) {
			throw new UnexpectedRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new UnexpectedRuntimeException(e);
		}
	}

	/**
	 * Creates complex entity property
	 * 
	 * @param listProperty nor null list property
	 * @param listPath list embeding path.
	 * Example "element1", "element2" means that list items will be plased at ./element1/element2/*.
	 * If it empty or null than list items plased under xml entity object root. 
	 */
	protected final <T extends XmlListEntityObject<?>> T bindListProperty(
			T listProperty, String listPath ){
		
		IXmlElementDataProvider elementProvider = this.stableDataProvider;
		if ( listPath != null )
		{
			for( String elementName : XmlEntityPathParser.parsePath( listPath, false ) )
			{
				elementProvider = new XmlElementDataProvider( elementProvider, elementName );
			}
		}
		listProperty.bindTo(elementProvider);
		return listProperty;		
	}
	
	/**
	 * Creates list entity property.
	 */
	protected final <T extends XmlListEntityObject<?>> T bindListProperty( T listProperty )
	throws UnexpectedRuntimeException {
		return bindListProperty(listProperty, (String) null );
	}

	/**
	 * Bind xml entity object to data. This method does not change
	 * stableDataProvider.
	 */
	final void bindTo(IXmlElementDataProvider targetData) {
		if ( targetData == null ) {
            throw new ArgumentNullPointerException( "targetData" );
        }
		this.stableDataProvider.setImplementation(targetData);
	}
	
	/**
	 * 
	 */
	final void unbind() {
		this.stableDataProvider.setImplementation( null );
	}
	
	final IXmlElementDataProvider getBindTarget() {
		return this.stableDataProvider.getImplementation();
	}

	/**
	 * Create entity object
	 */
	protected static <T extends XmlEntityObject> T wrap(Document data)
	throws UnexpectedRuntimeException {
		return XmlEntityUtils.<T> wrap(data);
	}

	/**
	 * Create entity object
	 */
	protected static <T extends XmlEntityObject> T wrap(Element data)
	throws UnexpectedRuntimeException {
		return XmlEntityUtils.<T> wrap(data);
	}

	/**
	 * Create entity object
	 */
	public static <T extends XmlEntityObject> T wrap(Document data,
			Class<T> entityClass) throws UnexpectedRuntimeException {
		return XmlEntityUtils.<T> wrap(data, entityClass);
	}

	/**
	 * Create entity object
	 */
	public static <T extends XmlEntityObject> T wrap(Element data,
			Class<T> entityClass) throws UnexpectedRuntimeException {
		return XmlEntityUtils.<T> wrap(data, entityClass);
	}

	/**
	 * Returns binded xml document or null if entity binded not to root element
	 * or not binded.
	 */
	public final Document getBindedDocument() {
		if (this.stableDataProvider.getImplementation() != null
				&& this.stableDataProvider.getImplementation() instanceof RootXmlElementDataProvider) {
			RootXmlElementDataProvider realElementProvider = (RootXmlElementDataProvider) this.stableDataProvider
			.getImplementation();
			Element bindedElement = realElementProvider.getElement();
			if (bindedElement.isRootElement()) {
				return bindedElement.getDocument();
			}
		}
		return null;
	}
	
	/**
	 * Returns binded xml document or null if entity binded not to root element
	 * or not binded.
	 */
	public final Document getDocumentCopy() {
		return this.stableDataProvider.getDataCopy();
	}

	/**
	 * @return Gets stable data provider
	 */
	protected final IXmlElementDataProvider getStableDataProvider() {
		return this.stableDataProvider;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append( getClass().getName() );
		sb.append( "{" );
		sb.append( StringUtils.getLineSeparator() );
		sb.append( this.stableDataProvider.toString() );
		sb.append( "}" );
		return sb.toString();
	}

	final protected Object writeReplace() throws ObjectStreamException {
		Document doc = getBindedDocument();
		if ( doc == null && this.stableDataProvider.isExist() ) {
			doc = getDocumentCopy();
		}
		return new SerializableXmlEntity( getClass(), doc );		
	}
	
}
