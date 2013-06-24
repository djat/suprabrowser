/**
 * 
 */
package ss.framework.domainmodel2;

import java.io.Serializable;

/**
 * 
 */
final class QualifiedObjectId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7106570846603744026L;

	private final Class<? extends DomainObject> objectClass;
	
	private final long objectId;
	
	/**
	 * @param objectClass
	 * @param objectId
	 */
	public QualifiedObjectId(final Class<? extends DomainObject> objectClass, final long objectId) {
		super();
		this.objectClass = objectClass;
		this.objectId = objectId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.objectClass == null) ? 0 : this.objectClass.hashCode());
		result = PRIME * result + (int) (this.objectId ^ (this.objectId >>> 32));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final QualifiedObjectId other = (QualifiedObjectId) obj;
		if (this.objectClass == null) {
			if (other.objectClass != null)
				return false;
		} else if (!this.objectClass.equals(other.objectClass))
			return false;
		if (this.objectId != other.objectId)
			return false;
		return true;
	}

	/**
	 * @return the objectClass
	 */
	public Class<? extends DomainObject> getObjectClass() {
		return this.objectClass;
	}

	/**
	 * @return the objectId
	 */
	public long getObjectId() {
		return this.objectId;
	}	
	
	
	
}
