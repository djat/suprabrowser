package ss.graph;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import ss.common.ArgumentNullPointerException;

public class GraphItemCollection implements Iterable<IGraphItem> {

	private Map<IItemIdentity,IGraphItem> items = new Hashtable<IItemIdentity,IGraphItem>();
	
	
	/**
	 * @param adaptor
	 */
	public void add(IGraphItem item) {
		if ( item == null ) {
			throw new ArgumentNullPointerException( "item" );
		}
		IItemIdentity identity = item.getIdentity();
		IGraphItem existedItem = get( identity );
		if ( existedItem != null ) {
			//#TODO: may be exception instead?
			mergeItems( identity, existedItem, item );
		}
		else {
			this.items.put(identity, item);
		}
	}


	/**
	 * @param identity
	 * @param existedItem
	 * @param newItem
	 */
	private void mergeItems(IItemIdentity identity, IGraphItem existedItem, IGraphItem newItem) {
		//#TODO:D1 think about merge algorithm, 
		// at now simply skip newItem.
	}


	/**
	 * @param identity
	 * @return
	 */
	public IGraphItem get(IItemIdentity identity) {
		return identity != null ? this.items.get( identity ) : null;
	}


	/**
	 * @return
	 */
	public int size() {
		return this.items.size();
	}


	/**
	 * @param identity
	 * @return
	 */
	public boolean containsKey(ItemIdentity identity) {
		return identity != null ? this.items.containsKey(identity) : false;
	}


	/**
	 * @param identity
	 * @return
	 */
	public IGraphItem get(ItemIdentity identity) {
		if ( identity == null ) {
			throw new ArgumentNullPointerException( "identity" );
		}
		return this.items.get(identity);
	}


	/**
	 * 
	 */
	public GraphItemCollection findRootItems() {
		GraphItemCollection roots = new GraphItemCollection();  
		for( IGraphItem item : this ) {
			if ( item.isRoot() ) {
				roots.add( item );
			}
		}
		return roots;		
	}


	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<IGraphItem> iterator() {
		return this.items.values().iterator();
	}



	
	
	
}
