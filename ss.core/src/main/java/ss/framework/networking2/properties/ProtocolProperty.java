package ss.framework.networking2.properties;

import java.io.Serializable;

import ss.common.CompareUtils;

public abstract class ProtocolProperty<T extends Serializable> implements Serializable {

	private final T value;

	/**
	 * @param value
	 */
	public ProtocolProperty(T value) {
		super();
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public final T getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return this.value == null ? 0 : this.value.hashCode();
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
		final ProtocolProperty other = (ProtocolProperty) obj;
		return CompareUtils.equals( this.value, other.value );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " = " + this.value;
	}

	
	
	
	
}
