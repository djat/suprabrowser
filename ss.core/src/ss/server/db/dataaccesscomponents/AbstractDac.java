/**
 * 
 */
package ss.server.db.dataaccesscomponents;

import java.sql.SQLException;
import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.XmlDocumentUtils;
import ss.server.db.DbUtils;
import ss.server.db.QueryExecutor;
import ss.server.db.XMLDB;

/**
 *
 */
public abstract class AbstractDac {

	private static XMLDB XMLDB_INSTANCE = null ;   
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractDac.class);
	
	public final XMLDB getXmlDb() {
		if ( XMLDB_INSTANCE == null ) {
			XMLDB_INSTANCE = new XMLDB( new Hashtable());
		}
		return XMLDB_INSTANCE ;
	}
	
	protected final Document findFirstDocument( String sphereId, String type, AbstractInlineCondition condition )  {
		final String statement = DataCommand.SELECT.formatPrefix() +
			formatStatementCondition(sphereId, type, condition);
		try {
			return QueryExecutor.safeQueryFirstDocument( statement );
		} catch (Throwable ex) {
			ss.common.ExceptionHandler.handleException(this, ex );
			return null;
		}
	}
	
	private String formatStatementCondition( String sphereId, String type, AbstractInlineCondition condition ) {
		return " WHERE"
		    + " sphere_id = " + DbUtils.quote( sphereId )
			+ " and type = " + DbUtils.quote( type )
			+ " and xmldata like " + DbUtils.quote( condition.formatLikeString() );		
	}

	/**
	 * @param sphereId
	 * @param type
	 * @param documentCopy
	 */
	public synchronized void deleteDocument(final String sphereId, final String type, AbstractInlineCondition condition ) {
		executeUpdate(DataCommand.DELETE.formatPrefix() + formatStatementCondition(sphereId, type, condition)); 
	}

	/**
	 * @param statement
	 */
	private void executeUpdate(final String statement) {
		try {
			logger.info("execute update " + statement);
			QueryExecutor.executeUpdate(statement);
		} catch (SQLException ex) {
			ss.common.ExceptionHandler.handleException(this, ex );
		}
	}
	
	/**
	 * @param sphereId
	 * @param type
	 * @param documentCopy
	 */
	public synchronized void insertDocument(final String sphereId, final String type, Document document ) {
		// TODO identify do we need all fields 
		// SPHERE_ID,XMLDATA,TYPE,THREAD_TYPE,MOMENT,CREATE_TS,THREAD_ID,MESSAGE_ID,ISRESPONSE,MODIFIED
		String statement = DataCommand.INSERT.formatPrefix() 
		+ " (sphere_id,type,xmldata, moment, thread_type, thread_id, message_id, modified )"
		+ " VALUES ("
		+ DbUtils.quote( sphereId )  
		+ ", " + DbUtils.quote( type )
		+ ", " + DbUtils.quote( XmlDocumentUtils.toCompactString( document) )
		+ ", " + DbUtils.quote( DbUtils.formatNowDate() )
		+ ", " + DbUtils.quote( type )
		+ ", " + DbUtils.quotedStubId()
		+ ", " + DbUtils.quotedStubId()
		+ ", " + DbUtils.quote( DbUtils.formatNowDate() )
		+ ")";
		executeUpdate( statement );
	}

	/**
	 * @param sphereId
	 * @param type
	 * @param document
	 * @param condition
	 */
	public synchronized void updateDocument(String sphereId, String type, Document document, AbstractInlineCondition condition) {
		try {
			deleteDocument(sphereId, type, condition);
		} catch(NullPointerException npe) {
			logger.error("cannot find and delete such doc");
		}
		insertDocument(sphereId, type, document);
	}
}
