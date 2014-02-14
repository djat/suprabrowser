package ss.graph;

import java.util.ArrayList;
import java.util.List;

import ss.common.ArgumentNullPointerException;

public abstract class AbstractGraphItemLinks {

	private final IGraphItem itemOwner;
	
	private final List<IGraphItem> links = new ArrayList<IGraphItem>();
	
	
	/**
	 * @param itemOwner
	 */
	public AbstractGraphItemLinks(final IGraphItem itemOwner) {
		super();
		this.itemOwner = itemOwner;
	}

	/**
	 * @param thirgId
	 * @return
	 */
	public IGraphItem get(IItemIdentity itemId) {
		if ( itemId == null ) {
			throw new ArgumentNullPointerException( "itemId" );
		}
		for( IGraphItem item : this.links ) {
			if ( item.getIdentity().equals( itemId ) ) {
				return item;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public int size() {
		return this.links.size();
	}

	/**
	 * @param other
	 */
	public void link(IGraphItem other) {
		if ( other == null ) {
			throw new ArgumentNullPointerException( "other" );
		}
		if ( contains( other.getIdentity() ) ) {
			return;
		}
		this.links.add( other );
		crossLink( other );
	}

	/**
	 * @param other
	 */
	protected abstract void crossLink(IGraphItem other);
	
	/**
	 * @param identity
	 * @return
	 */
	private boolean contains(IItemIdentity identity) {
		return identity != null ? get( identity ) != null : false;
	}

	/**
	 * @return the itemOwner
	 */
	public IGraphItem getItemOwner() {
		return this.itemOwner;
	}

	
	
	
}
