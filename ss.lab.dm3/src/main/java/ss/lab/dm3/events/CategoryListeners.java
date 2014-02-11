package ss.lab.dm3.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dmitry Goncharov
 */
public class CategoryListeners<T extends EventListener> implements Iterable<T>{

	private final Category<T> category;
	
	private final List<T> items = new ArrayList<T>();
	
	/**
	 * @param category
	 */
	public CategoryListeners(Category<T> category) {
		super();
		this.category = category;
	}

	public Category<T> getCategory() {
		return this.category;
	}
	
	public void add( T listener ) {
		this.category.checkEventListener(listener);
		if ( !this.items.contains( listener ) ) {
			this.items.add( listener );
		}
	}
	
	public void remove( T listener ) {
		this.items.remove( listener );
	}

	/**
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return this.items.iterator();
	}
	
	

}
