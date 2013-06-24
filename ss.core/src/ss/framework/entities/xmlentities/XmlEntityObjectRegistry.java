package ss.framework.entities.xmlentities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import org.dom4j.Element;
import ss.common.ArgumentNullPointerException;

public class XmlEntityObjectRegistry<T extends XmlEntityObject>  {

	private static final int garbageDeadObjectsThreashold = 5;

	private final Map<Element, T> elementToObjectMap = new Hashtable<Element, T>();
	
	private IXmlEntityObjectFactory<T> objectFactory;
	
	private int isertionCount = 0;
	
	private IXmlElementDataProvider parentElementProvider;
	
	/**
	 * @param objectFactory
	 */
	public void init(IXmlEntityObjectFactory<T> objectFactory, IXmlElementDataProvider parentElementProvider ) {
		this.objectFactory = objectFactory;
		this.parentElementProvider = parentElementProvider;
		this.isertionCount = 0;
		this.elementToObjectMap.clear();
	}
	
	/**
	 * Returns object that represent element
	 * @param element
	 * @return
	 */
	public T getOrCreateObject(Element element) {
		if ( element == null ) {
			throw new ArgumentNullPointerException( "element" );
		}
		T object = this.elementToObjectMap.get(element);
		if ( object == null ) {
			object = this.objectFactory.createBlankObject(element);
			bindObject(element, object);
			++ this.isertionCount; 
		}
		if ( this.isertionCount > garbageDeadObjectsThreashold ) {
			this.isertionCount = 0;
			removeDeadObjects();
		}
		return object;		
	}

	private void removeDeadObjects() {
		if ( !this.parentElementProvider.isExist() ) {
			this.elementToObjectMap.clear();
		}
		else {
			final Element parent = this.parentElementProvider.requireElement(); 
			final ArrayList<Element> keysCopy = new ArrayList<Element>();
			keysCopy.addAll( this.elementToObjectMap.keySet() );
			for( Element element : keysCopy ) {
				if ( element.getParent() != parent ) {
					this.elementToObjectMap.remove(element);
				}
			}
		}
	}

	/**
	 * Returns object that represent element
	 * @param element
	 * @return
	 */
	public void bindObject(Element element, T object ) {
		if ( element == null ) {
			throw new ArgumentNullPointerException( "element" );
		}
		if ( object == null ) {
			throw new ArgumentNullPointerException( "object" );
		}
		object.bindTo( new ListItemRootXmlElementDataProvider( element, this.parentElementProvider ) );
		this.elementToObjectMap.put(element, object);				
	}
	
	public void unbindObject(T object ) {
		if ( object == null ) {
			return;
		}
		final IXmlElementDataProvider objectDataProvider = object.getBindTarget();
		if ( objectDataProvider instanceof ListItemRootXmlElementDataProvider ) {
			ListItemRootXmlElementDataProvider inListDataProvider = (ListItemRootXmlElementDataProvider) objectDataProvider;
			inListDataProvider.unbind( this.parentElementProvider );
		}
	}
}
