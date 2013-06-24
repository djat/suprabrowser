/**
 * 
 */
package ss.server.domainmodel2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ss.common.DateUtils;
import ss.framework.domainmodel2.Criteria;
import ss.framework.domainmodel2.Record;
import ss.framework.domainmodel2.RunntimeDomainObject;
import ss.server.db.DBPool;
import ss.server.db.DbUrlProvider;
import ss.server.domainmodel2.db.DbDataProvider;
import ss.server.domainmodel2.db.statements.StableConnectionProvider;
import ss.server.domainmodel2.db.statements.StatementExecutorPool;

/**
 *
 */
public class RuntimeDataProvider {

	private final DbDataProvider dbDataProvider;
	
	private int counter;

	
	/**
	 * @param dbDataProvider
	 */
	public RuntimeDataProvider(final DbDataProvider dbDataProvider) {
		super();
		this.dbDataProvider = dbDataProvider;
	}

	public List<Record> selectItems(Criteria criteria) {
		final Record record = new Record( RunntimeDomainObject.class );
		record.setId( ++this.counter  );
		final DBPool dbPool = DBPool.getPool();
		record.setLong( "xmldbPoolFreeSize", dbPool.freeConnSize() );
		record.setLong( "xmldbPoolInUseSize", dbPool.usedConnSize() );
		final StatementExecutorPool pool = this.dbDataProvider.getDataMapper().getStatementExecutorPool();
		record.setLong( "dm2PoolFreeSize", pool.getFreeExecutorsCount() );
		record.setLong( "dm2PoolTotalSize", pool.getTotalExecutorsCount() );
		record.setLong( "dm2PoolConnectionCounter", StableConnectionProvider.getPooledConnectionCounter() );
		final long dbUsageUptime = new Date().getTime() - DbUrlProvider.INSTANCE.getCreationTime().getTime();
		record.setText( "dbUsageUptime", DateUtils.timeSpanToPrettyString( dbUsageUptime ) );
		List<Record> records = new ArrayList<Record>();
		records.add(record);
		return records;
	}

}
