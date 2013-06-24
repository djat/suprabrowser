package ss.framework.entities.xmlentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import ss.common.ArgumentNullPointerException;

public class SimpleListEntityObject<E extends XmlEntityObject> implements
	Iterable<E>,  IXmlEntityObjectFactory<E> {

	private Class<E> itemType = null;

	private XmlElementCollectionDataProvider elements = null;

	private final XmlEntityObjectRegistry<E> objectsRegistry;

	/**
	 * 
	 */
	public SimpleListEntityObject() {
		super();
		this.objectsRegistry = new XmlEntityObjectRegistry<E>();
	}
		
	/**
	 * Bind data to collection Before bind collection will be cleared.
	 * 
	 * @param elementsProvider
	 *            not null elements provider
	 */
	public void bind(XmlElementCollectionDataProvider elementsProvider,
			Class<E> itemType) {
		this.objectsRegistry.init(this, elementsProvider.getParentProvider() );
		this.elements = elementsProvider;
		this.itemType = itemType;
	}

	/**
	 * @return Returns count of items in collection
	 */
	public int getCount() {
		return this.elements.getCount();
	}

	/**
	 * @param item
	 *            item to found
	 * @return index of item or null in item not found
	 */
	public int indexOf(E item) {
		final Element element = getObjectElement(item, false );
		return element != null ? this.elements.indexOf(element) : -1;
	}

	/**
	 * Insert item in specified place
	 * 
	 * @param index
	 *            place of item
	 * @param item
	 *            not null item
	 */
	public void insert(int index, E item) {
		if (item == null) {
            throw new ArgumentNullPointerException( "item" );
        }
		Element originalItemElement = getObjectElement(item, true );
		Element elementInCollection = this.elements.insertCopy(index, originalItemElement);
		this.objectsRegistry.bindObject( elementInCollection, item );
	}

	/**
	 * Remove item from specified place
	 * 
	 * @param index
	 *            index of item
	 */
	public void removeAt(int index) {
		remove(get(index));
	}

	/**
	 * Remove item from specified place
	 * 
	 * @param item
	 *            not null item to remove
	 */
	public void remove(E item) {
		if ( item == null ) {
			return;
		}
		final Element element = getObjectElement(item, false );
		if (element != null) {
			this.elements.remove(element);
		}
		this.objectsRegistry.unbindObject( item );
	}

	/**
	 * Slow operation
	 * 
	 * @param index
	 *            from 0 to getCount()
	 * @return getted item from specified index
	 */
	public E get(int index) {
		return this.objectsRegistry.getOrCreateObject( this.elements.get( index ) );
	}
	
	/**
	 * @param item
	 *            item. null supported
	 * @return Returns element that represent item or null if item is null or
	 *         item have not data element
	 */
	private Element getObjectElement(E item, boolean forseCreate ) {
		if (item == null) {
			return null;
		}
		final IXmlElementDataProvider elementProvider = item.getStableDataProvider();
		return forseCreate ? elementProvider.getOrCreateElement() : elementProvider.getElement();
	}

	/**
	 * 
	 * Slow operation.
	 * 
	 * Returns an iterator over a set of elements of type T.
	 * 
	 * @return an Iterator.
	 */
	public Iterator<E> iterator() {
		return new SimpleListEntityObjectIterator<E>( this.elements.iterator(), this.objectsRegistry );
	}

	/**
	 * (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.IXmlEntityObjectFactory#createBlankObject(org.dom4j.Element)
	 */
	public E createBlankObject(Element element) throws CannotCreateEntityException {
		try {
			return this.itemType.newInstance();
		} catch (InstantiationException e) {
			throw new CannotCreateEntityException(e);
		} catch (IllegalAccessException e) {
			throw new CannotCreateEntityException(e);
		}
	}

	/* (non-Javadoc)
	 * @see ss.framework.entities.xmlentities.ISimpleListEntityObject#createSnapshot()
	 */
	public List<E> createSnapshot() {		
		ArrayList<E> snapshot = new ArrayList<E>();
		for( E object : this ) {
			snapshot.add( object );
		}
		return snapshot;	
	}

	/**
	 * @return
	 */
	public boolean isBinded() {
		return this.elements != null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append( "item-class: " ).append( this.itemType.getSimpleName() );
		sb.append( ", " ).append( this.elements != null ? this.elements.toString() : null );
		return sb.toString();
	}

	/**
	 * @return
	 */
	public Element getListElement() {
		return this.elements != null ? this.elements.getParent() : null;
	}
	
	
	
}
