package ss.lab.dm3.orm;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class QualifiedObjectId<T> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8413296726277574387L;

	private final Class<T> objectClazz;
	
	private final Long id;

	/**
	 * @param objectClazz
	 * @param id
	 */
	public QualifiedObjectId(Class<T> objectClazz, Long id) {
		super();
		this.objectClazz = objectClazz;
		this.id = id;
	}

	public Class<T> getObjectClazz() {
		return this.objectClazz;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime
				* result
				+ ((this.objectClazz == null) ? 0 : this.objectClazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QualifiedObjectId<?> other = (QualifiedObjectId<?>) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.objectClazz == null) {
			if (other.objectClazz != null)
				return false;
		} else if (!this.objectClazz.equals(other.objectClazz))
			return false;
		return true;
	}

	/**
	 * @param targetClass
	 * @param targetId
	 * @return
	 */
	public static <E> QualifiedObjectId<E> create(
			Class<E> targetClass, Long targetId) {
		return new QualifiedObjectId<E>(targetClass, targetId);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "objectClazz", this.objectClazz.getSimpleName() );
		tsb.append( "id", this.id );
		return tsb.toString();
	}

	/**
	 * @param class1
	 * @param qualifiedId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> QualifiedObjectId<T> cast(Class<T> objectClazz,
			QualifiedObjectId<?> qualifiedId) {
		if ( qualifiedId == null ) {
			return null;
		}
		else {
			if ( objectClazz.isAssignableFrom( qualifiedId.getObjectClazz() ) ) {
				return (QualifiedObjectId<T>) qualifiedId;
			}
			else {
				throw new IllegalArgumentException( "Can't cast qualifiedId [" + qualifiedId + "]" + " to " + objectClazz );
			}
		}
	}
	
	
	
	
	
	
}
