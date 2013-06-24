package ss.framework.entities.xmlentities;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import ss.framework.entities.IEntityObject;
import ss.framework.entities.UnexpectedItemClassException;
import ss.global.SSLogger;
import ss.common.ArgumentNullPointerException;

class XmlListEntityObjectImplementation<E extends XmlEntityObject> implements IEntityObject, Iterable<E> {

	@SuppressWarnings("unused")
	private final Logger logger = SSLogger.getLogger( getClass() ); 
	
	private final static int defaultAddIndex = -1;

    private final SimpleListEntityObject<E> items = new SimpleListEntityObject<E>();

    private final Class<E> itemType;
    
    public XmlListEntityObjectImplementation(Class<E> itemType) {
        if (itemType == null) {
            throw new ArgumentNullPointerException("itemType");
        }
        this.itemType = itemType;
    }

    /**
     * Bind collection to elements provider
     * Collection will be cleared first
     *
     * @param elementsProvider not null elements provider
     */
    final void bind(XmlElementCollectionDataProvider elementsProvider) {
        if (elementsProvider == null) {
            throw new ArgumentNullPointerException("elementsProvider");
        }
        this.items.bind(elementsProvider, this.itemType);
    }
    
	private final void standalone( String rootName, String itemName ) {
		final StableXmlElementDataProvider root = new StableXmlElementDataProvider( rootName );
		bind( new XmlElementCollectionDataProvider( root, itemName ) );
	}
	
	public final void standalone() {
		if ( this.items.isBinded() ) {
			throw new IllegalArgumentException( "Already binded " + this );
		}
		standalone( "list", "item" );
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
    protected final int internalAdd(E item) {
        if (item == null) {
            throw new ArgumentNullPointerException("item");
        }
        int index = getIsertIndexForAdd(item);
        internalInsert(index, item);
        return index;
    }
    
    protected final void interalAdd(Iterable<E> items) {
		for( E item : items ){
			internalAdd( item );
		}
	}

    /**
     * Called to perform registry item in collection.
     * Item already in collection.
     */
    protected void registryItem(E item) {
    }

    /**
     * Called to perform unregistry item from collection.
     * Called before item will be removed from collection. 
     */
    protected void unregistryItem(E item) {
    }

    /**
     * Returns insert index for item
     * Negative surname means place item to the end.
     */
    protected int getIsertIndexForAdd(E item) {
        return defaultAddIndex;
    }

    /**
     * Returns count of item in collection
     */
    public final int getCount() {
        return this.items.getCount();  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Returns index of item or -1 if item not found.
     */
    protected final int internalIndexOf(E item) {
        if (item == null) {
            return -1;
        }
        return this.items.indexOf(item);
    }


    /**
     * Intert item to the collection
     *
     * @TODO make this method protected 
     * 
     * @param index from 0 to getCount or negative that means place item to the end (same as getCount).
     * @param item  not null item
     */
    private final void internalInsert(int index, E item) {
        if (item == null) {
            throw new ArgumentNullPointerException( "item" );
        }
        if (this.itemType != item.getClass()) {
        	throw new UnexpectedItemClassException( this, this.itemType, item.getClass() );
        }
        if ( internalContains( item ) ) {
            //	@TODO item already in collection?
        }
        this.items.insert(index, item);
        try {
            registryItem(item);
        }
        catch (RuntimeException registryEx) {
        	this.items.removeAt(index);
            try {
                unregistryItem(item);
            }
            catch (RuntimeException unregistryEx) {
            	this.logger.warn( "Unregistry item after unsuccessful insert item failed" , unregistryEx );
            	this.logger.warn( "Failed registry item exception" , registryEx );
            }
            throw registryEx;
        }
        afterItemInserted(item);
    }

   /**
     * Called after item have been fully added to collection.
     * For notification purpose.
     *
     * @param item that was inserted
     */
    protected void afterItemInserted(E item) {
    }


    /**
     * Remove item from items. Perfrom unregistry before remove.
     *
     * @param item item to remove, null will be simply skiped.
     */
    protected final void internalRemove(E item) {
        if (item == null) {
        	this.logger.warn( String.format( "Trying to remove null item from %s ", this ) );
            return;
        }
        unregistryItem(item);
        this.items.remove(item);
        afterItemRemoved(item);
    }


    /**
     * Called after item have been fully removed from collection.
     * For notification purpose.
     */
    protected void afterItemRemoved(E item) {
    }

    /**
     * Set item to specified place
     *
     * @param index new item
     * @param item
     */
//	 protected E internalSetCopy( int index, E item )
//     {
//		E prevItem = this.items.get( index );
//		if ( prevItem == item )
//		{
//			return item;
//		}
//
//        unregistryItem( prevItem );
//		E result = items.set( index, item );
//        try
//		{
//			registryItem( item );
//		}
//		catch(RuntimeException ex)
//		{
//			items.set( index, prevItem );
//			throw ex;
//		}
//		afterItemRemoved( prevItem );
//		afterItemInserted( item );
//	}


    /**
     * Get item at index
     *
     * @param index from range 0 to getCount()
     * @return item on specified index
     */
    protected final E internalGet(int index) {
        return this.items.get(index);
    }

    /**
     * Clear collection
     */
    protected final void internalClear() {
        if ( getCount() > 0 ) {
            for (E item : createIterableSnapshot() ) {
                internalRemove( item );
            }
        }
    }

    /**
     * @return true if collection contains item.
     * If item is null returns false 
     * @param item can be null
     */
    protected final boolean internalContains(E item) {
        if (item == null) {
            return false;
        }
        return internalIndexOf(item) > 0;
    }

    /**
     * @return iterator via collection copy. For for each cycles with modification.
     */
    public final Iterable<E> createIterableSnapshot() {
        return this.items.createSnapshot();
    }

    

//	//-----------------------------------------------------------------------------
//public override string ToString()
//{
//	StringBuilder sb = new StringBuilder();
//	sb.Append( "Collection size = \"" + Count + "\", " + OperationsProperties.EntityType.FullName + "{\n" );
//	foreach (object item in this )
//	{
//		sb.Append( item.ToString()  + ";\n" );
//	}
//	sb.Append( "}" );
//
//	return sb.ToString();
//}

////-----------------------------------------------------------------------------
///// <summary>
///// Creates a new object that is a copy of the current instance.
///// </summary>
///// <returns>A new object that is a copy of this instance.</returns>
//public override object Clone()
//{
//	ListEntityObject clone = (ListEntityObject) base.Clone();
//	for (int n = 0; n < clone.items.Count; n++)
//	{
//		object item = clone.items[n];
//		if ( item is ICloneable )
//		{
//			clone.items[n] = ((ICloneable)item).Clone();
//		}
//	}
//	return clone;
//}

//	    createListEntityObjectEditor()
//		protected void internalSort();
//		protected void InternalSort( IComparer comparer);
//		public override bool equals(object obj)
//		{
//			if ( obj == null ||
//				!getClass().equals( obj.getClass() ) )
//			{
//				return false;
//			}
//			ListEntityObject other = (ListEntityObject) obj;
//			if ( Count != other.Count )
//			{
//				return false;
//			}
//
//			for (int n = 0; n < Count; n++)
//			{
//				object item = internalGet( n );
//				object otherItem = other.internalGet( n );
//				if ( !ObjectUtils.isEquals( item, otherItem ) )
//				{
//					return false;
//				}
//			}
//			return true;
//		}

    /**
     * Gets an object that can be used to synchronize access to the collection 
     */
    public final Object getSyncRoot() {
        return this.items;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.items.toString();
	}

	/**
	 * @return
	 */
	public Element getListElement() {
		return this.items.getListElement();
	}
	   

}