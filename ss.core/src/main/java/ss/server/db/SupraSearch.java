package ss.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import ss.common.SearchCriteria;
import ss.common.exception.SystemException;
import ss.server.domainmodel2.db.IResultSetRowHandler;
import ss.server.domainmodel2.db.ResultSetRowHandlerException;
import ss.server.networking.DialogsMainPeer;

public class SupraSearch {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSearch.class);

	private Hashtable session;

	private SearchCriteria criteria;

	private DialogsMainPeer peer;

	// CLOOOG DialogsMainPeer is being passed in because it's a royal
	// encapsulation
	// nightmare with verifyAuth to call getEnabledSpheres and XMLDB to get a
	// bloody
	// database connection - sb
	public SupraSearch(DialogsMainPeer peer, Hashtable aSession,
			SearchCriteria searchCriteria) {
		this.peer = peer;
		this.session = aSession;
		this.criteria = searchCriteria;
	}

	public List execute() throws SystemException {

		final List<Document> results = new ArrayList<Document>();
		QueryBuilder builder = new QueryBuilder();
		XMLDB xmldb = this.peer.getXmldb();

		// get all valid sphereIds for this user
		Vector sphereIds = xmldb.getVerifyAuth().getEnabledSpheres(
				(String) this.session.get("real_name"));

		// build query based on the criteria and the sphereIds to search on
		final String query = builder.buildSupraQuery(this.criteria, sphereIds);
		xmldb.safeExecuteQuery(query, new IResultSetRowHandler() {

			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				// TODO Auto-generated method stub
				try {
					results.add(DocumentHelper.parseText(rs
							.getString(QueryBuilder.XMLDATA)));
				} catch (DocumentException exc) {
					logger.error(
							"Ignoring: xml couldn't be parsed into a document"
									+ " in supra search. recid = "
									+ rs.getInt(QueryBuilder.REC_ID)
									+ " Criteria = "
									+ SupraSearch.this.criteria, exc);
				}
			}
		});
		return results;
	}
}
