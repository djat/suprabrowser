package ss.lab.dm3.orm.managed;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerResolveHelper;

/**
 * @author Dmitry Goncharov
 */
public class ManagedReference<T extends MappedObject> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private MappedObject owner;
	
	private String name;
	
	private Class<T> entityType;
	
	private T object = null;
	
	protected Long id;

	/**
	 * @param owner
	 */
	public ManagedReference(MappedObject owner, Class<T> entityType) {
		super();
		this.owner = owner;
		this.entityType = entityType;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
		this.object = null;
		afterIdChanged(id);
	}
	
	/**
	 * @return
	 */
	public final boolean isNullReference() {
		return this.id == null;
	}
	
	public static <E extends MappedObject> ManagedReference<E> create( MappedObject owner, Class<E> entityType ) {
		return new ManagedReference<E>( owner, entityType );
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void afterIdChanged(Long id) {
		if ( isManagedByOrm() ) {
			getOrmManager().referenceChanged(this);
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Ignore id change for " + this );
			}			
		}
	}
	
	protected final boolean isManagedByOrm() {
		OrmManager manager = getOrmManager();
		return manager != null ? manager.isObjectManaged( getOwner() ) : false;
	}

	public synchronized T get() {
		if (isNullReference()) {
			return null;
		}
		if (this.object != null) {
			return this.object;
		}
		this.object = resolve();
		return this.object;
	}

	public void set(T value) {
		if (this.object != value) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Setting reference. Old value " + value
						+ ", new value " + this.object);
			}
			this.object = value;
			this.id = this.object == null ? null : this.object.getId();
			afterValueChanged(this.object);
		}
	}

	/**
	 */
	protected void afterValueChanged(T value) {
		if ( isManagedByOrm() ) {
			getOrmManager().referenceChanged( this );
		}
	}

	/**
	 * @return
	 */
	private T resolve()  {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Resolving " + this.entityType.getSimpleName() + ", name " + this.name + ", id "  + this.id );
		}
		final OrmManager ormManager = getOrmManager();
		if ( ormManager == null ) {
			throw new NullPointerException( "ormManager is null for " + this );
		}
		T resolved = ormManager.getObjectResolver().resolveManagedOrNull( this.entityType, this.id );
		if (this.log.isDebugEnabled() && resolved == null ) {
			this.log.debug( "Resolved " + resolved );
		}
		return resolved;
	}

	/**
	 * @return
	 */
	protected OrmManager getOrmManager() {
		return OrmManagerResolveHelper.resolve( this.owner );
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		return tsb.append("owner", this.owner).append("id", this.id).append("value", this.object)
				.toString();
	}

	public MappedObject getOwner() {
		return this.owner;
	}

	public Class<T> getEntityType() {
		return this.entityType;
	}

	/**
	 * 
	 */
	public void refresh() {
		if ( isManagedByOrm() ) {
			//if ( this.object != null ) {
			this.getOrmManager().referenceChanged(this);
			//}
		}
		else {
			this.log.error( "Reference is not managed by orm " + this );
		}
	}

}
