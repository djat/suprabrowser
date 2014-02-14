package ss.framework.entities.xmlentities;
import org.dom4j.Element;
import ss.common.ArgumentNullPointerException;


public class RootXmlElementDataProvider extends AbstractXmlElementDataProvider {

    private Element element;

    /**
     * @param element
     */
    public RootXmlElementDataProvider(Element element) {
        super();
        if ( element == null ) {
            throw new ArgumentNullPointerException( "element" );
        }
        this.element = element;
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#getElement()
      */
    public final Element getElement() {
        return this.element;
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#getOrCreateElement()
      */
    public final Element getOrCreateElement() {
        return requireElement();
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#isExist()
      */
    public final boolean isExist() {
        return true;
    }

    /* (non-Javadoc)
      * @see xmlentities.IXmlElementDataProvider#requireElement()
      */
    public final Element requireElement() throws ElementNotFoundException {
        Element element = getElement();
        if ( element == null ) {
            throw new ElementNotFoundException( "Document has not root element." );
        }
        return element;
    }

}

