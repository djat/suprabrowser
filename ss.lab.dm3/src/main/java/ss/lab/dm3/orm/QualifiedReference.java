package ss.lab.dm3.orm;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
@Embeddable
public final class QualifiedReference<T extends MappedObject> implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 5547170678161584331L;

	private String targetQualifier;
	
	private Long targetId;

	public QualifiedReference() {
		super();
	}

	public QualifiedReference(T object) {
		super();
		if ( object == null ) {
			throw new NullPointerException( "object" );
		}
		this.targetQualifier = QualifiedUtils.resolveQualifier(object);
		this.targetId = object.getId();
	}
	
	/**
	 * @param targetId
	 * @param targetQualifier
	 */
	public QualifiedReference(QualifiedObjectId<T> objectId) {
		super();
		if ( objectId == null ) {
			throw new NullPointerException( "objectId" );
		}
		final Class<T> objectClazz = objectId.getObjectClazz();
		this.targetQualifier = QualifiedUtils.resolveQualifier(objectClazz);
		this.targetId = objectId.getId();
	}
	
	/**
	 * @param id
	 * @param targetQualifier
	 */
	public QualifiedReference(Class<T> objectClazz, Long id) {
		super();
		if ( objectClazz == null ) {
			throw new NullPointerException( "objectClazz" );
		}
		if ( id == null ) {
			throw new NullPointerException( "id" );
		}
		this.targetQualifier = QualifiedUtils.resolveQualifier(objectClazz);
		this.targetId = id;
	}

	public String getTargetQualifier() {
		return this.targetQualifier;
	}

	public Long getTargetId() {
		return this.targetId;
	}

	public void setTargetQualifier(String targetQualifier) {
		this.targetQualifier = targetQualifier;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.targetId == null) ? 0 : this.targetId.hashCode());
		result = prime * result + ((this.targetQualifier == null) ? 0 : this.targetQualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		QualifiedReference<?> other = (QualifiedReference<?>) obj;
		if (this.targetId == null) {
			if (other.targetId != null) {
				return false;
			}
		}
		else if (!this.targetId.equals(other.targetId)) {
			return false;
		}
		if (this.targetQualifier == null) {
			if (other.targetQualifier != null) {
				return false;
			}
		}
		else if (!this.targetQualifier.equals(other.targetQualifier)) {
			return false;
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Transient
	public T get() {
		OrmManager orm = QualifiedUtils.getOrmManager();
		final Class<? extends T> clazz = getTargetObjectClazz();
		if ( clazz == null && this.targetId == null ) {
			return null;
		}
		else {
			if ( this.targetId == null ) {
				throw new IllegalStateException( "Can't get object by null id. QualifiedReference is " + this );
			}
			return orm.resolve( clazz, this.targetId);
		}
	}
	
	public <E extends T> E get(Class<E> clazz) {
		return clazz.cast(get());
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Class<T> getTargetObjectClazz() {
		return this.targetQualifier != null ? (Class<T>) QualifiedUtils.resolveClass( this.targetQualifier ) : null;
	}
	
	/**
	 * @return
	 */
	@Transient
	public QualifiedObjectId<? extends T> getTargetQualifiedId() {
		return this.targetQualifier != null ? QualifiedObjectId.create( getTargetObjectClazz(), this.targetId ) : null;
	}	

	/**
	 * @param extension
	 * @return
	 */
	public static <E extends MappedObject> QualifiedReference<E> wrap(
			E object) {
		return object != null ? new QualifiedReference<E>( object ) : null;
	}
	
	public static <E extends MappedObject> QualifiedReference<E> wrap(
			QualifiedObjectId<E> objectId) {
		return objectId != null ? new QualifiedReference<E>( objectId ) : null;
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends MappedObject> Class<QualifiedReference<E>> wrap( Class<E> beanClazz ) {
		if ( beanClazz == null ) {
			throw new NullPointerException( "beanClazz" );
		}
		return (Class)QualifiedReference.class;
	}

	public static <E extends MappedObject> E unwrap( QualifiedReference<E> ref ) {
		return ref != null ? ref.get() : null;
	}

	@SuppressWarnings("unchecked")
	public <E extends MappedObject> QualifiedReference<E> cast( Class<E> objectClazz ) {
		 if ( getTargetObjectClazz().isAssignableFrom( objectClazz ) ) {
			 return (QualifiedReference<E>) this;
		 }
		 else {
			 throw new ClassCastException( "Can't case " + getTargetObjectClazz() + " to " + objectClazz );
		 } 
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "targetQualifier", this.targetQualifier );
		tsb.append( "targetId", this.targetId );
		return tsb.toString();
	}



	
}
