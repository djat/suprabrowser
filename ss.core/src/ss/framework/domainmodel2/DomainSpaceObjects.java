/**
 * 
 */
package ss.framework.domainmodel2;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.event.EventListenerList;

import ss.framework.domainmodel2.network.UpdateResult;

/**
 * 
 */
final class DomainSpaceObjects {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DomainSpaceObjects.class);
	
	private final AbstractDomainSpace spaceOwner;
	
	private final DomainObjectMap cleanObjects = new DomainObjectMap();

	private final DomainObjectMap newObjects = new DomainObjectMap();

	private final DomainObjectMap removedObjects = new DomainObjectMap();

	private final DomainObjectMap dirtyObjects = new DomainObjectMap();
	
	private final EventListenerList listeners = new EventListenerList();

	private final Object listenersMutex = new Object();

	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	/**
	 * @param spaceOwner
	 */
	public DomainSpaceObjects(AbstractDomainSpace spaceOwner) {
		super();
		this.spaceOwner = spaceOwner;
	}

	/**
	 * @param object
	 * @return
	 */
	public final boolean isNew(DomainObject object) {
		return this.newObjects.contains(object);
	}

	public final boolean isClean(DomainObject object) {
		return this.cleanObjects.contains(object);
	}

	public final boolean isRemoved(DomainObject object) {
		return this.removedObjects.contains(object);
	}

	public final boolean isDirty(DomainObject object) {
		return this.dirtyObjects.contains(object);
	}

	/**
	 * 
	 */
	final void clearChanges() {
		this.readWriteLock.writeLock().lock();
		try {
			this.removedObjects.clear();
			this.newObjects.clear();
			this.dirtyObjects.clear();
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	
	final void unsafeUnregisterObject( DomainObject object ) {
		this.cleanObjects.remove(object);
		this.removedObjects.remove(object);
		this.newObjects.remove(object);
		this.dirtyObjects.remove(object);		
	}

	/**
	 * 
	 */
	final void clear() {
		this.readWriteLock.writeLock().lock();
		try {
			this.cleanObjects.clear();
			clearChanges();
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
	/**
	 * @param object
	 */
	public final void markNew(DomainObject object) {
		this.readWriteLock.writeLock().lock();
		try {
			if (isClean(object) || isRemoved(object) || isDirty(object)) {
				throw new CannotMarkObjectAnsNewException(object);
			}
			this.newObjects.add(object);
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	/**
	 * @param object
	 */
	public void markRemoved(DomainObject object) {
		this.readWriteLock.writeLock().lock();
		try {
			if (isNew(object)) {
				this.newObjects.remove(object);
			} else {
				this.cleanObjects.remove(object);
				this.dirtyObjects.remove(object);
				this.removedObjects.add(object);
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	public <D extends DomainObject> D getSingleObject(Criteria<D> criteria) {
		return this.selectItems(criteria).getFirst();
	}

	/**
	 * @param object
	 */
	public void markDirty(DomainObject object) {
		this.readWriteLock.writeLock().lock();
		try {
			if (isClean(object)) {
				this.cleanObjects.remove(object);
				this.dirtyObjects.add(object);
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}
		
	public <D extends DomainObject> DomainObjectList<D> selectItems(Criteria<D> criteria) {
		this.readWriteLock.writeLock().lock();
		try {
			final DomainObjectList<D> result = new DomainObjectList<D>(
					criteria.getDomainObjectClass());
			List<Record> records;
			try {
				records = getDataProvider().selectItems(criteria);
			} catch (DataProviderException ex) {
				throw new SelectFailedException(ex);
			}
			for (Record record : records) {
				final D domainObject = unsafeTranslateRecordToDomainObject(
						criteria.getDomainObjectClass(), record);
				if (domainObject != null) {
					result.add(domainObject);
				}
			}
			this.cleanObjects.collectObjects(result, criteria);
			this.newObjects.collectObjects(result, criteria);
			this.dirtyObjects.collectObjects(result, criteria);
			return result;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	/**
	 * @return
	 */
	private IDataProvider getDataProvider() {
		return this.spaceOwner.checkAndGetDataProvider();
	}

	/**
	 * @param record
	 */
	private <D extends DomainObject> D unsafeTranslateRecordToDomainObject(Class<D> objectClass, Record record) {
		if (isRemoved(objectClass, record.getId())) {
			return null;
		}
		D object = getLoadedObjectById(objectClass, record.getId());
		if (object != null) {
			return object;
		} else {
			object = this.spaceOwner.createBlank(objectClass);
			unsafeMakeObjectClean(object, record);
			return object;
		}		
	}
	
	private void unsafeMakeObjectClean( DomainObject object, Record objectDataRecord ) {
		if (object.getClass() != objectDataRecord
				.getDomainObjectClass()) {
			logger.error("Object and record point to different domain classes.");
		}
		object.load(objectDataRecord);
		this.cleanObjects.add(object);
	}

	/**
	 * @param id
	 * @return
	 */
	private <D extends DomainObject> boolean isRemoved(Class<D> domainObjectClass, long id) {
		this.readWriteLock.readLock().lock();
		try {
			return this.removedObjects.getObjectById(domainObjectClass, id ) != null;
		} finally {
			this.readWriteLock.readLock().unlock();
		}	
	}

	/**
	 * @param id
	 * @return
	 */
	public <D extends DomainObject> D getLoadedObjectById(Class<D> domainObjectClass, long id) {
		this.readWriteLock.readLock().lock();
		try {
			D object = this.cleanObjects.getObjectById(domainObjectClass,
					id);
			if (object == null) {
				object = this.dirtyObjects.getObjectById(domainObjectClass, id);
			}
			if (object == null) {
				object = this.newObjects.getObjectById(domainObjectClass, id);
			}
			return object;
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	public <D extends DomainObject> D getObjectById(Class<D> domainObjectClass, long id) {
		return getSingleObject( CriteriaFactory.createEqual( domainObjectClass, DomainObject.IdDescriptor.class, id ) );			
	}

	/**
	 * @throws CommitFailedException
	 * 
	 */
	public void commitChanges() throws CommitFailedException {
		this.readWriteLock.writeLock().lock();
		try {
			try {
				final UpdateData updateData = unsafeCollectUpdate();
				UpdateResult result = this.getDataProvider().update(updateData);
				mergeUpdate(result.getChangedData(), false);
				LockedIterable<DomainObject> iterable = this.newObjects.lockIterable();
				try {
					//TODO#think about this
					this.cleanObjects.addAll( iterable );
				}
				finally {
					iterable.release();
				}
				this.newObjects.clear();
			} catch (DataProviderException ex) {
				throw new CommitFailedException(ex);
			} finally {
				clearChanges();
			}
		} finally {
			this.readWriteLock.writeLock().unlock();
		}		
	}	
	
	/**
	 * @return
	 */
	private UpdateData unsafeCollectUpdate() {
		UpdateData updateData = new UpdateData(this.spaceOwner.getId());
		updateData.addRemoved(this.removedObjects.toRecords());
		updateData.addDirty(this.dirtyObjects.toRecords());
		updateData.addNew(this.newObjects.toRecords());
		return updateData;		
	}

	public void rollbackChanges() {
		// throw new TbiException( "Implement changes rollback" );
		logger.error( "Rolling back not yet implemented" );
		this.clearChanges();
	}

	public boolean mergeUpdate(ChangedData changedData, boolean externalUpdate ) {
		this.readWriteLock.writeLock().lock();
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("MergingUpdate. External update " + externalUpdate
						+ ". Data " + changedData);
			}
			final AffectedDomainObjectList removedObjects = removeObjects(changedData
					.getRemoved());
			final AffectedDomainObjectList modifiedObjects = unsafeGetModifiedObjects(changedData
					.getModified());
			if (removedObjects.size() > 0) {
				notifyObjectRemoved(removedObjects);
			}
			if (modifiedObjects.size() > 0) {
				notifyObjectChanged(modifiedObjects);
			}
			if (externalUpdate) {
				if (removedObjects.isContainDirty()
						|| modifiedObjects.isContainDirty()
						|| modifiedObjects.isContainRemoved()) {
					return false;
				}
			}
			return true;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	private AffectedDomainObjectList removeObjects( Iterable<QualifiedObjectId> objectsIds ) {
		this.readWriteLock.writeLock().lock();
		try {
			AffectedDomainObjectList affectedObjects = new AffectedDomainObjectList();
			for (QualifiedObjectId id : objectsIds) {
				affectedObjects.add(this.dirtyObjects.remove(id),
						ObjectMark.DIRTY);
				affectedObjects.add(this.cleanObjects.remove(id),
						ObjectMark.CLEAN);
				affectedObjects.add(this.removedObjects.remove(id),
						ObjectMark.REMOVED);
			}
			return affectedObjects;
		} finally {
			this.readWriteLock.writeLock().unlock();
		}
	}

	private AffectedDomainObjectList unsafeGetModifiedObjects(Iterable<Record> records) {
		AffectedDomainObjectList affectedObjects = new AffectedDomainObjectList();
		for (Record record : records) {
			DomainObject object = getLoadedObjectById(record
					.getDomainObjectClass(), record.getId());
			if (object != null) {
				affectedObjects.add(object, object.getMark());
				unsafeUnregisterObject(object);
				unsafeMakeObjectClean(object, record);
			}
		}
		return affectedObjects;		
	}
	
	/**
	 * @param listener
	 */
	public void addDomainChangesListener(DomainChangesListener listener) {
		synchronized( this.listenersMutex ) {
			this.listeners.add(DomainChangesListener.class, listener);
		}
	}

	/**
	 * @param listener
	 */
	public void removeDomainChangesListener(DomainChangesListener listener) {
		synchronized( this.listenersMutex ) {
			this.listeners.remove(DomainChangesListener.class, listener);
		}		
	}

	void notifyObjectRemoved(AffectedDomainObjectList objects) {
		synchronized( this.listenersMutex ) {
			// Guaranteed to return a non-null array
			Object[] listeners = this.listeners.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event
			
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == DomainChangesListener.class) {
					// Lazily create the event:
					//if (fooEvent == null)
					//		fooEvent = new FooEvent(this);
					((DomainChangesListener) listeners[i + 1]).objectRemoved( objects );
				}
			}
		}
	}
	
	void notifyObjectChanged(AffectedDomainObjectList objects) {
		synchronized( this.listenersMutex ) {
			// Guaranteed to return a non-null array
			Object[] listeners = this.listeners.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event
			
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == DomainChangesListener.class) {
					// Lazily create the event:
					//if (fooEvent == null)
					//		fooEvent = new FooEvent(this);
					((DomainChangesListener) listeners[i + 1]).objectChanged( objects );
				}
			}
		}
	}

	/**
	 * @param object
	 * @return
	 */
	public ObjectMark getMark(DomainObject object) {
		this.readWriteLock.readLock().lock();
		try {
			if (isClean(object)) {
				return ObjectMark.CLEAN;
			} else if (isDirty(object)) {
				return ObjectMark.DIRTY;
			} else if (isNew(object)) {
				return ObjectMark.NEW;
			} else if (isRemoved(object)) {
				return ObjectMark.REMOVED;
			} else {
				return ObjectMark.OBSOLETE;
			}
		} finally {
			this.readWriteLock.readLock().unlock();
		}
	}	

	/**
	 *
	 */
	public static class CommitFailedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8877015915753417625L;

		/**
		 * @param cause
		 */
		public CommitFailedException(DataProviderException cause) {
			super(cause);		
		}		
	}

	/**
	 *
	 */
	public static class SelectFailedException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8878985342062886525L;

		/**
		 * @param cause
		 */
		public SelectFailedException(DataProviderException cause) {
			super(cause);
		}		
	}	
}
