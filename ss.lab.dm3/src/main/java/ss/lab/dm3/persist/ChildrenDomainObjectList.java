package ss.lab.dm3.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.managed.IManagedCollection;
import ss.lab.dm3.orm.managed.ManagedCollectionController;

/**
 * 
 * Example of use
 * <code>

@Entity
class Category extends DomainObject {
		
	private Category parent;
		
	@OneToMany(mappedBy="parent")
	private final ChildrenDomainObjectList<Category> children = new ChildrenDomainObjectList<Category>();

	@ManyToOne()
	public Category getParent() {
		return this.parent;
	}
		
	public void setParent(Category parent) {
		this.parent = parent;
	}
	
	@Transient
	public ChildrenDomainObjectList<Category> getChildren() {
		return this.children;
	}
			
}
	
Iterate through category children
		 
	Category category = domain.resolve( Category.class, 1L );
	for( Category child : category.getChildren() ) {
		System.out.println( child );
	}

 * </code>
 * 
 * 
 * @author Dmitry Goncharov
 *
 */
public class ChildrenDomainObjectList<T extends DomainObject> implements Collection<T>, IManagedCollection, Iterable<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6359730487377628033L;

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	protected DomainCollectionController controller = null;
	
	private transient List<T> fetched = new ArrayList<T>();
	
	private transient Context context = null;
	
	public ChildrenDomainObjectList() {
		super();
	}
	
	/**
	 * @param itemClazz
	 */
	@Deprecated
	public ChildrenDomainObjectList(DomainObject owner, Class<T> itemClazz) {
		this();		
	}

	/**
	 *  
	 */
	@Deprecated
	public static <E extends DomainObject> ChildrenDomainObjectList<E> create(
			DomainObject owner, Class<E> itemType) {
		return new ChildrenDomainObjectList<E>( owner, itemType );
	}
	
	/**
	 * @return
	 */
	public final boolean isFetched() {
		return this.fetched != null;
	}

	public final Iterator<T> iterator() {
		return getFetched().iterator();
	}
	
	public final int size() {
		return getFetched().size();
	}
	
	private synchronized List<T> getFetched() {
		if ( this.controller != null && 
			 (this.fetched == null || isContextOutOfDate()) ) {			
			final Domain domain = this.controller.getDomain();
			final List<T> fetched = domain.find( getFetchQuery() ).toList();
			setFetched(fetched);
		}
		return this.fetched;
	}

	private void setFetched(final List<T> fetched) {
		final Domain domain = this.controller.getDomain();
		this.fetched = fetched;
		this.context = domain.getContext();
		if (this.log.isDebugEnabled()) {
			this.log.debug( this.controller + " fetches " + this.fetched.size() );
		}
	}

	/**
	 * @return
	 */
	private boolean isContextOutOfDate() {
		return this.context != null && !this.context.isAlive();
	}
		
	/**
	 * @return
	 */
	public final TypedQuery<T> getFetchQuery() {
		return this.controller != null ? this.controller.getFetchQuery() : null;		
	}

	public final boolean contains(T obj) {
		return getFetched().contains( obj );
	}
		
	public boolean add(T obj) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Adding " + obj );
		}
		if ( this.controller != null ) {
			this.controller.bindItemToOwner(obj);
		}
		else {
			addToFetched(obj);
		}
		return true;
	}
	
	public T remove(T obj, boolean deleteObject) {
		if ( obj == null ) {
			return obj;
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug("Removing " + obj );		
		}
		if ( this.controller != null ) {
			this.controller.unbindItemFromOwner(obj);
			if ( deleteObject ) {
				obj.delete();
			}
		}
		else {
			removeFromFetched(obj);
		}
		return obj;
	}
	
	public void clear( boolean deleteObject ) {
		for( T obj : this.toSet() ) {
			remove(obj, deleteObject);
		}
	}
	
	/**
	 * Use remove(T,boolean)
	 */
	@Deprecated
	protected final void unbindAndRemove(T obj) {
		remove(obj, true);
	}
	
	/**
	 * @return
	 */
	public final T findById( Long id ) {
		if ( id == null ) {
			return null;
		}
		for( T item : this ) {
			if ( item.getId().equals( id )  ) {
				return item;
			}
		}
		return null;
	}	

	/**
	 * @return
	 */
	public final Object[] toArray() {
		return getFetched().toArray();
	}
	
	public final Set<T> toSet() {
		Set<T> hashSet = new HashSet<T>();
		hashSet.addAll( getFetched() );
		return hashSet;
	}

	public final void copyTo(IObjectMatcher entityFilter, Collection<T> target) {
		copyTo( this.controller.getItemType(), entityFilter, target);
	}
	
	/**
	 * @param entityFilter
	 */
	public final <E extends DomainObject> void copyTo(Class<E> itemClazz, IObjectMatcher entityFilter, Collection<E> target) {
		for( T item : this ) {
			if ( itemClazz.isInstance( item ) ) {
				E typedItem = itemClazz.cast( item );
				if ( entityFilter == null || entityFilter.match( typedItem ) ) {
					target.add( typedItem );
				}
			}
		}
	}
		
	public final ManagedCollectionController getController() {
		return this.controller;
	}
	
	public final void setUpController(MappedObject owner) {
		if ( this.controller != null ) {
			throw new IllegalStateException( "Controller already injected to " + this );
		}
		this.controller = new DomainCollectionController( (DomainObject) owner );
		if ( this.context != null || this.fetched == null || this.fetched.size() > 0 ) {
			throw new IllegalStateException( "Invalid object state " + this );
		}
		this.fetched = null;
		this.context = null;
	}

	@Override
	public final String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "controller", this.controller );
		tsb.append( "context", this.context );
		tsb.append( "items.size", isFetched() ? String.valueOf( this.fetched.size() ) : "not loaded" );
		return tsb.toString();
	}

	/**
	 * 
	 */
	private final void resetFetchedItems() {
		this.fetched = null;
		this.context = null;		
	}

	/**
	 * 
	 */
	private final void addToFetched(T object ) {
		this.fetched.add( object );
		if ( this.context != null && !isContextOutOfDate() ) {
			this.context.addByOrm(object);
		}
	}	
	
	public Domain getDomain() {
		return this.controller.getDomain();
	}
	/**
	 * 
	 */
	private final void removeFromFetched(T object ) {
		this.fetched.remove( object );
		if ( this.context != null && !isContextOutOfDate() ) {
			this.context.removeByOrm(object);
		}
	}
	
	public class DomainCollectionController extends ManagedCollectionController {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8155400648590392751L;

		/**
		 * @param owner
		 */
		public DomainCollectionController(DomainObject owner) {
			super(owner);
		}

		/**
		 * @return
		 */
		public TypedQuery<T> getFetchQuery() {
			checkInitialized();		
			final Long id = getOwner().getId();
			if ( id == null ) {
				throw new NullPointerException( "Invalid owner id. Owner " + getOwner()  );
			}
			if (this.log.isDebugEnabled()) {
				this.log.debug( getOwner() + " fetch " + getItemType().getSimpleName() + " by " + getMappedByName() + " = " +  id );
			}
			return QueryHelper.eq( getItemType(), getMappedByName(), id );
		}

		/**
		 * @return
		 */
		public Domain getDomain() {
			return ((DomainObject)this.owner).ctrl.getDomain();
		}

		@Override
		public void resetFetchedItems() {
			super.resetFetchedItems();
			ChildrenDomainObjectList.this.resetFetchedItems();
		}
		
		@Override
		public synchronized void addByOrm(MappedObject item) {
			super.addByOrm(item);
			if ( isFetched() ) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Add by orm " + item + " from " + this );
				}
				final T castedItem = getItemType().cast(item);
				ChildrenDomainObjectList.this.addToFetched( castedItem );
			}
		}		

		@SuppressWarnings("unchecked")
		@Override
		public void setUpByOrm(List<? extends MappedObject> items) {
			super.setUpByOrm(items);
			setFetched((List<T>)items);
		}

		@Override
		public DomainObject getOwner() {
			return (DomainObject) super.getOwner();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<T> getItemType() {
			return (Class<T>) super.getItemType();
		}

		@Override
		public synchronized void removeByOrm(MappedObject item) {
			super.removeByOrm(item);
			if ( isFetched() ) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Remove by orm " + item + " from " + this );
				}
				final Class<T> itemType = getItemType();
				if ( itemType.isInstance( item ) ) {
					final T castedItem = itemType.cast(item);
					ChildrenDomainObjectList.this.removeFromFetched( castedItem );
				}
				else {
					throw new IllegalArgumentException( "Can't add " + item + " to " + this );
				}
			}
		}
	}

	//TODG review all method that returs value 
	
	public boolean addAll(Collection<? extends T> c) {
		for( T item : c ) {
			add( item );
		}
		return true;
	}

	public void clear() {
		for( T item : new ArrayList<T>( getFetched() ) ) {
			remove(item);
		}
	}

	public boolean contains(Object o) {
		List<T> fetched = getFetched();
		return fetched.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		List<T> fetched = getFetched();
		return fetched.containsAll(c);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		remove( (T) o, true );
		return true;
	}

	public boolean removeAll(Collection<?> c) {
		for( Object item : c ) {
			remove( item );
		}
		return true;
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public <E> E[] toArray(E[] a) {
		return getFetched().toArray(a);
	}
	
}