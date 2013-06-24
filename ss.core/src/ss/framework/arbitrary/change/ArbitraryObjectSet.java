package ss.framework.arbitrary.change;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ArbitraryObjectSet<T> implements Serializable {

	@SuppressWarnings("unused")
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 4236388626252829624L;
	
	private final Set<T> items = new HashSet<T>();
	
	public void add( T item ) {
		if ( item == null ) {
			throw new NullPointerException( "item" );
		}
		if ( contains( item ) ) {
			// TODG solve what to do with duplicates
			// throw new IllegalArgumentException( "Already contains " + item );
			this.log.warn( "Already contains " + item );
		}
		this.items.add( item );
	}
	
	public void add(Iterable<T> items) {
		for( T item : items ) {
			add( item );
		}
	}
	
	public <F> void add(F item, IObjectConverter<T, F> converter) {
		if ( item == null ) {
			throw new NullPointerException( "item" );
		}
		if (converter == null ) {
			throw new NullPointerException( "converter" );
		}
		add( converter.convert( item ) );
	}
	
	public <F> void add(Iterable<F> items, IObjectConverter<T, F> converter) {
		add( items, converter, null );
	}
	
	public <F> void add(Iterable<F> items, IObjectConverter<T, F> converter, BackwardConvert<F,T> backwardConvert ) {
		if ( items == null ) {
			throw new NullPointerException( "items" );
		}
		if (converter == null ) {
			throw new NullPointerException( "converter" );
		}
		for( F item : items ) {
			final T result = converter.convert( item );
			add( result );
			if ( backwardConvert != null ) {
				backwardConvert.add( result, item );
			}
		}		
	}
	
	/**
	 * @param item
	 * @return
	 */
	public boolean contains(T item) {
		return item != null ? this.items.contains( item ) : false;
	}
	
	public void remove(T item) {
		if ( item != null ) {
			this.items.remove( item );
		}
	}
	
	public int size() {
		return this.items.size();
	}

	public void foreach(IObjectHandler<T> objectHandler) {
		if ( objectHandler == null ) {
			throw new NullPointerException( "objectHandler" );
		}
		for( T item : this.items ) {
			objectHandler.handle( item );
		}
	}
			
	/**
	 * @param objectHandler
	 * @param idToManagedSphere
	 */
	public <R> void foreach(IObjectHandler<R> objectHandler,
			IObjectConverter<R,T> converter) {
		if ( objectHandler == null ) {
			throw new NullPointerException( "objectHandler" );
		}
		if ( converter == null ) {
			throw new NullPointerException( "converter" );
		}
		for( T item : this.items ) {
			objectHandler.handle( converter.convert( item ) );
		}
	}
	
	public <R> Set<R> toSet(IObjectConverter<R,T> converter) {
		if ( converter == null ) {
			throw new NullPointerException( "converter" );
		}
		final Set<R> result = new HashSet<R>();
		for( T item : this.items ) {
			result.add( converter.convert( item ) );
		}
		return result;
	}
	
	public Set<T> toSet() {
		return new HashSet<T>( this.items );
	}

	/**
	 * @return
	 */
	protected Set<?> getItems() {
		return this.items;
	}
	
}
