package ss.common.protocolobjects;

import java.io.Serializable;
import java.util.Hashtable;

public class AbstractProtocolObject {
	
	private Hashtable<String,Serializable> valuesMap;

	public AbstractProtocolObject () {
		this( new Hashtable<String,Serializable>());
	}
	
	@SuppressWarnings("unchecked")
	public AbstractProtocolObject ( Hashtable valuesMap ) {
		super();
		if ( valuesMap == null ) {
			throw new NullPointerException( "valuesMap cannot be null" );
		}
		this.valuesMap = valuesMap;		
	}


	/**
	 * Put value to hashtable,
	 * Null values skip
	 * @param key not null value key
	 */
	protected void putValue(String key, String value ) {
		if ( key == null ) {
			throw new NullPointerException( "key cannot be null" );
		}
		if ( value == null ) {
			return;
		}
		this.valuesMap.put( key, value );	
	}
	
	/**
	 * Put value to hashtable,
	 * Null values not supported.
	 */
	protected final void putNotNullValue(String key, Serializable value ) {
		if ( key == null ) {
			throw new NullPointerException( "key cannot be null" );
		}
		if ( value == null ) {
			throw new NullPointerException( "Value for key " + key +" is null." );
		}
		this.valuesMap.put( key, value );		
	}
	
	/**
	 * Returns value by key or null if no value was found.
	 * @param key not null value key
	 */
	protected final Serializable getValue(String key ) {
		if ( key == null ) {
			throw new NullPointerException( "key cannot be null" );
		}
		return this.valuesMap.get(key );
	}

	/**
	 * Returns value by key or null if no value was found.
	 * @param key not null value key
	 */
	protected final String getStringValue(String key ) {
		return (String) this.getValue( key );
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Hashtable<String,Object> getValues() {
		return (Hashtable)this.valuesMap;
	}
}