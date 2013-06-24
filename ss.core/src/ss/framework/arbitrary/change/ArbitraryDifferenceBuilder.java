package ss.framework.arbitrary.change;

import java.util.Set;

public class ArbitraryDifferenceBuilder<T> {

	private final ArbitraryObjectSet<T> from = new ArbitraryObjectSet<T>();
	
	private final ArbitraryObjectSet<T> to = new ArbitraryObjectSet<T>();

	public ArbitraryObjectSet<T> getFrom() {
		return this.from;
	}

	public ArbitraryObjectSet<T> getTo() {
		return this.to;
	}

	/**
	 * 
	 */
	public ArbitraryChangeSet<T> getDifference() {
		final Set<T> added = this.to.toSet();
		added.removeAll( this.from.getItems() );
		final Set<T> removed = this.from.toSet();
		removed.removeAll( this.to.getItems() );
		return ArbitraryChangeSet.createByAddedRemoved( added, removed );
	}

	public <F> ArbitraryChangeSet<F> getDifference(IObjectConverter<F, T> converter) {
		if ( converter == null ) {
			throw new NullPointerException( "converter" );
		}
		ArbitraryChangeSet<T> originalDifference = getDifference();
		return originalDifference.convert( converter );		
	}
	
}

