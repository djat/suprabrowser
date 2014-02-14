/**
 * 
 */
package ss.server.domainmodel2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.UpdateData;
import ss.server.domainmodel2.db.statements.StatementExecutorPool;

/**
 * 
 */
public final class DataMapper implements IRecordMapperProvider {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DataMapper.class);

	private final IRecordMapperProvider recordMapperProvider = new RecordMapperProvider();  
	
	private final QueryFactory queryBuilder = new QueryFactory( this );

	private final StatementExecutorPool statementExecutorPool;
	
	/**
	 * @param statementExecutor
	 */
	public DataMapper( String dbUrl ) {
		super();
		this.statementExecutorPool = new StatementExecutorPool( dbUrl );
	}

	public synchronized List<Record> select( final Criteria criteria ) {
		final List<Record> records = new ArrayList<Record>(); 
		executeQuery( this.queryBuilder.createSelect( criteria ), new IResultSetRowHandler() {
			public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
				RecordMapper mapper = getMapper( criteria.getDomainObjectClass() );
				records.add(mapper.mapResultSet(rs));
			}
		});
		return records;
	}

	
	/**
	 * @param query
	 * @param recordCollector
	 * @see ss.server.domainmodel2.db.statements.StatementExecutor#executeQuery(java.lang.String, ss.server.domainmodel2.db.IResultSetRowHandler)
	 */
	private void executeQuery(String query, IResultSetRowHandler recordCollector) {
		IStatementExecutor statementExecutor = this.statementExecutorPool.getStatementExecutor();
		try {
			statementExecutor.executeQuery(query, recordCollector);
		}
		finally {
			statementExecutor.release();
		}
	}

	/* (non-Javadoc)
	 * @see ss.server.domainmodel2.IRecordMapperProvider#getMapper(java.lang.Class)
	 */
	public RecordMapper getMapper(Class domainObjectClass) {
		return this.recordMapperProvider.getMapper(domainObjectClass);
	}

	/**
	 * 
	 */
	public void dispose() {
		this.statementExecutorPool.disposeFree();
	}

	/**
	 * @param data
	 */
	public void update(UpdateData data) {
		IStatementExecutor statementExecutor = this.statementExecutorPool.getStatementExecutor();
		try {
			statementExecutor.openTransaction();
			delete( statementExecutor, data.getRemovedRecords() );
			update( statementExecutor, data.getDirtyRecords() );
			insert( statementExecutor, data.getNewRecords() );
			statementExecutor.commitTransaction();
		}
		finally {
			statementExecutor.release();
		}
	}

	
	private void insert(IStatementExecutor statementExecutor, Iterable<Record> records) {
		for( Record record : records ) {
			statementExecutor.execute( this.queryBuilder.createInsert( record ) );						
		}
	}
	
	/**
	 * @param records
	 */
	private void delete(IStatementExecutor statementExecutor, Iterable<Record> records) {
		for( Record record : records) {
			statementExecutor.execute( this.queryBuilder.createDelete( record ) );
		}	
	}	

	/**
	 * @param dirtyRecords
	 */
	private void update(IStatementExecutor statementExecutor, Iterable<Record> records) {
		for( Record record : records ) {
			statementExecutor.execute( this.queryBuilder.createUpdate( record ) );
		}
	}

	/**
	 * @return the statementExecutorPool
	 */
	public StatementExecutorPool getStatementExecutorPool() {
		return this.statementExecutorPool;
	}

	

	
}
