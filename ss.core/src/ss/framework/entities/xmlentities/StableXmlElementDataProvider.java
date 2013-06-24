package ss.framework.entities.xmlentities;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import ss.common.XmlDocumentUtils;


public class StableXmlElementDataProvider implements IXmlElementDataProvider {

    private IXmlElementDataProvider implementation;

    private String desiredRootElementName;

    /**
     * @param implementation
     */
    public StableXmlElementDataProvider(String desiredRootElementName ) {
        super();
        this.desiredRootElementName = desiredRootElementName;
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#getElement()
      */
    public Element getElement() {
        return getOrCreateImplementation().getElement();
    }


    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#getOrCreateElement()
      */
    public Element getOrCreateElement() {
        return getOrCreateImplementation().getOrCreateElement();
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#isExist()
      */
    public boolean isExist() {
        return getOrCreateImplementation().isExist();
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#requireElement()
      */
    public Element requireElement() throws ElementNotFoundException {
        return getOrCreateImplementation().requireElement();
    }

    /**
     * @return the implementation
     */
    public IXmlElementDataProvider getImplementation() {
        return this.implementation;
    }

    /**
     * @param implementation the implementation to set
     */
    public void setImplementation(IXmlElementDataProvider implementation) {
        this.implementation = implementation;
    }

    /**
     * Returns data provider. If implementation is null that it will be instanted.
     */
    private IXmlElementDataProvider getOrCreateImplementation() {
        if ( this.implementation == null ) {
            final DocumentFactory documentFactory = DocumentFactory.getInstance();
            Document document = documentFactory.createDocument( documentFactory.createElement( this.desiredRootElementName ) );
            this.implementation = new RootXmlElementDataProvider( document.getRootElement() );
        }
        return this.implementation;
    }

	/**
	 * @return
	 */
	public Document getDataCopy() {
		if ( isExist() ) {
			return DocumentHelper.createDocument( (Element) requireElement().clone() );
		}
		else {
			return DocumentHelper.createDocument( DocumentHelper.createElement( this.desiredRootElementName ) );
		}
	}

	@Override
	public String toString() {
		if ( isExist() ) {
			return XmlDocumentUtils.toPrettyString( requireElement() );
		}
		else {
			return "[unbinded]";
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlElementDataProvider#cleanup(org.dom4j.Element)
	 */
	public void cleanup() {
		final IXmlElementDataProvider implementation = getImplementation();
		if ( implementation != null ) {
			implementation.cleanup();	
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlElementDataProvider#remove()
	 */
	public void removeAllMatched() {
		final IXmlElementDataProvider implementation = getImplementation();
		if ( implementation != null ) {
			implementation.removeAllMatched();	
		}
	}
   
	
	
}

