package ss.framework.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ss.common.ArgumentNullPointerException;

//TODO: full list entity object implementation
public class ListEntityObject<E> implements IEntityObject, Iterable<E> {

	private List<E> items = new ArrayList<E>();

    private Class<E> itemType;

    public ListEntityObject(Class<E> itemType) {
        if ( itemType == null ){
            throw new ArgumentNullPointerException( "itemType" );
        }
        this.itemType = itemType;
    }

    public ListEntityObject() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public final Iterator<E> iterator() {
		return this.items.iterator();
	}

	/**
	 * Add item to the collection
	 * 
	 * @param item not null item
	 */
	protected final void internalAdd(E item) {
		if (item == null) {
			throw new ArgumentNullPointerException("item");
		}
        if ( item.getClass() != this.itemType ) {
            throw new UnexpectedItemClassException( this, this.itemType, item.getClass() );
        }
        registryItem(item);
		this.items.add(item);
	}

	/**
	 * Called to perform registry item in collection
	 */
	protected void registryItem(E item) {
	}

	/**
	 * Returns true if item already in collection. For null always returns false
	 */
	protected final boolean internalContains(E item) {
		if (item == null) {
			return false;
		}
        return this.items.contains(item);
	}

}

