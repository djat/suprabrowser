package ss.framework.entities.xmlentities;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.framework.entities.AbstractEntityProperty;
import ss.framework.entities.IComplexEntityProperty;
import ss.common.ArgumentNullPointerException;

public class XmlComplexEntityProperty<T extends XmlEntityObject> extends AbstractEntityProperty implements IComplexEntityProperty<T>  {

	private final XmlElementDataProvider dataProvider;
	
    private T value;

    /**
     * 
     */
    public XmlComplexEntityProperty(XmlElementDataProvider dataProvider, T value ) {
        super();
        if ( value == null ) {
            throw new ArgumentNullPointerException( "valu" );
        }
        this.dataProvider = dataProvider; 
        bindValue( value);
    }


    /* (non-Javadoc)
      * @see xmlentities.IComplexProperty#getEntity()
      */
    public T getValue() {
        return this.value;
    }

	/* (non-Javadoc)
	 * @see ss.framework.entities.IComplexEntityProperty#setValue(ss.framework.entities.xmlentities.XmlEntityObject)
	 */
	@SuppressWarnings("unchecked")
	public void setValue(T value) {
		if (value == null) {
			throw new ArgumentNullPointerException("value");
		}
		unbindValue();
		final Element newElement = value.getStableDataProvider().getElement();
		if ( newElement != null ) {
			final Element oldElement = this.dataProvider.getOrCreateElement();
			final Element parent = oldElement.getParent();
			final List<Element> parentContent = parent.content();
			parentContent.add( parentContent.indexOf( oldElement ), newElement );
			oldElement.detach();
		}
		bindValue( value );
	}


	/**
	 * @param dataProvider
	 * @param value
	 */
	private void bindValue(T value) {
		this.value = value;
        this.value.bindTo( this.dataProvider );
	}
	/**
	 * 
	 */
	private void unbindValue() {
		this.value.unbind();
		Element element = this.dataProvider.getElement();
		if ( element != null ) {
			Document copy = DocumentHelper.createDocument( (Element) element.clone() );
			this.value.bindTo( new RootXmlElementDataProvider( copy.getRootElement() ) );
		}
		this.value = null;
	}

}

