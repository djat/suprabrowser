/**
 * 
 */
package ss.server.domainmodel2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ss.framework.domainmodel2.DomainObject;
import ss.framework.domainmodel2.Record;

/**
 *
 */
final class RecordMapper {

	private final Class<? extends DomainObject> domainObjectClass;
	
	private final String tableName;
	
	private final List<String> fieldNames;
	
	/**
	 * @param domainObjectClass
	 * @param tableName
	 * @param fieldNames
	 */
	public RecordMapper(final Class<? extends DomainObject> domainObjectClass, final String tableName, final String[] fieldNames) {
		super();
		this.domainObjectClass = domainObjectClass;
		this.tableName = tableName;
		this.fieldNames = new ArrayList<String>();
		for( String fieldName : fieldNames ) {
			this.fieldNames.add( fieldName ); 	
		}		
	}


	/**
	 * @param record
	 * @return
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * @param record 
	 * @param record
	 * @return
	 */
	public Iterable<String> getFieldNames(Record record) {
		ArrayList<String> names = new ArrayList<String>(); 
		for ( String fieldName : record.getFieldNames() ) {
			names.add( fieldName );
		}
		return names;
	}

	/**
	 * @param record
	 * @return
	 */
	public Iterable<Object> getFieldValues(Record record) {
		ArrayList<Object> values = new ArrayList<Object>(); 
		for ( String fieldName : record.getFieldNames() ) {
			values.add( record.getFieldValue(fieldName) );
		}
		return values;
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	public Record mapResultSet(ResultSet rs) throws SQLException {
		Record record = new Record( this.domainObjectClass );
		for( String fieldName : this.fieldNames ) {
			record.setText( fieldName, rs.getString( fieldName ) );
		}
		return record;
	}

	/**
	 * @param record
	 * @return
	 */
	public Map<String, Object> getFieldNameToValue(Record record) {
		Map<String,Object> fieldToValue = new Hashtable<String,Object>();
		for( String fieldName : record.getFieldNames() ) {
			fieldToValue.put( fieldName, record.getFieldValue( fieldName ) ); 
		}
		return fieldToValue;
	}

	/**
	 * @return
	 */
	public Iterable<String> getFieldNames() {
		return this.fieldNames;
	}
 
	/**
	 * @return
	 */
	public String getIdFieldName() {
		return "id";
	}


	/**
	 * @return the domainObjectClass
	 */
	public Class<? extends DomainObject> getDomainObjectClass() {
		return this.domainObjectClass;
	}
	
	

}
