package ss.server.db.suprasphere;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import ss.domainmodel.SupraSphereStatement;
import ss.server.db.QueryExecutor;
import ss.server.domainmodel2.db.IResultSetRowHandler;
import ss.server.domainmodel2.db.ResultSetRowHandlerException;

public class SupraSphereSingleton {

	private static final String SUPRASPHERE = "suprasphere";

	public static final SupraSphereSingleton INSTANCE;

	private volatile Document document;
	
	private volatile SupraSphereStatement statement;

	private final Object sync = new Object();
	
	private volatile boolean upToDate = false;
	
	private String modifiedDate = null;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereSingleton.class);

	static {
		INSTANCE = new SupraSphereSingleton();
	}

	private SupraSphereSingleton() {
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * getDocument returns the SupraSphere Document in a thread safe manor. That
	 * is if another thread has called refresh, the getDocument method will
	 * return once refresh has completed.
	 * 
	 * @return the SupraSphere Document
	 */
	public Document getDocument() {
		synchronized (this.sync) {
			checkUpToDate();
			return this.document;
		}
	}
	
	public Document getDocumentCloned(){
		synchronized (this.sync) {
			checkUpToDate();
			return (Document) this.document.clone();
		}
	}
	
	public SupraSphereStatement getStatement(){
		synchronized (this.sync) {
			checkUpToDate();
			return this.statement;
		}
	}

	/**
	 * refresh will attempt to immediately update the supra sphere document
	 * 
	 * @throws DocumentException
	 */
	private void refresh() throws DocumentException {
			if (logger.isDebugEnabled()) {
				logger.debug("Refreshing supra sphere document");
			}
			this.document = fetchSupraSphereDocument();
			this.statement = SupraSphereStatement.wrap(this.document);
	}

	private Document fetchSupraSphereDocument() throws DocumentException {
		final String query = "select XMLDATA, MODIFIED from supraspheres where type = '" + SUPRASPHERE + "'";
		final AtomicReference<Document> doc = new AtomicReference<Document>();
		final AtomicReference<String> mod = new AtomicReference<String>();
		QueryExecutor.safeExecuteQuery( query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				final String xml = rs.getString( 1 );
				final String modified = rs.getString( 2 );
				mod.set(modified);
				try {
					doc.set(DocumentHelper.parseText(xml));
				} catch (DocumentException ex) {
					logger.error("Parse failed. Xml " + xml);
					throw new ResultSetRowHandlerException(ex);
				}
			}
			
		});
		if (doc.get() == null) {
			logger.fatal("The suprasphere record in the supraspheres table is missing!");
			throw new DocumentException("Document doesn't exist in database");
		}
		setModified( mod.get() );
		return doc.get();
	}
	
	private void setModified( final String newValue ) {
		this.modifiedDate = newValue;
	}
	
	private synchronized boolean isModifiedUpToDate(){
		final String query = "select MODIFIED from supraspheres where type = '" + SUPRASPHERE + "'";
		final AtomicReference<String> mod = new AtomicReference<String>();
		QueryExecutor.safeExecuteQuery( query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				mod.set( rs.getString( 1 ) );
			}
			
		});
		if ( (this.modifiedDate == null) || (mod.get() == null) || 
				(!this.modifiedDate.equals( mod.get() )) ){
			return false;
		}
		return true;
	}
	
	public boolean isSupraSphere( final String type ){
		return SUPRASPHERE.equals(type);
	}
	
	public void setOutdated(){
		synchronized (this.sync) {
			this.upToDate = false;
		}
	}
	
	private void checkUpToDate(){
		if (this.upToDate && isModifiedUpToDate()) {
			return;
		}
		try {
			refresh();
			this.upToDate = true;
		} catch ( Exception ex ){
			logger.error("Error in refreshing document", ex);
		}
	}
}
