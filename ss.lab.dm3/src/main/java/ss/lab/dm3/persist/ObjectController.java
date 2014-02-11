package ss.lab.dm3.persist;

import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.mapper.BeanMapper;

/**
 * @author Dmitry Goncharov
 *
 */
public class ObjectController {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private static final String ANY_STATE_NAME = "ANY";

	/**
	 * Applicable state changes:
	 * 
	 * DETACHED from any 
	 * CLEAN from any 
	 * NEW from DETACHED 
	 * DIRTY from DETACHED, CLEAN 
	 * REMOVED from DETACHED, CLEAN, NEW, DIRTY 
	 * PENDING from DETACHED, NEW, DIRTY, REMOVED
	 * 
	 * Typical state flow:
	 * 
	 * NEW -> PENDING -> CLEAN
	 *  
	 * CLEAN -> DIRTY -> PENDING -> CLEAN
	 *  
	 * CLEAN or DIRTY or NEW -> REMOVED -> PENDING -> DETACHED
	 * 
	 * Special state MANAGED_MERGING applicable only in DomainObject function 
	 * 
	 */

	public enum State {

		DETACHED(ANY_STATE_NAME), CLEAN(ANY_STATE_NAME), NEW("DETACHED"), DIRTY(
				"DETACHED", "CLEAN"), REMOVED("DETACHED", "CLEAN", "NEW",
				"DIRTY"), PENDING("DETACHED", "NEW", "DIRTY", "REMOVED"), MANAGED_MERGING(), 
				PROXY(ANY_STATE_NAME);

		/**
		 * 
		 */
		private String[] fromStates;

		/**
		 * @param fromStates
		 */
		private State(String... fromStates) {
			this.fromStates = fromStates;
		}

		/**
		 * @param target
		 */
		public void checkSwitchTo(State target) {
			if (this == target) {
				return;
			}
			if (target.fromStates.length == 1
					&& target.fromStates.equals(ANY_STATE_NAME)) {
				return;
			}
			final String thisStateAsString = this.toString();
			for (String fromState : target.fromStates) {
				if (fromState.equals(thisStateAsString)) {
					return;
				}
			}
			throw new CantChangeDomainObjectStateException(target, this);
		}

	};

	/**
	 * 
	 */
	private long generation;

	/**
	 * Domain object should be accessed to domain thread.
	 * 
	 */
	private volatile State state = State.DETACHED;
	
	private DomainObject owner;
	
	private transient Domain domain;
	
	/**
	 * @param owner
	 */
	public ObjectController(DomainObject owner) {
		super();
		this.owner = owner;
	}
	
	public Domain getDomain() {
		if ( this.domain == null ) {
			this.domain = DomainResolverHelper.getCurrentDomain();
			// TODO ? additional actions?
		}
		return this.domain;
	}

	/**
	 * 
	 */
	public BeanMapper<DomainObject> getMapper() {
		return getDomain().getMapper().get( getEntityClass() );
	}

	/**
	 * @return
	 */
	public Class<? extends DomainObject> getEntityClass() {
		return (Class<? extends DomainObject>) DomainObjectInterceptor.getClassWoProxy( this.owner );
	}

	/**
	 * 
	 */
	public void ensureLoaded() {
		if ( isProxy() ) {
			getDomain().loadDataForProxy(this.owner);
		}
	}
	
	public boolean isDetached() {
		return this.state == State.DETACHED;
	}

	public boolean isNew() {
		return this.state == State.NEW;
	}

	public boolean isDirty() {
		return this.state == State.DIRTY;
	}

	public boolean isClean() {
		return this.state == State.CLEAN;
	}
	
	public boolean isProxy() {
		return this.state == State.PROXY;
	}

	public boolean isPending() {
		return this.state == State.PENDING;
	}

	public boolean isRemoved() {
		return this.state == State.REMOVED;
	}

	public boolean isManagedMerging() {
		return this.state == State.MANAGED_MERGING;
	}
	
	public final State getState() {
		return this.state;
	}

	// TODO setState, after introducing Transient property annotation
	void changeState(State newState) {
		this.state = newState;
	}
	
	/**
	 * 
	 */
	private void checkNotPending() {
		if (isPending()) {
			throw new ObjectIsPendingException(this.owner);
		}
	}

	protected synchronized final void markDirty() {
		checkNotPending();
		if (isDetached() || isNew() || isManagedMerging() ) {
			return;
		}
		if (!isDirty()) {
			this.state.checkSwitchTo(State.DIRTY);
			getDomain().registryDirty(this.owner);
		}
	}

	

	/**
	 * Mark object as new. It will automatically be saved in db on the
	 * transaction commit.
	 */
	protected synchronized final void markNew(Long id) {
		checkNotPending();
		this.owner.setId(id);
		this.state.checkSwitchTo(State.NEW);
		if (this.log.isDebugEnabled()) {
			this.log.debug("Marked as new " + this);
		}
		getDomain().registryNew(this.owner);
	}

	/**
	 * Mark object as new. It will automatically be saved in db on the
	 * transaction commit.
	 */
	protected synchronized final void markNew() {
		markNew(getDomain().createId(this.owner));
	}

	private synchronized final void markRemoved() {
		checkNotPending();
		if (!isRemoved()) {
			this.state.checkSwitchTo(State.REMOVED);
			getDomain().registryRemoved(this.owner);
		}
	}

	/**
	 * 
	 */
	public synchronized final void remove() {
		if (!isRemoved()) {
			this.owner.beforeRemove();
			markRemoved();
		}
	}
	
	public final void from(Entity entity, boolean managed ) {
		State pushState = this.state;
		try {
			this.state = managed ? State.MANAGED_MERGING : State.DETACHED;
			getMapper().toObject( this.owner, entity);
		}
		finally {
			this.state = pushState;
		}
	}

	/**
	 * @param other
	 * @return
	 */
	public boolean from(DomainObject other, boolean managed ) {
		State pushState = this.state;
		try {
			this.state = managed ? State.MANAGED_MERGING : State.DETACHED;
			return getMapper().toObject(this.owner, other);
		}
		finally {
			this.state = pushState;
		}
	}

	public long getGeneration() {
		return this.generation;
	}

	void setGeneration(long generation) {
		this.generation = generation;
	}

	/**
	 * @return
	 */
	public static ObjectController get(DomainObject object) {
		return object.ctrl;
	}
	
}
