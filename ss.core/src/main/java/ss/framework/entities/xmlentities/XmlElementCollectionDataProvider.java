package ss.framework.entities.xmlentities;

import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class XmlElementCollectionDataProvider implements Iterable<Element> {

    private final static List<Element> emptyList = new ArrayList<Element>();

    private IXmlElementDataProvider parentProvider;

    private String itemElementName;

    public XmlElementCollectionDataProvider(IXmlElementDataProvider parentProvider, String itemElementName) {
    	if ( parentProvider == null ) {
    		throw new NullPointerException( "parentProvider" );
    	}
    	if ( itemElementName == null ) {
    		throw new NullPointerException( "itemElementName" );
    	}
        this.parentProvider= parentProvider;
        this.itemElementName = itemElementName;
    }

    /**
     * @return true if parent for provider is exit
     */
    private boolean isExist() {
        return this.parentProvider.isExist();
    }

    /**
     * @return Gets or create parent element for collection
     */
    private Element getOrCreateParent() {
        return this.parentProvider.getOrCreateElement();
    }
    
    /**
     * @return count of item in collection
     */
    public int getCount() {
        return isExist() ? this.getItems().size() : 0;
    }
    
    /**
     * Insert element copy.  
     * 
     * @param index insertion index. Less than means add to the end
     * @param element not null element that copy will be inserted
     * @return inserted element.
     */
    @SuppressWarnings({"unchecked"})
    public Element insertCopy(int index, Element element ) {
        Element elementCopy = (Element)element.clone();
        if ( !elementCopy.getName().equals( this.itemElementName ) ) {
        	elementCopy.setName( this.itemElementName );
            // throw new UnexpectedElementNameException( this, this.itemElementName, elementName );
        }
        final Element safeParent = getOrCreateParent();
        if ( index < 0 ) {
            safeParent.add( elementCopy );
        }
        else {
        	// TODO: implement
        	throw new RuntimeException( "Sorry. Not yet implemented" ); 
            // safeParent.elements().add(index, elementCopy);
        }
        return elementCopy;
    }

    /**
     * @return Returns index of element in collection or -1 if element is not found.
     */
    public int indexOf( Element element ) {
        return isExist() ? getItems().indexOf( element ) : -1;
    }

    /**
     * Removes element from collection
     * @param element element to remove
     */
    public void remove( Element element ) {
        if ( isExist() ) {
            getOrCreateParent().remove( element );
        }
    }

    /**
     * @param index element index
     * @return Returns element by index
     */
    public Element get( int index ) {
        return isExist() ? getItems().get( index ) : null;
    }

    /**
     * Returns list of element. Empty list if parent for collection is not exist
     * @return
     */
    @SuppressWarnings({"unchecked"})
    private List<Element> getItems() {
        if ( isExist() ) {
            return (List<Element>) this.parentProvider.requireElement().elements( this.itemElementName );
        }
        else {
            return emptyList;
        }
    }

    /**
     * @return Returns iterator via collecion
     */
    public Iterator<Element> iterator() {
        return getItems().iterator();
    }

    /**
     * @return parent or null is parent not yer created
     */
    public Element getParent() {
        return this.parentProvider.getElement();
    }

    /**
     * @return parent element provider
     */
	public IXmlElementDataProvider getParentProvider() {
		return this.parentProvider;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.parentProvider != null ? this.parentProvider.toString() : "[not-binded]";
	}
	
	
}
