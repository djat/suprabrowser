/**
 * 
 */
package ss.server.domainmodel2.db;

import ss.common.ArgumentNullPointerException;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.Record;

/**
 * 
 */
final class QueryFactory {

	private final IRecordMapperProvider mapperProvider;
	
	/**
	 * @param mapperProvider
	 */
	public QueryFactory(IRecordMapperProvider mapperProvider) {
		if ( mapperProvider == null ) {
			throw new ArgumentNullPointerException( "mapperProvider" );
		}
		this.mapperProvider = mapperProvider;		
	}
	
	/**
	 * @param criteria
	 * @return
	 */
	protected final QueryStringBuilder createBuilder(Criteria criteria) {
		return createBuilder( criteria.getDomainObjectClass() );
	}

	/**
	 * @param record
	 * @return
	 */
	protected final QueryStringBuilder createBuilder(Record record) {
		return createBuilder( record.getDomainObjectClass() );
	}
	
	/**
	 * @param mapper
	 * @return
	 */
	private QueryStringBuilder createBuilder(Class domainObjectClass) {
		RecordMapper mapper = this.mapperProvider.getMapper( domainObjectClass );
		return new QueryStringBuilder( mapper );
	}

	/**
	 * @param record
	 * @return
	 */
	public String createUpdate(Record record) {
		return createBuilder(record).add("UPDATE").addTableName().add("SET")
				.addValuesUpdate(record).addWhereById(record).toString();
	}

	/**
	 * @param criteria
	 * @return
	 */
	public String createSelect(Criteria criteria) {
		return createBuilder(criteria).add("SELECT").add("*")
				.add("FROM").addTableName().addWhere(criteria).toString();
	}
	
	public final String createDelete(Record record) {
		return createBuilder(record).add("DELETE FROM").addTableName()
				.addWhereById(record).toString();
	}
	
	public String createInsert(Record record) {
		return createBuilder(record).add("INSERT INTO").addTableName()
				.openBrace().addFieldNames(record).closeBrace().add(
						"VALUES").openBrace().addValues(record).closeBrace()
				.toString();
	}
}
