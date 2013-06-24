package ss.framework.entities.xmlentities;

import java.util.Iterator;

import org.dom4j.Element;

public class SimpleListEntityObjectIterator<E extends XmlEntityObject> implements Iterator<E> {

	private XmlEntityObjectRegistry<E > objectsRegistry;
	
	private Iterator<Element> elementIterator;
	
	/**
	 * @param elements
	 * @param objectsRegistry
	 */
	public SimpleListEntityObjectIterator(Iterator<Element> elementIterator, XmlEntityObjectRegistry<E> objectsRegistry) {
		super();
		this.elementIterator = elementIterator;
		this.objectsRegistry = objectsRegistry;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.elementIterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public E next() {
		return this.objectsRegistry.getOrCreateObject( this.elementIterator.next() );
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		this.elementIterator.remove();
	}
	
	

}
