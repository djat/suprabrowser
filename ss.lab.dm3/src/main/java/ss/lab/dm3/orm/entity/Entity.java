package ss.lab.dm3.orm.entity;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class Entity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5933096201119681982L;

	private final Long mapId;
	
	private final Serializable[] values;

	/**
	 * @param mapId
	 * @param values
	 */
	public Entity(long mapId, Serializable[] values) {
		super();
		this.mapId = mapId;
		this.values = values;
	}

	public Long getMapId() {
		return this.mapId;
	}

	public Serializable[] getValues() {
		return this.values;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "mapId", this.mapId )
		.append( "valuesLength", this.values.length )
		.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Entity ) {
			return equalsDeep((Entity) obj);
		}
		return false;
	}

	/**
	 * @param other
	 * @return
	 */
	public boolean equalsDeep(Entity other) {
		if ( this.mapId != other.mapId ||
			 this.values.length != other.values.length ) {
			return false;
		}
		for (int n = 0; n < this.values.length; n++) {
			if ( !equals( this.values[n], other.values[ n ] ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean equals(Serializable x, Serializable y) {
		if ( x == y ) {
			return true;
		}
		if ( x == null ||
			y == null ) {
			return false;
		}
		return x.equals( y );
	}

	/**
	 * @param after
	 */
	public boolean eqaulsByValues(Entity other) {
		if ( other == null || this.mapId != other.mapId ) {
			return false;
		}
		for (int n = 0; n < this.values.length; n++) {
			Serializable thisValue = this.values[n];
			Serializable otherValue = other.values[n];
			if ( thisValue == otherValue ) {
				continue;
			}
			if ( thisValue == null || otherValue == null ) {
				return false;
			}
			if ( !thisValue.equals( otherValue ) ) {
				return false;
			}
		}
		return true;
	}
	
	

}
