package ss.framework.arbitrary.change;

import java.io.Serializable;
import java.util.Collections;

public class ArbitraryChangeSet<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4168711474346178017L;

	private final ArbitraryObjectSet<T> created = new ArbitraryObjectSet<T>();
	
	private final ArbitraryObjectSet<T> updated = new ArbitraryObjectSet<T>();
	
	private final ArbitraryObjectSet<T> deleted = new ArbitraryObjectSet<T>();

	
	/**
	 * 
	 */
	public ArbitraryChangeSet() {
		super();
	}

	/**
	 * 
	 */
	public ArbitraryChangeSet(Iterable<T> created, Iterable<T> updated, Iterable<T> deleted) {
		super();
		this.created.add( created );
		this.updated.add( updated );
		this.deleted.add( deleted );
	}

	public ArbitraryObjectSet<T> getCreated() {
		return this.created;
	}
	
	public ArbitraryObjectSet<T> getUpdated() {
		return this.updated;
	}

	public ArbitraryObjectSet<T> getDeleted() {
		return this.deleted;
	}

	public <F> ArbitraryChangeSet<F> convert(IObjectConverter<F, T> converter) {
		if ( converter == null ) {
			throw new NullPointerException( "converter" );
		}
		return new ArbitraryChangeSet<F>( 
			this.created.toSet( converter ),
			this.updated.toSet( converter ),
			this.deleted.toSet( converter )
		);
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArbitraryChangeSet<T> createByAddedRemoved(Iterable<T> added, Iterable<T> removed) {
		return new ArbitraryChangeSet<T>( added, (Iterable)Collections.EMPTY_LIST, removed );
	}
	

}
