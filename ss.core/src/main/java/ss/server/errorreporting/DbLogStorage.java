package ss.server.errorreporting;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import ss.common.ArgumentNullPointerException;
import ss.framework.errorreporting.CantCreateSessionException;
import ss.framework.errorreporting.ICreateSessionInformation;
import ss.framework.errorreporting.ILogEvent;
import ss.framework.errorreporting.ILogStorage;
import ss.framework.errorreporting.SessionInformation;
import ss.server.db.DbUrlProvider;
import ss.server.domainmodel2.db.statements.StableConnectionProvider;

public class DbLogStorage implements ILogStorage {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DbLogStorage.class);

	public final static DbLogStorage INSTACE = new DbLogStorage();

	private StableConnectionProvider connectionProvider;

	private DbLogStorage() {
		this.connectionProvider = new StableConnectionProvider( DbUrlProvider.INSTANCE.getDbUrl() );		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see errorreporting.store.ILogStorer#store(errorreporting.logext.remote.LogEvent)
	 */
	public void store(ILogEvent logEvent) {
		if ( logEvent == null ) {
			throw new ArgumentNullPointerException( "logEvent" );
		}
		PreparedStatementHelper helper = createHelper( "insert into log_event( session_id, message, stact_trace, location_info, context, level, date ) values (?, ?, ?, ?, ?, ?, ? )" );
		try {
			helper.setLong(1, logEvent.getSessionId());
			helper.setString(2, logEvent.getMessage());
			helper.setString(3, logEvent.getStackTrace());
			helper.setString(4, logEvent.getLocationInformation());
			helper.setString(5, logEvent.getContext());
			helper.setString(6, logEvent.getLevel());
			helper.setTimestamp( 7, new Timestamp( System.currentTimeMillis() ) );
			helper.executeAndCommit();
		} catch (SQLException ex) {
			logger.error("Can't save log event", ex);
			helper.silentRollback();			
		}
		finally {
			helper.silentClose();
		}		
	}

	/**
	 * @param string
	 * @return
	 */
	private PreparedStatementHelper createHelper(String query) {
		return new PreparedStatementHelper( query, this.connectionProvider );
	}

	/* (non-Javadoc)
	 * @see ss.framework.errorreporting.ILogStorage#createSession(ss.framework.errorreporting.ICreateSessionInformation)
	 */
	public SessionInformation createSession(ICreateSessionInformation createSessionInfo) throws CantCreateSessionException {
		if ( createSessionInfo == null ) {
			throw new ArgumentNullPointerException( "createSessionInfo" );
		}
		PreparedStatementHelper helper = createHelper( "insert into log_session ( session_key, user_name, date, context ) values (?, ?, ?, ? )" );
		try {
			helper.setString(1, createSessionInfo.getSessionKey() );
			helper.setString(2, createSessionInfo.getUserName() );
			final long currentTimeMillis = System.currentTimeMillis();
			helper.setTimestamp( 3, new Timestamp( currentTimeMillis ) );
			helper.setString( 4, createSessionInfo.getContext() );
			return new SessionInformation( helper.executeInsert(), new Date( currentTimeMillis ), createSessionInfo );			
		} catch (SQLException ex) {
			helper.silentRollback();
			throw new CantCreateSessionException( createSessionInfo, ex );
		}
		finally {
			helper.silentClose();
		}
	}

}
