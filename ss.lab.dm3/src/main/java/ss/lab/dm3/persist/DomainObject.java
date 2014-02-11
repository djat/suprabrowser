package ss.lab.dm3.persist;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;

/**
 * @author Dmitry Goncharov
 */
@MappedSuperclass
public class DomainObject implements IDomainObject {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	final ObjectController ctrl = new ObjectController( this );
	
	Long id = null;
	
	private QualifiedObjectId<? extends DomainObject> qualifiedId = null; 

	/**
	 * 
	 */
	public DomainObject() {
		super();
	}

	/**
	 * @param id
	 */
	public DomainObject(Long id) {
		this(id, null);
	}

	/**
	 * @param id
	 * @param objectVersion
	 */
	public DomainObject(Long id, ObjectVersion objectVersion) {
		super();
		this.id = id;
		// this.objectVersion = objectVersion;
	}

	@Id
	@org.hibernate.annotations.GenericGenerator(name = "hibernate-assigned",strategy = "assigned")
	@GeneratedValue(strategy = GenerationType.AUTO,generator="hibernate-assigned" )
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		if ( this.id == id || ( this.id != null && this.id.equals( id ) ) ) {
			return;
		}
		if (!this.ctrl.isDetached()) {
			throw new CantUpdateDomainObjectIdException(this);
		}
		this.id = id;
		if ( this.qualifiedId != null && !this.qualifiedId.getId().equals( id ) ) {
			this.qualifiedId = null;
		}
	}	

	@SuppressWarnings("unchecked")
	@Transient
	public final QualifiedObjectId<? extends DomainObject> getQualifiedId() {
		if ( this.qualifiedId == null ) {
			final Long id = getId();
			if ( id == null ) {
				throw new NullPointerException( "Id is null for " + this );
			}
			this.qualifiedId = new QualifiedObjectId(getEntityClass(), id);
		}
		return this.qualifiedId;
	}
	
	/**
	 * @return
	 */
	@Transient
	public final Class<? extends DomainObject> getEntityClass() {
		return this.ctrl.getEntityClass();
	}

	/**
	 * 
	 */
	protected void beforeRemove() {
	}

	/*
	 * 
	 * TODO Think about toString operation.
	 * Potential very problematic place for wrapped objects.
	 *  
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("state", this.ctrl.getState()).append("id", this.id);
		return tsb.toString();
	}

	/**
	 * @param tsb
	 */
	@Deprecated
	protected void toString(ToStringBuilder tsb) {
		// tsb.append("state", this.ctrl.getState()).append("id", this.id);
	}

	/**
	 * @param mapperManager
	 * @return
	 */
	public final Entity toEntity() {
		return this.ctrl.getMapper().toEntity(this);
	}

	@Deprecated
	public static <T extends DomainObject> T createNew(Class<T> objClazz) {
		throw new UnsupportedOperationException( "Please use domain.createObject()");
	}

	/**
	 * 
	 */
	public final void from(Entity entity, boolean managed ) {
		this.ctrl.from(entity, managed);
	}
	
	/**
	 * Use delete() instead
	 */
	@Deprecated
	public final void remove() {
		delete();
	}

	public final void delete() {
		this.ctrl.remove();
	}
	
	public final void forceDirty() {
		this.ctrl.markDirty();
	}
	
	@Transient
	public Domain getDomain() {
		return this.ctrl.getDomain();
	}

	public String toShortString() {
		StringBuilder sb = new StringBuilder();
		sb.append( getEntityClass().getSimpleName() );
		sb.append( "@" );
		sb.append( hashCode() );
		sb.append( "#" );
		sb.append( this.ctrl.getState() );
		sb.append( "," );
		sb.append( this.id );
		return sb.toString();
	}
}
