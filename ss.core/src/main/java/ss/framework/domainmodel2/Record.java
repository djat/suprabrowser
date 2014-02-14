/**
 * 
 */
package ss.framework.domainmodel2;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.global.SSLogger;


/**
 *
 */
public final class Record implements Serializable  {

	private static final Logger logger = SSLogger.getLogger(Record.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2426465205655766190L;

	private final Class<? extends DomainObject> domainObjectClass;
	
	private final Hashtable<String,String> fieldNameToValue = new Hashtable<String,String>();
	
	/**
	 * @param class
	 */
	public Record(Class<? extends DomainObject> domainObjectClass ) {
		this.domainObjectClass = domainObjectClass;
	}

	/**
	 * @return
	 */
	public long getId() {
		return getLong( "id" );
	}

	/**
	 * @param id
	 */
	public void setId(long id) {
		setLong("id", id);		
	}

	/**
	 * @param string
	 */
	public long getLong(String columnName) {
		String value = getText(columnName);
		if ( value != null ) {
			try {
				return Long.parseLong( value ); 
			}
			catch( NumberFormatException ex ) {
				logger.error(ex.getMessage(), ex);
			}
		}
		return 0;
	}

	public void setLong(String columnName, long value ) {
		setText(columnName, Long.toString( value ) );
	}

	/**
	 * @param login
	 * @return
	 */
	public String getText(String columnName) {
		return this.fieldNameToValue.get(columnName);
	}

	/**
	 * @param login
	 * @param login2
	 */
	public void setText(String columnName, String value) {
		this.fieldNameToValue.put(columnName, value == null ? "" : value );
	}

	public boolean getBoolean(String columnName) {
		String value = getText(columnName);
		return value != null && 0 == value.compareToIgnoreCase( "true" );
	}

	public void setBoolean(String columnName, boolean value ) {
		setText(columnName, value ? "true" : "false" );
	}

	/**
	 * @return
	 */
	public Class<? extends DomainObject> getDomainObjectClass() {
		return this.domainObjectClass;
	}

	/**
	 * @return
	 */
	public Iterable<String> getFieldNames() {
		return this.fieldNameToValue.keySet();
	}

	/**
	 * @param fieldName
	 * @return
	 */
	public Object getFieldValue(String fieldName) {
		return getText(fieldName);
	}

	/**
	 * @param string
	 */
	public void declare(String[] fieldNames) {
		for( String fieldName : fieldNames ) {
			setText(fieldName, "" );
		}
	}

	/**
	 * @return
	 */
	public QualifiedObjectId createQualifiedId() {
		return new QualifiedObjectId( getDomainObjectClass(), getId() );
	}
	
}
