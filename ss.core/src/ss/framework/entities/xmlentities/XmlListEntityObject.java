package ss.framework.entities.xmlentities;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;
import ss.common.XmlDocumentUtils;

public class XmlListEntityObject<E extends XmlEntityObject> extends XmlListEntityObjectImplementation<E> {

    private final String itemName;

    public XmlListEntityObject(Class<E> itemType, String itemName ) {
        super(itemType);
        if ( itemName == null ) {
            throw new ArgumentNullPointerException( "itemName" );
        }
        this.itemName = itemName;
    }


    /**
     * Bind collection to items parent element provider
     * Collection will be cleared first
     *
     * @param parentProvider not null items parent element provider
     */
    final void bindTo(IXmlElementDataProvider parentProvider) {
        if (parentProvider== null) {
            throw new ArgumentNullPointerException("parentProvider");
        }
        super.bind( new XmlElementCollectionDataProvider( parentProvider, this.itemName ) );
    }

    public final E findFirst( IXmlEntityObjectFindCondition<E> findCondition ) {
    	for( E item : this ) {
    		if ( findCondition.macth( item ) ) {
    			return item;
    		}
    	}
    	return null;    	
    }
    
	public final void bindTo(Element rootElement) {
		bindTo( new RootXmlElementDataProvider( rootElement ) );
	}
	
	public final void bindTo(Document document) {
		bindTo( document.getRootElement() );
	}
	
	public String toXml() {
		final Element listElement = getListElement();
		if ( listElement != null ) {
			return XmlDocumentUtils.toPrettyString( DocumentHelper.createDocument((Element)listElement.clone()) );
		}
		return null;
	}
	
	public void fromXml( String xml) {
		if ( xml != null ) {
			bindTo( XmlDocumentUtils.parse(xml) );
		}
		else {
			standalone();
		}
	}	
	
}
