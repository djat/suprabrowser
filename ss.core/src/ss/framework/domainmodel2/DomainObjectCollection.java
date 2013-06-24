package ss.framework.domainmodel2;

import ss.common.ArgumentNullPointerException;

public class DomainObjectCollection<D extends DomainObject> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DomainObjectCollection.class);
	
	private final AbstractDomainSpace spaceOwner;
	
	private final Criteria<D> criteria;
	
	private DomainObjectList<D> items = null;
	
	/**
	 * @param spaceOwner
	 */
	public DomainObjectCollection(AbstractDomainSpace spaceOwner, Criteria<D> criteria) {
		super();
		if ( spaceOwner == null ) {
			throw new ArgumentNullPointerException( "spaceOwner" );
		}
		if ( criteria == null ) {
			throw new ArgumentNullPointerException( "criteria" );
		}
		this.spaceOwner = spaceOwner;
		this.criteria = criteria;
		this.spaceOwner.registerCollection( this );
	}


	/**
	 * @return the criteria
	 */
	public final Criteria getCriteria() {
		return this.criteria;
	}

	
	/**
	 * @param object
	 */
	final void notifyObjectNew(D object) {
		if ( this.criteria.match( object ) ) {
			getItems().add( object );
		}
	}
	
	/**
	 * @param objectClass
	 */
	final void notifyObjectNew(Class<? extends DomainObject> objectClass) {
		if ( this.criteria.getDomainObjectClass() == objectClass ) {
			synchronized( this ) {
				this.items = null;
			}
		}
	}

	/**
	 * @param object
	 */
	final void notifyObjectRemove(D object) {
		if ( this.criteria.match( object ) ) {
			getItems().remove( object );
		}
	}

	/**
	 * @return
	 */
	public AbstractDomainSpace getSpaceOwner() {
		return this.spaceOwner;
	}
	
	public final <SD extends D,FD extends FieldDescriptor<?,V>,V> boolean contains( Class<SD> domainObjectClass, Class<FD> fieldDesciptorClass, V expectedFieldValue ) {
		return expectedFieldValue != null ? getFirst(domainObjectClass, fieldDesciptorClass,  expectedFieldValue ) != null : false;
	}

	/**
	 * @param value
	 * @param selector
	 * @return
	 * 
	 */
	public final <SD extends D,FD extends FieldDescriptor<?,V>,V> D getFirst(Class<SD> domainObjectClass, Class<FD> fieldDesciptorClass, V expectedFieldValue) {
		Criteria<SD> criteria = CriteriaFactory.createEqual(domainObjectClass, fieldDesciptorClass, expectedFieldValue);
		DomainObjectList<D> gettedItems = getItems();
		LockedIterable<D> iterable = gettedItems.lockIterable();
		try {
			for (D item : iterable ) {
				if (criteria.match(item)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Select " + item + " by " + criteria);
					}
					return criteria.getDomainObjectClass().cast(item);
				}
			}
		} finally {
			iterable.release();
		}
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Select [NULL] by " + criteria + ", items count " + gettedItems.size() );
		}
		return null;
	}

	/**
	 * @return
	 * @see ss.framework.domainmodel2.DomainObjectList#lockIterable()
	 */
	public LockedIterable<D> lockIterable() {
		return this.items.lockIterable();
	}

	/**
	 * @return the items
	 */
	private synchronized final DomainObjectList<D> getItems() {
		if ( this.items == null ) {
			this.items = this.spaceOwner.selectItems( this.criteria );
		}			
		return this.items;
	}

}
