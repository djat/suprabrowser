/*
 A module XML database, which allows different XML databases to be plugged in. Now, it
 uses the Xerces database from Apache, but as long as the methods in this file work for
 a different kind of database, it could use any database.
 */

package ss.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractDocument;
import org.dom4j.tree.DefaultElement;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.BookmarkUtils;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.common.XmlDocumentUtils;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.domain.service.SupraSphereFacadeProvider;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.FavouriteSphere;
import ss.domainmodel.FileStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.ObjectRelation;
import ss.domainmodel.Order;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.SphereLocation;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.search.SphereIndex;
import ss.server.db.suprasphere.SupraSphereSingleton;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.domainmodel2.db.IResultSetRowHandler;
import ss.server.domainmodel2.db.ResultSetRowHandlerException;
import ss.server.domainmodel2.db.statements.IStableConnectionProvider;
import ss.server.networking.DialogsMainPeer;
import ss.util.DateTimeParser;
import ss.util.SessionConstants;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;
import ss.util.VotingEngine;

/**
 * Description of the Class
 * 
 * @author david
 * @created March 23, 2004
 */

public class XMLDB {

	/**
	 * 
	 */
	public static final String HH_MM_SS = "HH:mm:ss";

	/**
	 * 
	 */
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd " + HH_MM_SS;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XMLDB.class);

	private static final Random tableIdGenerator = new Random();

	private final Hashtable session;

	private String dbSphere;

	private String sphereName;

	private VerifyAuth verifyAuth;

	private final XmldbUtils utils;

	private final Convertor convertor = new Convertor(this);

	private final static Object writeLock = new Object();

	private final LightSphereMessagesSelector sphereDefinitionSelector;

	/**
	 * Constructor for the XMLDB object
	 * 
	 * @param session
	 *            Description of the Parameter
	 */
	public XMLDB(Hashtable session) {
		this.session = session;
		this.utils = new XmldbUtils(this);
		this.sphereDefinitionSelector = new LightSphereMessagesSelector(this);
		loadProperties();
	}

	public XMLDB() {
		this(null);
	}

	public String getDBSphere() {
		return this.dbSphere;
	}

	public VerifyAuth getVerifyAuth() {
		return this.verifyAuth;
	}

	public void setVerifyAuth(VerifyAuth auth) {
		this.verifyAuth = auth;
	}

	public synchronized long getNextTableId() {
		return Math.abs(tableIdGenerator.nextLong());
	}

	public void initVerifyAuth() {
		try {
			this.setVerifyAuth(SupraSphereProvider.INSTANCE.createVerifyAuth());
		} catch (Throwable e) {
			logger.error("Error in initialization VerifyAuth" ,e);
		}
	}

	public String getSphereName() {
		return this.sphereName;
	}

	public void loadProperties() {
		logger.info("properties: " + System.getProperty("user.dir"));
		final File file = VariousUtils.getSupraFile("dyn_server.xml");
		final SAXReader reader1 = new SAXReader();
		try {
			final Document doc = reader1.read(file);
			this.dbSphere = doc.getRootElement().element("mysql")
					.attributeValue("db_sphere");
			logger.info("DB SPHERE: " + this.dbSphere);
			this.sphereName = doc.getRootElement().element("mysql")
					.attributeValue("sphere_name");
			if (this.sphereName == null) {
				this.sphereName = this.dbSphere;
			}
		} catch (Exception exc) {
			logger.error("Couldn't load properties", exc);
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @param buildDoc
	 *            Description of the Parameter
	 */
	public synchronized Document saveTabOrderToContact(Hashtable session,
			final Document buildDoc, String loginSphere) {
		String login_name = (String) session.get("username");
		return updateContactDoc(loginSphere, login_name,
				new IDocumentHandler() {
					public void handleDocument(Document contactDocument) {
						ContactStatement contact = ContactStatement
								.wrap(contactDocument);
						contact.clearBuildOrder();

						Element buildOrder = (Element) buildDoc
								.getRootElement().clone();
						for (int i = 0; i < buildOrder.elements().size(); i++) {
							Order order = Order.wrap((Element) buildOrder
									.elements("order").get(i));
							contact.getBuildOrder().add(order);
						}
					}
				});
	}

	public synchronized Document addSphereToContactFavourites(
			Hashtable session, final Document buildDoc, String loginSphere) {
		logger.info("start adding sphere to contact document");
		final String login_name = (String) session.get("username");
		return updateContactDoc(loginSphere, login_name,
				new IDocumentHandler() {
					public void handleDocument(Document contactDocument) {
						ContactStatement contact = ContactStatement
								.wrap(contactDocument);
						Element favourites = (Element) buildDoc
								.getRootElement().clone();
						FavouriteSphere fs = FavouriteSphere
								.wrap((Element) favourites.elements("sphere")
										.get(0));
						contact.addSphereToFavourites(fs);
					}
				});
	}

	@SuppressWarnings("unchecked")
	public synchronized Document saveWindowPositionToContact(Hashtable session,
			final Document windowDoc, String loginSphere) {
		final String login_name = (String) session.get("username");
		return updateContactDoc(loginSphere, login_name,
				new IDocumentHandler() {

					public void handleDocument(Document document) {
						Element build = null;
						try {
							build = document.getRootElement().element(
									"window_position");
							document.getRootElement().remove(build);

						} catch (NullPointerException exc) {
							logger
									.error(
											"Document was not created from parseText()",
											exc);
						}

						Element buildOrder = (Element) windowDoc
								.getRootElement().clone();

						if (build == null) {
							document.getRootElement().add(buildOrder);
						} else {
							Vector vec = new Vector(build.elements());
							// Go through the old build order, if the old
							// order contains a single entry for a system name,
							// add that to the new build order, otherwise,

							for (int i = 0; i < vec.size(); i++) {
								Element one = (Element) vec.get(i);
								String systemName = one
										.attributeValue("system_name");
								Element order = null;

								String apath = "//window_position/order[@system_name=\""
										+ systemName + "\"]";
								// String apath =
								// "//build_order/order[@value=\""+"0"+"\"]";

								try {
									order = (DefaultElement) windowDoc
											.selectObject(apath);
								} catch (ClassCastException exc) {
									logger.error("Should be a DefaultElement",
											exc);
								} catch (Exception exc) {
									logger
											.error(
													"Catch all exception - should probably get passed up",
													exc);
								}

								// Only way it should add the order is if the
								// order is null

								if (order == null) {
									Element newOne = (Element) one.clone();
									buildOrder.add(newOne);
								}
							}
							document.getRootElement().add(buildOrder);
						}
					}

				});
	}

	public synchronized Document getRegularSphereDocument(String supraSphere,
			final String sphereId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ supraSphere
				+ "' and type='"
				+ "sphere"
				+ "'"
				+ DbUtils.likeXmlInlineAttribute( "system_name", sphereId)
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocument(query);
	}

	/**
	 * Gets the sphereOrder attribute of the XMLDB object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The sphereOrder value
	 */
	public synchronized Document getSphereOrder(Hashtable session) {
		String username = (String) session.get("username");
		String sphereId = (String) session.get("supra_sphere");
		return getContactDoc(sphereId, username);
	}

	/**
	 * Gets the forSphere attribute of the XMLDB object
	 * 
	 * @param sphere
	 *            Description of the Parameter
	 * @param sphere_document
	 *            Description of the Parameter
	 * @return The forSphere value
	 */

	public synchronized Vector<Document> getAllMessages( final String sphereId ) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
			+ sphereId + "'";
		return new Vector<Document>(safeQueryDocumentList(query));
	}
	
	public synchronized Vector<Document> getAllNonVotedMessages( final String sphereId, 
			final String contactName ) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
			+ sphereId + "'";// and XMLDATA like ";
		return new Vector<Document>(safeQueryDocumentList(query));
	}
	
	public synchronized Vector<Document> getMostRecentCreatedMessages( final String sphereId,
			final int count) {
		if (count < 1) {
			logger.error("Count should be greater then 0");
			return null;
		}
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
			+ sphereId + "'" + " and type in ('message', '"
					+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
					+ "', 'file', '"
					+ SupraXMLConstants.TYPE_VALUE_RESULT
					+ "', '"
					+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
					+ "', '"
					+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
					+ "','bookmark', 'comment', 'keywords',"
					+ " 'terse', 'edit', 'reply', 'library', 'contact', 'sphere', 'rss')" 
			+" ORDER BY `create_ts` DESC LIMIT " + count;

		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public synchronized Document[] registerResponse(Document workflowDoc,
			String resultId, String sphereId) {
		Document returnDoc = null;
		Document parentDoc = null;
		try {
			Document document = getDocByMessageId(resultId, sphereId);
			
			ResultStatement resultSt = ResultStatement.wrap(document);
			resultSt.getResponseCollection().add(
					WorkflowResponse.wrap(workflowDoc));
			AbstractDelivery model = resultSt.getModelForServer();
			parentDoc = getDocByMessageId(resultSt.getResponseId(), sphereId);
			ss.domainmodel.Statement parentSt = ss.domainmodel.Statement
					.wrap(parentDoc);
 
			if (model.isPassed(resultSt)) {
				parentSt.setPassed(true);
				parentDoc = replaceDoc(parentSt.getBindedDocument(), sphereId);
				resultSt.setSubject(ResultStatement.PASSED);
			} else {
				if (!model.canPassed(resultSt)) {
					resultSt.setSubject(ResultStatement.NOT_PASSED);
				}
			}

			returnDoc = replaceDoc(resultSt.getBindedDocument(), sphereId);
		} catch (Exception exc) {
			logger.error("Exception occurred in getAllMessages. continuing..",
					exc);
		}
		
		return new Document[] { returnDoc, parentDoc };
	}

	@SuppressWarnings("unchecked")
	public synchronized Document[] getEntireThread(String sphereId,
			String threadId) {

		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and thread_id = "
				+ threadId
				+ " ORDER BY create_ts";

		logger.info("SELECTING ONE ENTIRE THREAD like this: " + query);
		List<Document> docs = safeQueryDocumentList(query);

		return docs.toArray(new Document[]{});
	}

	public synchronized Vector<Document> getOnlyThread(String sphereId,
			String threadId, Hashtable responses) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId + "' and thread_id = " + threadId;
		final Vector<Document> thread = new Vector<Document>(
				safeQueryDocumentList(query));
		if (responses != null) {
			for (Document document : thread) {
				responses.remove(document.getRootElement()
						.element("message_id").attributeValue("value"));
			}
		}
		return thread;
	}

	public synchronized Hashtable getForSphereLight(Vector sphereList) {
		return this.sphereDefinitionSelector.getForSphereLight(sphereList);
	}

	public synchronized Hashtable getForSphereLight(Hashtable session,
			DialogsMainPeer handler, String sphere, Document sphere_document,
			Long existingStart, Long existingEnd) throws DocumentException {
		return this.sphereDefinitionSelector.getForSphereLight(session,
				handler, sphere, sphere_document, existingStart, existingEnd);
	}

	private static class ThresholdCalculator implements IResultSetRowHandler {
		int totalNumber = 0;

		int total = 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.server.domainmodel2.db.IRecordCollector#nextRecord(java.sql.ResultSet)
		 */
		public void handleRow(ResultSet rs) throws ResultSetRowHandlerException,
				SQLException {
			this.totalNumber++;
			int accrued = rs.getInt(1);
			logger.info("accrued..." + accrued);
			this.total += accrued;
		}

		public int getResult(String topOrBottom) {
			logger.info("totalNumber: " + this.totalNumber);
			logger.info("total: " + this.total);

			if (topOrBottom.equals("top")) {
				double calc = this.total / (this.totalNumber * (.75));
				return new Double(calc).intValue();
			} else {
				double calc = this.total / (this.totalNumber * (.25));
				return new Double(calc).intValue();
			}
		}
	}

	public int getMostUsedThresholdForStatement(String sphere, String types,
			String filter, String topOrBottom) {
		final String query = "select total_accrued from supraspheres where sphere_id = '"
				+ sphere
				+ "' and "
				+ types
				+ " AND XMLDATA LIKE '%"
				+ filter
				+ "%' AND total_accrued is not null)";
		logger.info("getmostusedthresholdfor: " + query);

		ThresholdCalculator thresholdCalculator = new ThresholdCalculator();
		safeExecuteQuery(query, thresholdCalculator);
		return thresholdCalculator.getResult(topOrBottom);
	}

	public int getMostUsedThresholdForStatement(String sphere, String types,
			String filter, String startMomentString, String endMomentString,
			boolean isUsed, String topOrBottom) {
		String query;
		if (!isUsed) {
			query = "select total_accrued from supraspheres where sphere_id = '"
					+ sphere
					+ "' and ("
					+ types
					+ " AND XMLDATA LIKE '%"
					+ filter
					+ "%' AND (\""
					+ startMomentString
					+ "\" > moment and moment > \""
					+ endMomentString
					+ "\") AND total_accrued is not null)";
		} else {
			query = "select total_accrued from supraspheres where sphere_id = '"
					+ sphere
					+ "' and ("
					+ types
					+ " AND XMLDATA LIKE '%"
					+ filter
					+ "%' AND (\""
					+ startMomentString
					+ "\" > used and used > \""
					+ endMomentString
					+ "\") AND total_accrued is not null)";
		}
		logger.info("getmostusedthresholdfor: " + query);
		ThresholdCalculator thresholdCalculator = new ThresholdCalculator();
		safeExecuteQuery(query, thresholdCalculator);
		return thresholdCalculator.getResult(topOrBottom);
	}

	public synchronized Document checkIfSeen(String sphereId,
			String uniqueEnoughToCheck) {
		String query = "select xmldata from supraspheres where sphere_id = '"
				+ sphereId + "' and XMLDATA like '%<address value=\""
				+ uniqueEnoughToCheck + "\"/>%'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;

		logger.info("Checking if seen: " + sphereId);
		return safeQueryFirstDocument(query);
	}

	public synchronized Document checkIfSeenRSS(String sphereId,
			String uniqueEnoughToCheck) {
		String query = "select xmldata from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and type='rss' and XMLDATA like '%<subject value=\""
				+ uniqueEnoughToCheck + "\"/>%'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;

		logger.info("Checking if seen: " + sphereId);
		return safeQueryFirstDocument(query);
	}

	public synchronized Document getSphereDefinition(String supraSphere,
			String sphereId) {
		sphereId = this.utils.replaceChars(sphereId);
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ supraSphere
				+ "' and ((type='sphere') AND (XMLDATA like '%system_name=\""
				+ sphereId + "\"%')) " + DbUtils.SUFFIX_FOR_TOP_RECORD;
	
		return safeQueryFirstDocument(query);
	}

	/**
	 * Gets the decisiveMember attribute of the XMLDB object
	 * 
	 * @param sphere
	 *            Description of the Parameter
	 * @return The decisiveMember value
	 */
	public synchronized String getDecisiveMember(String sphere) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere
				+ "' and type='sphere'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		final Document doc = safeQueryFirstDocument(query);
		if (doc != null) {
			Element dec = null;
			try {
				dec = doc.getRootElement().element("voting_model").element(
						"specific").element("member");
			} catch (NullPointerException npe) {
			}
			if (dec != null) {
				return dec.attributeValue("contact_name");
			}
		}
		return null;
	}

	private class ExtendedDocumentGetter implements IResultSetRowHandler {

		private String recId;
		
		protected Document dbDoc;

		private String moment;
		
		private String createTs;


		/* (non-Javadoc)
		 * @see ss.server.domainmodel2.db.IResultSetRowHandler#handleRow(java.sql.ResultSet)
		 */
		public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
			try {
				this.dbDoc = DocumentHelper.parseText(rs.getString(2));
			} catch (DocumentException exc) {
				logger.error("Doc exc", exc);
				return;
			}

			this.recId = rs.getString(1);
			this.moment = rs.getString(3);
			this.createTs = rs.getString(4);
		}
		
		/**
		 * @return the rec_id
		 */
		public String getRecId() {
			return this.recId;
		}

		/**
		 * @return the old_doc
		 */
		public Document getDbDoc() {
			return this.dbDoc;
		}

		/**
		 * @return the moment
		 */
		public String getMoment() {
			return this.moment;
		}

		/**
		 * @return the createTs
		 */
		public String getCreateTs() {
			return this.createTs;
		}

	}
	
	private class DocumentVoter extends ExtendedDocumentGetter  {

		private final String real_name;

		private final String vote_moment;

		private final String sphere;


		/**
		 * @param real_name
		 * @param vote_moment
		 * @param sphere
		 */
		public DocumentVoter(final String real_name,
				final String vote_moment, final String sphere) {
			super();
			this.real_name = real_name;
			this.vote_moment = vote_moment;
			this.sphere = sphere;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.server.domainmodel2.db.IRecordCollector#nextRecord(java.sql.ResultSet)
		 */
		public void handleRow(ResultSet rs) throws ResultSetRowHandlerException,
				SQLException {
			super.handleRow(rs);
			if ( this.dbDoc == null ) {
				return;
			}

			try {
				this.dbDoc.getRootElement().element("voting_model").element(
						"tally").addElement("member").addAttribute("value",
						this.real_name).addAttribute("vote_moment",
						this.vote_moment);

				boolean ratified = checkThreshold(this.dbDoc, this.sphere);

				if (ratified) {

					Element confElement = this.dbDoc.getRootElement().element(
							"confirmed");
					if (confElement == null) {

						this.dbDoc.getRootElement().addElement("confirmed");
						this.dbDoc.getRootElement().element("confirmed")
								.addAttribute("value", "true");
					} else {
						this.dbDoc.getRootElement().element("confirmed")
								.addAttribute("value", "true");
					}
				} else {
					Element confElement = this.dbDoc.getRootElement().element(
							"confirmed");
					if (confElement == null) {

						this.dbDoc.getRootElement().addElement("confirmed");
						this.dbDoc.getRootElement().element("confirmed")
								.addAttribute("value", "false");
					} else {
						this.dbDoc.getRootElement().element("confirmed")
								.addAttribute("value", "false");
					}
				}
			} catch (NullPointerException exc) {
				logger.error("NPE exc, problem in doc!", exc);
				this.dbDoc.getRootElement().addElement("voting_model")
						.addAttribute("type", "absolute").addAttribute("desc",
								"Absolute without qualification");
				this.dbDoc.getRootElement().element("voting_model").addElement(
						"tally").addAttribute("number", "0.0").addAttribute(
						"value", "0.0");
			}
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param sphere
	 *            Description of the Parameter
	 * @param real_name
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */

	public synchronized Document voteDoc(final Document doc, String sphere,
			String real_name) {

		final String type = doc.getRootElement().element("type")
				.attributeValue("value");
		final String thread_type = doc.getRootElement().element("thread_type")
				.attributeValue("value");

		final SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		final String message_id = doc.getRootElement().element("message_id")
				.attributeValue("value");

		final String query = "select RECID, XMLDATA, MOMENT, create_ts from supraspheres where sphere_id = '"
				+ sphere
				+ "' and message_id = "
				+ message_id
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;

		final Date current = new Date();
		final String vote_moment = DateFormat.getTimeInstance(DateFormat.LONG)
				.format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		DocumentVoter documentVoter = new DocumentVoter(
				real_name, vote_moment, sphere);
		safeExecuteQuery(query, documentVoter);
		final String recId = documentVoter.getRecId();  
		final Document dbDoc = documentVoter.getDbDoc(); 
		final String newdate = documentVoter.getMoment();
		final String timeStamp = documentVoter.getCreateTs();
		if (dbDoc == null) {
			return null;
		}

		final Element thread = doc.getRootElement().element("thread_id");
		String threadId;
		if (thread != null) {
			threadId = thread.attributeValue("value");
		} else {
			threadId = message_id;
		}
		String isResponse;
		if (doc.getRootElement().element("response_id") != null) {
			isResponse = "1";
		} else {
			isResponse = "0";
		}

		final String finalModified = df2.format(current);
		final String newXmldata = this.utils.quote(dbDoc.asXML());

		String updateQuery = "replace into supraspheres "
				+ " (RECID,XMLDATA,TYPE,SPHERE_ID,THREAD_TYPE,MOMENT,CREATE_TS,THREAD_ID,MESSAGE_ID,ISRESPONSE,USED,MODIFIED) values ('"
				+ recId + "','" + newXmldata + "','" + type + "','" + sphere
				+ "','" + thread_type + "','" + newdate + "','" + timeStamp
				+ "','" + threadId + "','" + message_id + "','" + isResponse
				+ "','" + finalModified + "','" + finalModified + "')";
		safeExecuteUpdate(updateQuery);
		if (SupraSphereSingleton.INSTANCE.isSupraSphere(type)) {
			SupraSphereSingleton.INSTANCE.setOutdated();
		}
		try {
			SphereIndex sphereIndex = SphereIndex.get(sphere);
			sphereIndex.updateDoc(dbDoc);
		} catch (Throwable ex) {
			logger.error("Error in indexing voted document" , ex);
		}
		return dbDoc;
	}
	
	public void updateSupraSphereDoc(Document doc) {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				YYYY_MM_DD_HH_MM_SS);
		final String modified = dateFormatter.format( new Date() );
		final String updateQuery = "update supraspheres set XMLDATA = '" 
			+ this.utils.quote(doc.asXML()) + "', " + "MODIFIED = '" + modified +
			"' where type = 'suprasphere'";
		safeExecuteUpdate(updateQuery);
		SupraSphereSingleton.INSTANCE.setOutdated();
	}

	public synchronized Document useDoc(final Document doc, String sphere,
			String real_name, String increment) {

		String createdMoment = null;
		String modifiedMoment = null;

		String message_id = doc.getRootElement().element("message_id")
				.attributeValue("value");

		SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

		Element thread = doc.getRootElement().element("thread_id");
		String threadId = null;
		if (thread != null) {
			threadId = thread.attributeValue("value");
		} else {
			threadId = message_id;
		}

		try {
			createdMoment = doc.getRootElement().element("moment")
					.attributeValue("value");
		} catch (Exception e) {

			Date current = new Date();
			String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
					current)
					+ " "
					+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(
							current);
			createdMoment = moment;
			modifiedMoment = moment;
		}

		Date current = new Date();
		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		modifiedMoment = moment;
		String finalModified = df2.format(current);

		SimpleDateFormat stf = new SimpleDateFormat("h:m:s a z MMM dd, yyyy");

		try {
			Date dC = stf.parse(createdMoment);
			Date dM = stf.parse(modifiedMoment);
			//
			logger.info("in usedoc: " + dC.toString() + " : " + dM.toString());
		} catch (ParseException exc) {
			logger.debug("Parse Exception in useDoc", exc);
		}

		String type = doc.getRootElement().element("type").attributeValue(
				"value");
		String thread_type = doc.getRootElement().element("thread_type")
				.attributeValue("value");

		String query = "select RECID, XMLDATA, MOMENT, create_ts from supraspheres where sphere_id = '"
				+ sphere + "' and message_id = " + message_id
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;

		ExtendedDocumentGetter documentGetter = new ExtendedDocumentGetter();
		safeExecuteQuery(query, documentGetter );		
		final String recId = documentGetter.getRecId();
		final Document dbDoc = documentGetter.getDbDoc();
		final String newdate = documentGetter.getMoment();
		final String timeStamp = documentGetter.getCreateTs();

		String isResponse;
		if (doc.getRootElement().element("response_id") != null) {
			isResponse = "1";
		} else {
			isResponse = "0";
		}

		int newIntTotal = 0;

		if (dbDoc != null) {
			if (increment != null) {
				if (dbDoc.getRootElement().element("interest") == null) {
					dbDoc.getRootElement().addElement("interest")
							.addAttribute("total", increment).addElement(
									"accrual").addAttribute("giver", real_name);
					newIntTotal = new Integer(increment).intValue();

				} else {

					dbDoc.getRootElement().element("interest").addElement(
							"accrual").addAttribute("giver", real_name);
					int total = new Integer(dbDoc.getRootElement().element(
							"interest").attributeValue("total")).intValue();
					total = total + new Integer(increment).intValue();
					newIntTotal = new Integer(total).intValue();
					String newTotal = new Integer(total).toString();
					dbDoc.getRootElement().element("interest").addAttribute(
							"total", newTotal);

				}
			}

			String newXmldata = this.utils.quote(dbDoc.asXML());

			query = "replace into supraspheres "
					+ " (RECID,XMLDATA,TYPE,SPHERE_ID,THREAD_TYPE,MOMENT,CREATE_TS,THREAD_ID,MESSAGE_ID,ISRESPONSE,USED,MODIFIED,TOTAL_ACCRUED) values ('"
					+ recId + "','" + newXmldata + "','" + type + "','" + sphere
					+ "','" + thread_type + "','" + newdate + "','" + timeStamp
					+ "','" + threadId + "','" + message_id + "','"
					+ isResponse + "','" + finalModified + "','"
					+ finalModified + "','" + newIntTotal + "')";

			safeExecuteUpdate(query);
			if (SupraSphereSingleton.INSTANCE.isSupraSphere(type)) {
				SupraSphereSingleton.INSTANCE.setOutdated();
			}
		}
		return dbDoc;
	}

	public boolean checkThreshold(Document doc, String sphere_id) {

		String decisive = getDecisiveMember(sphere_id);

		if (decisive == null || decisive.equals("__NOBODY__")) {
			decisive = "__NOBODY__";
			return true;
		}

		VotingEngine ve = new VotingEngine();
		boolean voted = false;

		if (ve.hasVoted(decisive, doc))
			voted = true;

		return voted;
	}

	public synchronized List<String> getAllSphereIds() throws SQLException {
		final List<String> sphereIds = new ArrayList<String>();
		final String query = "select distinct sphere_id from supraspheres where type = 'sphere'";
		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				sphereIds.add(rs.getString(1));
			}
		});
		return sphereIds;
	}

	/**
	 * Updates the xml document and modified and used with the current time/date
	 * 
	 * @param document
	 *            to be updated
	 * @param sphere
	 *            the document belongs to
	 * @return document that was passed in (unchanged)
	 */
	public synchronized Document replaceDoc(final Document document,
			String sphere) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Sphere is currently in replacedoc: " + sphere );
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				YYYY_MM_DD_HH_MM_SS);
		final String modified = dateFormatter.format( new Date() );
		final String used = modified;

		if (logger.isDebugEnabled()) {
			logger.info("replaceDoc new timeStamp = " + used);
		}
		ss.domainmodel.Statement statement = ss.domainmodel.Statement
				.wrap(document);

		final String messageId = statement.getMessageId();

		if (logger.isDebugEnabled()) {
			logger.debug("replaceDoc for sphere: " + sphere + " msgId = "
					+ messageId + " new xml = \n" + document.asXML());
		}

		final String query = "UPDATE supraspheres SET " + "XMLDATA = '"
				+ this.utils.quote(document.asXML()) + "', " + "USED = '"
				+ used + "', " + "MODIFIED = '" + modified + "' "
				+ "where sphere_id = '" + sphere + "' and message_id = '"
				+ messageId + "'";

		try {
			QueryExecutor.executeUpdate(query);
			if ( document != null && document.getRootElement() != null ) {
				if ( document.getRootElement().getName().equals( "suprasphere" ) ) {
					SupraSphereFacadeProvider.INSTANCE.registrySupraSphereChanges( SupraSphereProvider.INSTANCE.createVerifyAuth() );
					SupraSphereSingleton.INSTANCE.setOutdated();
				}
			}
			
		} catch (SQLException exc) {
			logger.error("SQL Exception in replaceDoc", exc);
		}

		try {
			SphereIndex sphereIndex = SphereIndex.get(sphere);
			sphereIndex.updateDoc(document);
		} catch (IOException e) {
			logger.error("", e);
		}
		return document;
	}
	
	public synchronized void moveDoc(final Document document,
			final String sourceSphereId, final String targetSphereId, final boolean isReindex) {
		if (logger.isDebugEnabled()) {
			logger.debug("Moving doc from " + sourceSphereId + " to " + targetSphereId);
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				YYYY_MM_DD_HH_MM_SS);
		final Date currentTime = new Date();
		final String modified = dateFormatter.format(currentTime);
		final String used = modified;

		if (logger.isDebugEnabled()) {
			logger.info("replaceDoc new timeStamp = " + used);
		}
		ss.domainmodel.Statement statement = ss.domainmodel.Statement
				.wrap(document);

		statement.setCurrentSphere(targetSphereId);
		
		final String messageId = statement.getMessageId();

		final String query = "UPDATE supraspheres SET " + "XMLDATA = '"
				+ this.utils.quote(document.asXML()) + "', " + "USED = '"
				+ used + "', " + "MODIFIED = '" + modified + "', " + " sphere_id = '" + targetSphereId + "' "
				+ "where sphere_id = '" + sourceSphereId + "' and message_id = '"
				+ messageId + "'";

		try {
			QueryExecutor.executeUpdate(query);
		} catch (SQLException exc) {
			logger.error("SQL Exception in replaceDoc", exc);
		}

		if (isReindex) {
			try {
				SphereIndex sphereIndexSource = SphereIndex.get(sourceSphereId);
				sphereIndexSource.removeDoc(document);
				SphereIndex sphereIndexTarget = SphereIndex.get(targetSphereId);
				sphereIndexTarget.addDoc(document);
			} catch (IOException e) {
				logger.error("Error in processing in lucene moving document", e);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param sphere
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */

	public Document removeDoc(final Document doc, String sphere)
			throws SQLException {
		if ( doc == null ) {
			logger.error( "Can't remove null document " + doc + " to " + sphere );
			return null;
		}
		// create table if doesn't exist
		createTable("recalled");
		
		IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection connection = null;
		String messageId = Statement.wrap(doc).getMessageId();
		try {
			connection = connectionProvider.getConnection();
			// prepare the select of the removed sphere
			StringBuffer selectBuffer = new StringBuffer();
			selectBuffer
					.append(
							"select sphere_id, recid, xmldata, type, moment, ")
					.append(
							"thread_type, create_ts, thread_id, message_id, isResponse, ")
					.append("used, modified, total_accrued ")
					.append(
							"from supraspheres where sphere_id = ? and message_id = ?");

			PreparedStatement selectStatement = connection
					.prepareStatement(selectBuffer.toString());

			selectStatement.setString(1, sphere);
			selectStatement.setString(2, messageId);

			// prepare the recall insert statement
			StringBuffer insertBuffer = new StringBuffer();
			insertBuffer.append("insert into recalled ").append(
					"(sphere_id, xmldata, type, moment, thread_type, ").append(
					"create_ts, thread_id, message_id, isResponse,").append(
					"used, modified) ").append("values ").append(
					"(?,?,?,?,?,?,?,?,?,?,?)");

			PreparedStatement insertStatement = connection
					.prepareStatement(insertBuffer.toString());


			// get column data of the removed sphere
			java.sql.ResultSet resultSet = selectStatement.executeQuery();

			// insert the removed sphere to the recalled table
			if (resultSet.next()) {

				insertStatement.setString(1, resultSet.getString("sphere_id"));
				insertStatement.setString(2, resultSet.getString("xmldata"));
				insertStatement.setString(3, resultSet.getString("type"));
				insertStatement.setTimestamp(4, resultSet
						.getTimestamp("moment"));
				insertStatement
						.setString(5, resultSet.getString("thread_type"));
				insertStatement.setTimestamp(6, resultSet
						.getTimestamp("create_ts"));
				insertStatement.setLong(7, Long.parseLong(resultSet
						.getString("thread_id")));
				insertStatement.setLong(8, Long.parseLong(resultSet
						.getString("message_id")));
				insertStatement.setInt(9, resultSet.getInt("isResponse"));
				insertStatement
						.setTimestamp(10, resultSet.getTimestamp("used"));
				insertStatement.setTimestamp(11, resultSet
						.getTimestamp("modified"));

				insertStatement.executeUpdate();

			}

			// delete the sphere from supraspheres table
			String deleteString = "delete from supraspheres where sphere_id = '"+sphere+"' and message_id='"+messageId+"'";
//			PreparedStatement deleteStatement = connection
//					.prepareStatement(deleteString);
//
//			int recId = resultSet.getInt("recId");
//			deleteStatement.setInt(1, recId);
//			deleteStatement.executeUpdate();
			safeExecuteDelete(deleteString);

			try {
				SphereIndex index = SphereIndex.get(sphere);
				index.removeDoc(doc);
			} catch (IOException e) {
				logger.error("", e);
			}
			// connection.commit();
			// connection.setAutoCommit(true);
		} catch (SQLException se) {
			logger.error("SQL Exception in removeDoc", se);
			// We can't rollbakc because autocommit is true
			// connection.rollback();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("Can't close connection", ex);
				}
			}
			DBPool.unbind(connectionProvider);
		}
		return doc;
	}

	class RecidAndMomentGetter implements IResultSetRowHandler {

		private String newMoment;

		private String recId;

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.server.domainmodel2.db.IRecordCollector#nextRecord(java.sql.ResultSet)
		 */
		public void handleRow(ResultSet rs) throws ResultSetRowHandlerException,
				SQLException {
			this.recId = rs.getString(1);
			this.newMoment = rs.getString(2);
		}

		public String getNewMoment() {
			return this.newMoment;
		}

		public String getRecId() {
			return this.recId;
		}

	}

	// TODO fix this method
	public Document removeSphereDocWithSystem(final Document doc,
			String sphereId, String systemName) {

		String type = doc.getRootElement().element("type").attributeValue(
				"value");
		String thread_type = doc.getRootElement().element("thread_type")
				.attributeValue("value");

		String query = "select RECID, moment from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and (type='sphere' and XMLDATA like '%<system_name value=\""
				+ systemName + "\"/>%')";

		logger.info("statement in replace!!! " + query);
		String newdate = null;

		RecidAndMomentGetter recidAndMomentGetter = new RecidAndMomentGetter();
		safeExecuteQuery(query, recidAndMomentGetter);
		final String rec_id = recidAndMomentGetter.getRecId();
		final String newMoment = recidAndMomentGetter.getNewMoment();

		logger.info("NEW MOMENT: " + newMoment);

		if (newMoment != null && (!newMoment.equals("0000-00-00 00:00:00"))) {

			newdate = newMoment;
			logger.warn("of course its null...never set: " + newdate);

		} else {
			logger.info("New Momeent was null");

			java.sql.Timestamp ts = null;
			try {
				long millis = System.currentTimeMillis();
				Long lng = new Long(millis);
				ts = new java.sql.Timestamp(lng.longValue());
				SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
				newdate = df2.format(ts);
				logger.info("new date: " + newdate);
			} catch (Exception e) {

			}
		}

		query = "select RECID, moment from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and (type='sphere' and XMLDATA like '%<system_name value=\""
				+ systemName + "\"/>%')";

		RecidAndMomentGetter recidAndMomentGetter2 = new RecidAndMomentGetter();
		safeExecuteQuery(query, recidAndMomentGetter2);
		// What now? TODO: fix this
		if (true) {
			throw new RuntimeException("Illegal implementation");
		}

		String timeStamp = null;

		long longnum = System.currentTimeMillis();
		String millis = (Long.toString(longnum));

		Long lng = new Long(millis);
		java.sql.Timestamp ts = new java.sql.Timestamp(lng.longValue());
		SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

		timeStamp = df2.format(ts);

		query = "insert into `"
				+ "recalled"
				+ "` (XMLDATA,TYPE,SPHERE,THREAD_TYPE,MOMENT,CREATE_TS) values ('"
				+ this.utils.quote(doc.asXML()) + "','" + type + "','"
				+ sphereId + "','" + thread_type + "','" + newdate + "','"
				+ timeStamp + "')";
		logger.info("STATEMENT 2: " + query);

		safeExecuteUpdate(query);
		query = "delete from `" + sphereId + "` where recid='" + rec_id + "'";
		safeExecuteUpdate(query);

		return doc;
	}

	public synchronized boolean createDatabase(String finalDBName) throws SQLException {
		final String query = "create database " + finalDBName;
		final int created = QueryExecutor.executeUpdate(query);
		if (logger.isDebugEnabled()) {
			logger.debug("In createDatabase result is: " + created);
		}
		return (created == 1);
	}

	/**
	 * @param query
	 * @return
	 */
	public int safeExecuteUpdate(String query) {
		return QueryExecutor.safeExecuteUpdate(query);
	}
	
	public boolean safeExecuteDelete(String query) {
		try {
			return QueryExecutor.executeDelete(query);
		} catch (SQLException ex) {
			return false;
		}
	}

	public synchronized boolean createTable(String tableName) {
		StringBuffer SQLBuffer = new StringBuffer();

		SQLBuffer
				.append("create table if not exists " + tableName + " (")
				.append(" sphere_id varchar(50) not null,")
				.append(" recid int unsigned auto_increment primary key,")
				.append(" xmldata mediumtext not null,")
				.append(" type varchar(100) not null,")
				.append(" moment datetime not null,")
				.append(" thread_type varchar(100) not null,")
				.append(
						" create_ts timestamp not null default current_timestamp,")
				.append(" thread_id bigint not null,").append(
						" message_id bigint not null,").append(
						" isResponse int(1) unsigned not null DEFAULT 0,")
				.append(" used datetime DEFAULT null,").append(
						" modified datetime not null,").append(
						" total_accrued int unsigned not null DEFAULT 0)");

		int created = safeExecuteUpdate(SQLBuffer.toString());
		return created == 1 ? true : false;
	}

	public static void main(String[] args) {
		XMLDB xmldb = new XMLDB();
		xmldb.convertor.main(args);
	}

	/**
	 * Description of the Method
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param sphere
	 *            Description of the Parameter
	 */
	public synchronized void insertDoc(final Document doc, String sphere) {
		insertDoc(doc, sphere, true);
	}

	public synchronized void insertDoc(final Document doc, String sphere, boolean isToIndex) {
		if (doc == null) {
			logger.error("Can't insert null document");
			return;
		}
		ss.domainmodel.Statement statement = ss.domainmodel.Statement.wrap(doc);
		int isResponse = 0;
		if (statement.getResponseId() != null) {
			isResponse = 1;
		}

		String type = statement.getType();
		String threadType = statement.getThreadType();
		String threadId = null;

		if (statement.getThreadId() != null) {
			threadId = statement.getThreadId();
			if (statement.getResponseId() != null) {
				String responseID = statement.getResponseId();

				Document parentDoc = this.getSpecificID(sphere, responseID);
				if (parentDoc != null) {
					ss.domainmodel.Statement parentSt = ss.domainmodel.Statement
							.wrap(parentDoc);
					threadId = parentSt.getThreadId();
					if (statement.getMessageId() == null) {
						statement.setMessageId(VariousUtils.createMessageId());
					}
					statement.setThreadId(threadId);
				}

			}
		} else {
			logger.info("thread id it was nulll");
			if (statement.getResponseId() != null) {
				String responseID = statement.getResponseId();
				if (responseID != null) {
					Document parentDoc = this.getSpecificID(sphere, responseID);
					ss.domainmodel.Statement parentSt = ss.domainmodel.Statement
							.wrap(parentDoc);
					threadId = parentSt.getThreadId();
					if (statement.getMessageId() == null) {
						statement.setMessageId(VariousUtils.createMessageId());
					}
					statement.setThreadId(threadId);
				}
			} else {
				if (statement.getMessageId() == null) {
					statement.setMessageId(VariousUtils.createMessageId());
				}

				threadId = statement.getMessageId();
				logger.info("add thread id: " + threadId);
				statement.setThreadId(threadId);

				if (statement.getOriginalId() == null) {
					statement.setOrigBody(threadId);
				}
			}
		}

		String moment;
		if (statement.getLastUpdated() != null) {
			moment = statement.getLastUpdated();
		} else {
			moment = this.utils.getCurrentMoment();
			statement.setLastUpdated(moment);
		}

		long longnum = System.currentTimeMillis();
		String millis = (Long.toString(longnum));

		String messageId = statement.getMessageId();
		Long lng = new Long(millis);

		java.sql.Timestamp ts = new java.sql.Timestamp(lng.longValue());
		SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		String newdate = df2.format(ts);

		String xmldata = this.utils
				.quote(statement.getBindedDocument().asXML());

		String query = "insert into supraspheres "
				+ "(SPHERE_ID,XMLDATA,TYPE,THREAD_TYPE,MOMENT,CREATE_TS,THREAD_ID,MESSAGE_ID,ISRESPONSE,MODIFIED) "
				+ "values ('" + sphere + "', '" + xmldata + "', '" + type
				+ "', '" + threadType + "', '" + newdate + "', '"
				+ ts.toString() + "', " + threadId + ", " + messageId + ", "
				+ isResponse + ", '" + newdate + "')";

		if (SupraSphereSingleton.INSTANCE.isSupraSphere(type)) {
			SupraSphereSingleton.INSTANCE.setOutdated();
		}
		
		if (statement.isSphere()) {
			if ( StringUtils.isBlank( statement.getCurrentSphere() ) ) {
				statement.setCurrentSphere(sphere);
			}
		}
		
		synchronized (getWriteLock()) {
			try {
				QueryExecutor.executeUpdate(query);
			} catch (SQLException ex) {
				logger.error("Can't insertDoc. Query " + query, ex);
			}
		}
		if (isToIndex) {
			try {
				logger.warn("sphere:"+sphere);
				SphereIndex index = SphereIndex.get(sphere);
				index.addDoc(doc);
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

	public synchronized void insertCopy(
			final ss.domainmodel.Statement statement, String sphereId) {
		if (statement == null) {
			logger.error("Statement in null");
		}
		final Date now = new Date();
		statement.setMoment(now);
		statement.setLastUpdated(now);
		statement.setMessageId(getNextTableId());
		insertDoc(statement.getBindedDocument(), sphereId);
	}

	/**
	 * Gets the specificID attribute of the XMLDB object
	 * 
	 * @param type
	 *            Description of the Parameter
	 * @param message_id
	 *            Description of the Parameter
	 * @return The specificID value
	 */
	public synchronized Document getSpecificID(String sphere_id,
			String message_id) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and message_id = "
				+ message_id
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		final Document doc = safeQueryFirstDocument(query);
		if (doc == null) {
			logger.error("DOC NULL in get specific: " + query);
		}

		return doc;
	}

	public synchronized Document getSpecificMessage(String message_id) {
		final String query = "select XMLDATA from supraspheres where message_id = "
				+ message_id + DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocument(query);
	}

	public synchronized Document getSpecificMessage(String message_id,
			String sphere_id) {
		final String query = "select XMLDATA from supraspheres where message_id = '"
				+ message_id
				+ "' and sphere_id = '"
				+ sphere_id + "'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		final Document doc = safeQueryFirstDocument(query);
		if (doc == null) {
			logger.error("DOC NULL in get specific: " + query);
		}
		return doc;
	}

	/**
	 * Gets the specificID attribute of the XMLDB object
	 * 
	 * @param type
	 *            Description of the Parameter
	 * @param message_id
	 *            Description of the Parameter
	 * @return The specificID value
	 */
	public synchronized Document[] getSupraSearchView(String sphere_id,
			String message_id) {
		final int rangeSize = 25;
		final Vector<Document> docs = new Vector<Document>();
		String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and create_ts < (select create_ts from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and message_id = '"
				+ message_id
				+ "') and type in ('message', '"
				+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
				+ "', 'file', '"
				+ SupraXMLConstants.TYPE_VALUE_RESULT
				+ "', '"
				+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
				+ "', '"
				+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
				+ "','bookmark', 'comment', 'keywords',"
				+ " 'terse', 'edit', 'reply', 'library', 'contact', 'sphere', 'rss') Order by create_ts DESC limit "
				+ rangeSize + ";";
		docs.addAll(safeQueryDocumentList(query));
		//TODO: ask why we need it? (originaly was is adding to begin in loop)
		Collections.reverse(docs);		
		final String query2 = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and create_ts >= (select create_ts from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and message_id = '"
				+ message_id
				+ "') and type in ('message', '"
				+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
				+ "', 'file', '"
				+ SupraXMLConstants.TYPE_VALUE_RESULT
				+ "', '"
				+ SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
				+ "', '"
				+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
				+ "', 'bookmark', 'comment', 'keywords',"
				+ " 'terse', 'edit', 'reply', 'library', 'contact', 'sphere', 'rss') Order by create_ts limit "
				+ (rangeSize + 1) + ";";
		docs.addAll(safeQueryDocumentList(query2));
		
		return docs.toArray(new Document[]{});
	}

	public synchronized void addQueryToContact(String sphereId,
			String loginName, final Element keywordElement) {
		updateContactDoc(sphereId, loginName, new IDocumentHandler() {
			public void handleDocument(Document contactDocument) {
				contactDocument.getRootElement().add(
						keywordElement.createCopy());
			}
		});
	}


	private Document updateContactDoc(String sphereId, String loginName,
			IDocumentHandler updater) {
		Document contactDocument = getContactDoc(sphereId, loginName);
		if (contactDocument != null) {
			updater.handleDocument(contactDocument);
			return replaceDoc(contactDocument, sphereId);
		} else {
			return null;
		}
	}

	public synchronized void addEventToMessage(Hashtable session,
			String messageId, Element event) {
		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ (String) session.get("sphere_id")
				+ "' and message_id = '"
				+ messageId + "'" + DbUtils.SUFFIX_FOR_TOP_RECORD;
		Document doc = safeQueryFirstDocument(statement);
		if (doc == null) {
			logger.info("DOC NULL in addQueryToContact: " + statement);
		} else {
			Element events = doc.getRootElement().element("events");
			if (events == null) {
				doc.getRootElement().addElement("events").add(event);
			} else {
				doc.getRootElement().element("events").add(event);
			}

			replaceDoc(doc, (String) session.get("sphere_id"));
		}
	}

	public synchronized Document addInviteToContact(Hashtable session,
			String contactMessageId, String inviteSphereId,
			String inviteSphereName, String inviteSSName,
			String inviteSphereType) {

		logger.info("STarting addinvitetocontact: " + contactMessageId + " , "
				+ inviteSphereId + ", " + inviteSphereName);
		String inviterName = (String) session.get("real_name");
		String inviterUsername = (String) session.get("username");
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ (String) session.get("sphere_id")
				+ "' and message_id = '"
				+ contactMessageId + "'" + DbUtils.SUFFIX_FOR_TOP_RECORD;
		Document doc = safeQueryFirstDocument(query);
		if (doc == null) {
			logger.info("DOC NULL in addQueryToContact: " + query);
		} else {
			Date current = new Date();
			String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
					current)
					+ " "
					+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(
							current);
			Element existingInvite = doc.getRootElement().element(
					"active_invitation");
			if (existingInvite != null) {

				logger
						.info("Already has an invitation....remove all references to user in spheres and suprasphere");
				doc.getRootElement().remove(existingInvite);

			}
			doc.getRootElement().addElement("active_invitation").addAttribute(
					"inviter", inviterName).addAttribute("moment", moment)
					.addAttribute("invite_sphere_name", inviteSphereName)
					.addAttribute("invite_sphere_id", inviteSphereId)
					.addAttribute("invite_supra_sphere_name", inviteSSName)
					.addAttribute("inviter_username", inviterUsername)
					.addAttribute("invite_sphere_type", inviteSphereType);

			String existingLogin = doc.getRootElement().element("login")
					.attributeValue("value");

			boolean existsAndNotBlank = false;

			if (existingLogin != null) {
				if (existingLogin.length() > 0) {

					if (!existingLogin.equals(doc.getRootElement().element(
							"message_id").attributeValue("value"))) {
						existsAndNotBlank = true;
					}

				}

			}

			if (!existsAndNotBlank) {
				doc.getRootElement().element("login").addAttribute("value",
						contactMessageId);
			}

			Element events = doc.getRootElement().element("events");
			if (events == null) {

				doc.getRootElement().addElement("events").addElement("invited")
						.addAttribute("description", "Invitation sent")
						.addAttribute("giver", inviterName).addAttribute(
								"moment", moment).addAttribute("status",
								"pending");

			} else {

				doc.getRootElement().element("events").addElement("invited")
						.addAttribute("description", "Invitation sent")
						.addAttribute("giver", inviterName).addAttribute(
								"moment", moment).addAttribute("status",
								"pending");

			}

			replaceDoc(doc, (String) session.get("sphere_id"));

		}
		return doc;

	}

	public synchronized Vector getKeywords(String homeSphereId ) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
			+ homeSphereId + "' and type='keywords'";
		if (logger.isDebugEnabled()) {
			logger.debug( "getKeywords by " + query	);
		}
		return new Vector<Document>(safeQueryDocumentList(query));
	}
	
	public synchronized Vector<Document> getKeywords( final List<String> sphereIds ) {

		final StringBuilder sphereList = new StringBuilder();
		for (String sphereId : sphereIds){
			if (sphereList.length() > 0){
				sphereList.append(",");
			}
			sphereList.append("'" + sphereId + "'");
		}
		final StringBuilder query = new StringBuilder();
		query.append( "select XMLDATA from supraspheres where sphere_id in (" );
		query.append( sphereList );
		query.append( ") and type='keywords'" );
		query.append( "  ORDER BY `used` DESC" );

		if (logger.isDebugEnabled()) {
			logger.debug( "getKeywords by " + query	);
		}
		return new Vector<Document>(safeQueryDocumentList(query.toString()));
	}
	
	public synchronized Vector<Date> getUseDates( List<String> messagesIds ){
		final Vector<Date> dates = new Vector<Date>();
		if ((messagesIds == null) || (messagesIds.isEmpty())){
			return dates;
		}
		
		final StringBuilder messagesList = new StringBuilder();
		for (String messagesId : messagesIds){
			if (messagesList.length() > 0){
				messagesList.append(",");
			}
			messagesList.append("'" + messagesId + "'");
		}
		final StringBuilder query = new StringBuilder();
		query.append( "select USED from supraspheres where message_id in (" );
		query.append( messagesList );
		query.append( ") and type='keywords'" );
		query.append( "  ORDER BY `used` DESC" );

		if (logger.isDebugEnabled()) {
			logger.debug( "get Dates by " + query );
		}
		safeExecuteQuery(query.toString(), new IResultSetRowHandler(){
			public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
				String usedString = rs.getString(1);
				Date tagDate = DateTimeParser.INSTANCE.parseToDate(
						usedString);
				dates.add( tagDate );
			}
			
		});
		return dates;
	}
	
	public synchronized Vector<Document> getAllKeywords() {
		String query = "select XMLDATA from supraspheres where type='keywords'";
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public synchronized Vector<Document> getKeywords(String homeSphereId,
			String filter) {
		String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ homeSphereId + "' and type='keywords'";
		if (filter != null) {
			query += " and XMLDATA " + DbUtils.xmlValueAttributeStaringCondition( "subject", filter )
					+ " LIMIT 15";
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "getKeywords by " + query	);
		}
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	@SuppressWarnings("unchecked")
	public synchronized Vector<Element> getRecentQueriesFor(Hashtable session,
			String homeSphereId, String homeMessageId) {
		final Vector<Element> recentQueries = new Vector<Element>();
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ homeSphereId + "' and message_id = '" + homeMessageId + "'";

		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				try {
					final Document doc = DocumentHelper.parseText(rs
							.getString(1));

					
					String cname = null;

					if (doc.getRootElement().element("last_name")
							.attributeValue("value").length() > 0) {
						cname = doc.getRootElement().element("first_name")
								.attributeValue("value")
								+ " "
								+ doc.getRootElement().element("last_name")
										.attributeValue("value");
					} else {
						cname = doc.getRootElement().element("first_name")
								.attributeValue("value");
					}
					String apath = "//contact/keywords";
					try {
						recentQueries.addAll((List<Element>) doc.selectObject(apath));
					} catch (ClassCastException cce) {
						recentQueries.add((Element) doc.selectObject(apath));
					}
				} catch (DocumentException ex) {
					logger.error("getRecentQueriesFor failed. Query " + query,
							ex);
				}
			}
		});
		return recentQueries;
	}

	public synchronized Vector<Document> getBookmarksForHomeSphere(
			String homeSphereId, String filter) {
		String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ homeSphereId
				+ "' and type='bookmark' and (XMLDATA " + DbUtils.xmlValueAttributeStaringCondition( "address", filter)  
				+ " || XMLDATA " + DbUtils.xmlValueAttributeStaringCondition( "address", "http://" + filter) 
				+ " || XMLDATA " + DbUtils.xmlValueAttributeStaringCondition( "address", "http://www." + filter)
				+ " || XMLDATA " + DbUtils.xmlValueAttributeStaringCondition( "address", "www." + filter) 
				+ " ) limit 30";
		if (logger.isDebugEnabled()) {
			logger.debug( "getBookmarksForHomeSphere by " + query );
		}
		return new Vector<Document>(safeQueryDocumentList(query));
	}
	
	public synchronized ArrayList<String> getBookmarksAddresses(List<String> sphereIds, String filter){
		final ArrayList<String> addresses = new ArrayList<String>();
		if ((sphereIds == null)||(sphereIds.isEmpty())){
			logger.info("Statement in getBookmarksAddresses: Spheres set is empty");
			return addresses;
		}
		final StringBuilder sphereList = new StringBuilder();
		for (String sphereId : sphereIds){
			if (sphereList.length() > 0){
				sphereList.append(",");
			}
			sphereList.append("'" + sphereId + "'");
		}
		final StringBuilder query = new StringBuilder();
		query.append( "select XMLDATA from supraspheres where sphere_id in (" );
		query.append( sphereList );
		query.append( ") and type='bookmark'" );
		if ( filter != null && filter.length() > 0 ) {
			query.append( " and " );
			query.append( "( " );
			query.append( " XMLDATA " ).append( DbUtils.xmlValueAttributeStaringCondition( "address", BookmarkUtils.toHttpUrl( filter ) ) );
			query.append( " or " );
			query.append( "XMLDATA " ).append( DbUtils.xmlValueAttributeStaringCondition( "address", BookmarkUtils.toHttpWwwUrl( filter ) ) );
			query.append( ")" );
		}
		else {
			query.append( DbUtils.SUFFIX_FOR_MOST_USED_RECORDS );
		}
		query.append( " LIMIT 50" );
		logger.info("Statement in getBookmarksAddresses: " + query);
		List<Document> bookmarks = safeQueryDocumentList(query.toString());
		for (Document bookmark : bookmarks){
			addresses.add(BookmarkStatement.wrap(bookmark).getAddress());
		}
		return addresses;
	}

	public synchronized Vector<Document> getEmailsForSphereId(String sphereId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and type='"
				+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL + "' order by moment DESC";
		logger.info("Statement in getEmailsForSphereId: " + query);
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public synchronized Vector<Document> getRecentBookmarksFor(
			Hashtable session, String homeSphereId, String homeMessageId) {
		long longnum = System.currentTimeMillis();
		long days = 4 * 1000 * 60 * 60 * 24;
		long newlong = longnum - days;
		java.sql.Timestamp ts = new java.sql.Timestamp(newlong);
		SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		String newdate = df2.format(ts);
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ homeSphereId
				+ "' and type='bookmark' and moment > \""
				+ newdate + "\"";

		logger.info("Statement in getrecentbookmarks: " + query);
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public synchronized Hashtable<String, String> getLoginSaltAndVerifier(
			String supraSphereName, String userName, String sphereName) {

		Hashtable<String, String> salt_contact = new Hashtable<String, String>();

		String query;

		if (sphereName.equals(supraSphereName)) {
			query = "select XMLDATA from supraspheres where sphere_id = '"
					+ supraSphereName + "' and type='membership'";
		} else {
			query = "select XMLDATA from supraspheres where sphere_id = '"
					+ sphereName + "' and type='membership'";
		}

		for (Document document : safeQueryDocumentList(query)) {
			if ( logger.isDebugEnabled() ) {
				logger.debug("doc  : "+XmlDocumentUtils.toPrettyString(document));
			}
			if ((document.getRootElement().element("login_name")
					.attributeValue("value")).equals(userName)) {

				if (document.getRootElement().element("verifier") != null) {
					String verifier = document.getRootElement().element(
							"verifier").getText();
					salt_contact.put("verifier", verifier);
					String salt = document.getRootElement().element("verifier")
							.attributeValue("salt");

					salt_contact.put("salt", salt);
				} else {

					if (document.getRootElement().element("machine_verifier") != null) {
						String verifier = document.getRootElement().element(
								"machine_verifier").getText();
						salt_contact.put("verifier", verifier);
						String salt = document.getRootElement().element(
								"machine_verifier").attributeValue("salt");

						salt_contact.put("salt", salt);
					}

				}

				String contact = document.getRootElement().element(
						"contact_name").attributeValue("value");
				salt_contact.put("contact", contact);

				String changePassphraseNextLogin;
				if (document.getRootElement().element(
						"change_passphrase_next_login") != null) {
					changePassphraseNextLogin = "true";

				} else {
					changePassphraseNextLogin = "false";
				}
				
				String lockContact;
				if (document.getRootElement().element(
						"locked_up") != null) {
					lockContact = "true";
				} else {
					lockContact = "false";
				}

				salt_contact.put("changePassphraseNextLogin",
						changePassphraseNextLogin);
				salt_contact.put("contact_locked", lockContact);
			}
		}
		return salt_contact;
	}

	public synchronized Document getContactDoc(String sphereId, String login) {
		final String query = " select `xmldata` from supraspheres WHERE `sphere_id` = '"
				+ sphereId
				+ "' AND `type`='contact' "
				+ " AND `xmldata` "
				+ DbUtils.xmlValueAttributeCondition("login", login)
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		if (logger.isDebugEnabled()) {
			logger.debug("Selecting contact by " + query);
		}
		return safeQueryFirstDocument(query);
	}
	
	public synchronized Document getContactDocOnContactName(String sphereId, String contactName) {
		StringTokenizer tokenizer = new StringTokenizer( contactName, " ");
		final String firstName = tokenizer.nextToken();
		final String lastName = tokenizer.nextToken();
		final String firstNameXMLDATA = "'%<first_name value=\"" + firstName + "\"/>%'";
		final String lastNameXMLDATA = "'%<last_name value=\"" + lastName + "\"/>%'";
		String query = "select `xmldata` from supraspheres where type='contact' and `sphere_id` = '"
			+ sphereId +
			"' and XMLDATA like " + firstNameXMLDATA + " and XMLDATA like " + lastNameXMLDATA
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		if (logger.isDebugEnabled()) {
			logger.debug("Selecting contact by " + query);
		}
		return safeQueryFirstDocument(query);
	}

	/**
	 * @param query
	 * @return
	 */
	public Document safeQueryFirstDocument(String query) {
		return QueryExecutor.safeQueryFirstDocument(query);
	}

	public synchronized Document getMembershipDoc(String loginSphereName,
			String username) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ loginSphereName + "' and type='membership'";
		for (Document document : safeQueryDocumentList(query)) {
			if ((document.getRootElement().element("login_name")
					.attributeValue("value")).equals(username)) {
				return document;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized Hashtable getMachineSaltAndVerifier(String userName,
			String sphereName, String profileId) {

		final Hashtable salt_contact = new Hashtable();
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereName + "' and type='membership'";
		
		for (Document document : safeQueryDocumentList(query)) {
			Element element = document.getRootElement();
			Element loginElement = element.element("login_name");
			if (loginElement == null) {
				continue;
			}

			final String xmlUsername = loginElement.attributeValue("value");
			if (userName.equals(xmlUsername)) {

				String apath = "//membership/machine_verifier";
				Element machineVerifier = null;

				String changePassphraseNextLogin;
				if (document.getRootElement().element(
						"change_passphrase_next_login") != null) {
					changePassphraseNextLogin = "true";

				} else {
					changePassphraseNextLogin = "false";
				}
				
				String lockContact;
				if (document.getRootElement().element(
						"locked_up") != null) {
					lockContact = "true";
				} else {
					lockContact = "false";
				}

				try {

					Element one = (Element) document.selectObject(apath);
					if (one != null) {

						if (one.attributeValue("profile_id").equals(profileId)) {
							machineVerifier = one;
						}
					}
				} catch (ClassCastException cce) {
					Vector machines = new Vector((ArrayList) document
							.selectObject(apath));

					for (int i = 0; i < machines.size(); i++) {
						Element one = (Element) machines.get(i);
						if (one.attributeValue("profile_id").equals(profileId)) {
							machineVerifier = one;
						}
					}
				}

				if (machineVerifier != null) {

					final String salt = machineVerifier.attributeValue("salt");
					salt_contact.put("salt", salt);

					final String verifier = machineVerifier.getText();
					salt_contact.put("verifier", verifier);
					final String contact = document.getRootElement().element(
							"contact_name").attributeValue("value");
					salt_contact.put("contact", contact);
					salt_contact.put("changePassphraseNextLogin",
							changePassphraseNextLogin);
					salt_contact.put("contact_locked", lockContact);
				}
			}
		}
		return salt_contact;
	}

	public synchronized Hashtable<String, String> getMachineSaltAndVerifier(
			String userName, String loginSphereName) {
		final Hashtable<String, String> salt_contact = new Hashtable<String, String>();
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ loginSphereName + "' and type='membership'";
		for (Document document : safeQueryDocumentList(query)) {
			if ((document.getRootElement().element("login_name")
					.attributeValue("value")).equals(userName)) {
				final String salt = document.getRootElement().element(
						"machine_verifier").attributeValue("salt");
				salt_contact.put("salt", salt);
				final String verifier = document.getRootElement().element(
						"machine_verifier").getText();
				salt_contact.put("verifier", verifier);
				final String contact = document.getRootElement().element(
						"contact_name").attributeValue("value");
				salt_contact.put("contact", contact);
				String changePassphraseNextLogin;
				if (document.getRootElement().element(
						"change_passphrase_next_login") != null) {
					changePassphraseNextLogin = "true";
				} else {
					changePassphraseNextLogin = "false";
				}
				
				String contactLocked;
				if (document.getRootElement().element(
						"locked_up") != null) {
					contactLocked = "true";
				} else {
					contactLocked = "false";
				}
				
				salt_contact.put("changePassphraseNextLogin",
						changePassphraseNextLogin);
				salt_contact.put("contact_locked", contactLocked);
			}
		}
		return salt_contact;

	}

	/**
	 * Gets the emailAddress attribute of the XMLDB object
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @return The emailAddress value
	 */
	public synchronized String getEmailAddress(String sphere_id,
			String contact_name) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='contact'";

		final ContactEmailAccessor contactEmailAccessor = new ContactEmailAccessor(
				safeQueryDocumentList(query));
		Map<String, String> contactToEmail = contactEmailAccessor
				.getContactNameToEmailMap();
		return contact_name != null ? contactToEmail.get(contact_name) : null;
	}

	public synchronized Vector<Document> getContactsFromEmail(String sphere_id,
			String email) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and type='contact'"
				+ (email == null ? "" : " AND XMLDATA like '%" + email + "%'");
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public synchronized Hashtable<String, String> getPersonalContactsForSphere(
			String sphere_id) {
		final Hashtable<String, String> personalContacts = new Hashtable<String, String>();
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and (type='contact')";

		logger.info("Statement in getpersontalcontacts: " + query);

		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				Document document;
				try {
					document = DocumentHelper.parseText(rs.getString(1));
				} catch (DocumentException ex) {
					logger.error("getPersonalContactsForSphere failed. Query "
							+ query, ex);
					return;
				}

				String last_name = document.getRootElement().element(
						"last_name").attributeValue("value");
				String fullName;
				if (last_name.length() > 0) {
					fullName = document.getRootElement().element("first_name")
							.attributeValue("value")
							+ " "
							+ document.getRootElement().element("last_name")
									.attributeValue("value");
				} else {
					fullName = document.getRootElement().element("first_name")
							.attributeValue("value");
				}
				personalContacts.put(document.getRootElement().element("login")
						.attributeValue("value"), fullName);
			}
		});
		return personalContacts;
	}

	public synchronized Hashtable<String, String> getPersonalContactsForEmail(
			String sphere_id) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='contact'";
		logger.info("Statement in getpersontalcontacts: " + query);
		final ContactEmailAccessor contactEmailAccessor = new ContactEmailAccessor(safeQueryDocumentList(query));
		return contactEmailAccessor.getEmailToContactNameMap();
	}
	
	public synchronized Collection<String> getEmailsForContactsInSphere(
			String sphere_id) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='contact'";
		logger.info("Statement in getpersontalcontacts: " + query);
		Collection<String> emails = new ArrayList<String>();
		final ContactEmailAccessor contactEmailAccessor = new ContactEmailAccessor(
				safeQueryDocumentList(query));
		final Set<String> contactNames = contactEmailAccessor.getContactNameToEmailMap().keySet();
		if (contactNames == null) {
			logger.error("contactNames is null");
			return emails;
		}
		for ( String contactName : contactNames ) {
			String email = contactEmailAccessor.getContactNameToEmailMap().get(contactName);
			if (StringUtils.isNotBlank(email)){
				emails.add( SpherePossibleEmailsSet.provideWithDescriptionIfNeeded(email, contactName) );
			}			
		}
		return emails;
	}
	
	public synchronized Collection<String> getEmailsForMembersInSphere(
			final String sphereId) {
		
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
			+ sphereId + "' and type='contact'";

		final ContactEmailAccessor contactEmailAccessor = new ContactEmailAccessor(
			safeQueryDocumentList(query));
		final Hashtable<String, String> contactsEmails = contactEmailAccessor.getContactNameToEmailMap();
		if ((contactsEmails == null)||(contactsEmails.isEmpty())){
			logger.warn("Emails for contacts is empty");
			return null;
		}
		Collection<String> emails = new ArrayList<String>();
		ISupraSphereFacade supraSphere = getSupraSphere();
		for (MemberReference member : supraSphere.getMembersForSphere(sphereId) ){
			String contactName = member.getContactName();
			String email = contactsEmails.get(contactName);
			if (StringUtils.isNotBlank(email)){
				emails.add( SpherePossibleEmailsSet.provideWithDescriptionIfNeeded(email, contactName) );
			}
		}
		return emails;
	}
	
	public synchronized Collection<String> getEmailsForAllContacts() {
		final String query = "select XMLDATA from supraspheres where type='contact'";
		logger.info("Statement in getpersontalcontacts: " + query);
		Collection<String> emails = new ArrayList<String>();
		final ContactEmailAccessor contactEmailAccessor = new ContactEmailAccessor(
				safeQueryDocumentList(query));
		final Set<String> contactNames = contactEmailAccessor.getContactNameToEmailMap().keySet();
		if (contactNames == null) {
			logger.error("contactNames is null");
			return emails;
		}
		for ( String contactName : contactNames ) {
			String email = contactEmailAccessor.getContactNameToEmailMap().get(contactName);
			if (StringUtils.isNotBlank(email)){
				emails.add( SpherePossibleEmailsSet.provideWithDescriptionIfNeeded(email, contactName) );
			}			
		}
		return emails;
	}

	private static class ContactEmailAccessor {

		private final ArrayList<ContactStatement> contacts = new ArrayList<ContactStatement>();

		/**
		 * @param contactsDocs
		 */
		public ContactEmailAccessor(List<Document> contactsDocs) {
			super();
			for (Document document : contactsDocs) {
				this.contacts.add(ContactStatement.wrap(document));
			}
		}

		public Hashtable<String, String> getEmailToContactNameMap() {
			final Hashtable<String, String> map = new Hashtable<String, String>();
			for (ContactStatement contact : this.contacts) {
				final String fullName = contact
						.getContactNameByFirstAndLastNames();
				final String address = contact.getEmailAddress();
				if (StringUtils.isNotBlank(address)) {
					map.put(address, fullName);
				}
			}
			return map;
		}

		public Hashtable<String, String> getContactNameToEmailMap() {
			final Hashtable<String, String> map = new Hashtable<String, String>();
			for (ContactStatement contact : this.contacts) {
				final String fullName = contact
						.getContactNameByFirstAndLastNames();
				final String address = contact.getEmailAddress();
				if (StringUtils.isNotBlank(address)) {
					map.put(fullName, address);
				}
			}
			return map;
		}
	}

	public synchronized Document getMyContact(final String contact_name,
			String sphere_id) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='contact'";
		for (Document doc : safeQueryDocumentList(query)) {
			String cname;
			if (doc.getRootElement().element("last_name").attributeValue(
					"value").length() > 0) {
				cname = doc.getRootElement().element("first_name")
						.attributeValue("value")
						+ " "
						+ doc.getRootElement().element("last_name")
								.attributeValue("value");
			} else {
				cname = doc.getRootElement().element("first_name")
						.attributeValue("value");
			}
			if (cname.equals(contact_name)) {
				return doc;
			}
		}
		return null;
	}

	/**
	 * Gets the sphereCore attribute of the XMLDB object
	 * 
	 * @param userSession
	 *            Description of the Parameter
	 * @return The sphereCore value
	 */
	public synchronized Document checkForExistingContact(Document checkDoc) {
		logger.info("In checking: "
				+ checkDoc.getRootElement().element("login").attributeValue(
						"value"));
		String statement = "select XMLDATA from supraspheres "
				+ " where sphere_id = '"
				+ (String) getSession().get("supra_sphere")
				+ "' and type='contact' AND XMLDATA like '%<login value=\""
				+ checkDoc.getRootElement().element("login").attributeValue(
						"value") + "\"%' "
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocument(statement);
	}
	
	public synchronized Document getContactExists( final ContactStatement newContact, final String sphereId ) {
		if (logger.isDebugEnabled()) {
			logger.debug("Is contact exists: contactName: " + newContact.getContactNameByFirstAndLastNames() + ", SphereId: " + sphereId);
		}
		final String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId + "' and type='contact'";
		final List<Document> list = safeQueryDocumentList( statement );
		if (list == null) {
			return null;
		}
		for (Document contact : list) {
			ContactStatement st = ContactStatement.wrap( contact );
			if (StringUtils.equalsIgnoreCase(st.getFirstName(), newContact.getFirstName()) &&
					StringUtils.equalsIgnoreCase(st.getLastName(), newContact.getLastName())){
				return contact;
			}
		}
		return null;
	}

	/**
	 * public synchronized Document getSupraSphereDocument() throws
	 * DocumentException { // TODO ??? some really strange method Document
	 * document = null; // try to get the cached supra sphere document if //
	 * verifyAuth has already // been instaniated otherwise go to the database. //
	 * if (verifyAuth != null) { if (false) { document =
	 * verifyAuth.getSphereDocument();
	 * 
	 * if (document == null) { document = SupraSphereSingleton.get()
	 * .fetchSupraSphereDocument(); } } else { document =
	 * SupraSphereSingleton.get().fetchSupraSphereDocument(); }
	 * 
	 * return document; }
	 */
	
	public synchronized ISupraSphereFacade getSupraSphere() {
		if ( this.verifyAuth == null ) {
			this.verifyAuth = SupraSphereProvider.INSTANCE.createVerifyAuth();
		}
		else {
			SupraSphereProvider.INSTANCE.configureVerifyAuth(this.verifyAuth );
		}
		return SupraSphereFacadeProvider.INSTANCE.get( this.verifyAuth );
	}

	public synchronized ISupraSphereEditFacade getEditableSupraSphere() {
		return SupraSphereProvider.INSTANCE.getEditableSupraSphere( this );
	}
	
	/**
	 * Gets the subPresence attribute of the XMLDB object
	 * 
	 * @param supra_sphere_id
	 *            Description of the Parameter
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The subPresence value
	 */
	public synchronized Vector<String> getSubPresence(String supra_sphere_id,
			String sphere_id) {
		final Vector<String> result = new Vector<String>();

		// TODO DAVID000 6 is this querying the supra sphere
		// table or a individual table

		sphere_id = this.utils.replaceChars(sphere_id);
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ supra_sphere_id
				+ "' and type='sphere' AND XMLDATA like '%system_name=\""
				+ sphere_id + "\"%' " + DbUtils.SUFFIX_FOR_TOP_RECORD;

		logger.info("Statement in getSubPresence: " + query);
		final Document doc = safeQueryFirstDocument(query);

		logger.info("about to seelct object");
		String apath = "//sphere/member";

		if (doc == null) {
			logger.error("Document is null in getSubPresence");
		}
		else {
		try {
			Element elem = (Element) doc.selectObject(apath);
			if (elem == null) {
				logger.info("null element in getavailsphere");
			}
			logger.info("NAME: " + elem.attributeValue("name"));
			result.add(elem.attributeValue("contact_name"));
		} catch (ClassCastException npe) {
			List real = (ArrayList) doc.selectObject(apath);
			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);
				logger.info("NAME in getsubpresence in xmldb: "
						+ one.attributeValue("contact_name"));
				result.add(one.attributeValue("contact_name"));
			}
		}
		}
		logger.info("RETURNING SIZE: " + result.size());
		return result;
	}

	/**
	 * TODO:OPTIMIZATION_ISSUE: function take too much time ~~ 22 seconds with
	 * suprasphere 30 contacts.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Refactoring(classify=SupraSphereRefactor.class, message = "This method previosly implement very strange functionality")
	public synchronized Vector<String> getMembersWithAccessibleSphereCore(
			String supraSphereName, String username) {
		return getAllVisibleContacts(username);
	}

	/**
	 * @param username
	 * @return
	 */
	private Vector<String> getAllVisibleContacts(String username) {
		final Vector<String> result = new Vector<String>();
		for( SphereReference p2pSphere : getSupraSphere().getAllAvailablePrivateSpheres( username ) ) {
			result.add( p2pSphere.getDisplayName() );
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Refactoring(classify=SupraSphereRefactor.class, message = "This method previosly implement very strange functionality")
	public synchronized Vector<String> getMembersWithLoginSphere(
			String supraSphereName, String sphereId) {
		final Vector<String> result = new Vector<String>();
		for( MemberReference member : getSupraSphere().getMembersForSphere(sphereId) ) {
			result.add( member.getContactName() );
		}
		return result;
	}

	/**
	 * Description of the Method
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public synchronized Vector<String> selectMembers(String sphere_id) {
		// Select members ships that are also members
		final Vector<String> members = new Vector<String>();
		String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='membership'";

		for (Document document : safeQueryDocumentList(query)) {
			members.add(document.getRootElement().element("contact_name")
					.attributeValue("value"));
		}
		;
		return members;
	}

	/**
	 * @param query
	 * @param handler
	 */
	void safeExecuteQuery(String query, IResultSetRowHandler handler) {
		QueryExecutor.safeExecuteQuery(query, handler);
	}

	/**
	 * Gets the children attribute of the XMLDB object
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @param message_id
	 *            Description of the Parameter
	 * @return The children value
	 */
	public synchronized Vector<Document> getChildren(String sphere_id,
			String message_id) {
		Vector<Document> all = new Vector<Document>();
		try {
			final String query = "select XMLDATA from supraspheres where sphere_id = '"
					+ sphere_id
					+ "' and ((type='sphere'||type='message'||type='"
					+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
					+ "'||type='file'||type='"
					+ SupraXMLConstants.TYPE_VALUE_RESULT
					+ "'|| type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE
					+"' || type='bookmark'"
					+ "||type='comment'||type='edit'||type='terse'||type='reply'"
					+ "||type='library'||type='contact'||type='"
					+ SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
					+ "') AND XMLDATA like '%" + message_id + "%')";

			for (Document document : safeQueryDocumentList(query)) {

				document.getRootElement().addElement("current_sphere")
						.addAttribute("value", sphere_id);

				Element response = document.getRootElement().element(
						"response_id");

				if (response != null) {

					String response_check = response.attributeValue("value");
					if (message_id.equals(response_check)) {

						document.getRootElement().addElement("current_sphere")
								.addAttribute("value", sphere_id);
						all.add(document);
					}
				}
			}
		} catch (NullPointerException exc) {
			logger.error("NPE exception", exc);
		}
		return all;

	}

	/**
	 * Gets the parentDoc attribute of the XMLDB object
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The parentDoc value
	 */
	public synchronized Document getParentDoc(Document doc, String sphere_id) {
		String response_id = null;
		try {
			response_id = doc.getRootElement().element("response_id")
					.attributeValue("value");
		} catch (Exception exc) {
			logger.error("Exception in getParentDoc", exc);
		}
		String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and ((type='sphere'||type='message'||type='"
				+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
				+ "'||type='file'||type='"
				+ SupraXMLConstants.TYPE_VALUE_RESULT
				+ "'|| type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE+"' || type='bookmark'"
				+ "||type='comment'||type='edit'||type='terse'||type='reply'||type='library'||type='contact'"
				+ "||type='" + SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE
				+ "') AND " + "message_id = '" + response_id + "')";

		Document thedoc = null;
		for (Document document : safeQueryDocumentList(query)) {
			try {
				String test_id = document.getRootElement()
						.element("message_id").attributeValue("value");
				if (test_id.equals(response_id)) {
					document.getRootElement().addElement("current_sphere")
							.addAttribute("value", sphere_id);
					thedoc = document;
				}
			} catch (Exception de) {
				// SKIP
			}
		}
		return thedoc;
	}

	/**
	 * Gets the allSpheres attribute of the XMLDB object
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The allSpheres value
	 */
	public synchronized Vector<Document> getAllSpheres() {
		final HashMap<String,Document> idToSphere = new HashMap<String,Document>();
		final String query = "select XMLDATA, sphere_id from supraspheres where type='sphere' ORDER BY create_ts";
		safeExecuteQuery(query, new IResultSetRowHandler() {
			
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				try {
					final Document doc = DocumentHelper.parseText(rs.getString(1));
					final String docSphereId = rs.getString(2);
					SphereStatement sphere = SphereStatement.wrap( doc );
					if ( sphere.getSystemName() == null ) {
						logger.error( "Invalid sphere definition " + sphere );
						return;
					}
					// If definition self or definition is not yet resolved
					if ( docSphereId.equals( sphere.getSystemName() ) ||
					     !idToSphere.containsKey( sphere.getSystemName() ) ) {
						checkThatDoesNotContains( sphere.getSystemName() );
						sphere.setCurrentSphere(docSphereId);
						idToSphere.put( sphere.getSystemName(), sphere.getBindedDocument() );
					}
					else {
						reportAmbigousSphereDefinition(sphere);
					}
				} catch (DocumentException exc) {
					logger.error("document exception in getAllSpheres", exc);
				}
			}

			/**
			 * @param sphere
			 */
			private void reportAmbigousSphereDefinition(SphereStatement sphere) {
				// Ignore email box because it is does not have SphereCoreId
				if ( sphere.isEmailBox() ) {
					return;
				}
				// Ignore if definition placed in core sphere
				if ( sphere.getSphereCoreId() != null &&   
					 sphere.getSphereCoreId().equals( sphere.getCurrentSphere() ) ) {
					return;
				}
				if ( logger.isDebugEnabled() ) {
					logger.debug( "Found ambigous sphere definition " + sphere );
				}
			}

			/**
			 * @param idToSphere
			 * @param sphere
			 */
			private void checkThatDoesNotContains( String sphereId ) {
				Document existedSphereDoc = idToSphere.get( sphereId );
				if ( existedSphereDoc != null ) {
					SphereStatement existedSphere = SphereStatement.wrap( existedSphereDoc );
					reportAmbigousSphereDefinition(existedSphere);
				}
			}
		});
		return new Vector<Document>( idToSphere.values() );
	}

	/**
	 * @param sphereId
	 * @param uniqueId
	 * @return
	 */
	public Document getKeywordsWithUnique(String sphereId, String uniqueId) {
		final String sphereCondition = StringUtils.isBlank(sphereId) ? "" : "sphere_id = '"+sphereId+"' and";
		final String query = "select XMLDATA from supraspheres where "+sphereCondition+" type='keywords' and XMLDATA like '%<unique_id value=\""
				+ uniqueId + "\"%' " + DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocumentAndSetUpCurrentSphere(query, sphereId);
	}

	/**
	 * @param sphereId
	 * @param queryText
	 * @return
	 */
	public Document getExistingQuery(String sphereId, String queryText) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and type='keywords' and XMLDATA like '%<subject value=\""
				+ queryText + "\"%' " + DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocumentAndSetUpCurrentSphere(query, sphereId);
	}

	/**
	 * @param sphereId
	 * @param query
	 * @return
	 */
	private Document safeQueryFirstDocumentAndSetUpCurrentSphere(String query,
			String sphereId) {
		Document doc = safeQueryFirstDocument(query);
		if (doc != null) {
			ss.domainmodel.Statement statement = ss.domainmodel.Statement
					.wrap(doc);
			statement.setCurrentSphere(sphereId);
		}
		return doc;
	}

	/**
	 * @param uniqueId
	 */
	public Vector<Document> findAssetsInSameConceptSet(String sphereId,
			String uniqueId, Vector messageIdsToExclude) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and (type!='keywords' AND XMLDATA like '%<interest><keywords%unique_id=\""
				+ uniqueId + "\"%' ) ORDER BY create_ts";

		logger.info("Statement when finding same: " + query);
		Vector<Document> results = new Vector<Document>();
		for (Document doc : safeQueryDocumentList(query)) {
			String id = doc.getRootElement().element("message_id")
					.attributeValue("value");

			if (!VariousUtils.vectorContains(id, messageIdsToExclude)) {
				doc.getRootElement().addElement("multi_loc_sphere")
						.addAttribute("value", sphereId);
				results.add(doc);
			}

		}
		return results;
	}

	public Vector<Document> findURLSbyTag(String sphereId, String uniqueId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and (type='bookmark' AND XMLDATA like '%<interest><keywords%unique_id=\""
				+ uniqueId + "\"%' ) ORDER BY create_ts";

		logger.info("Statement when finding same: " + query);
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public Vector<Document> getAllContactsForMembers(String sphereId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId + "' and type='contact'";
		return new Vector<Document>(safeQueryDocumentList(query));
	}
	
	public Vector<Document> getAllContacts() {
		String query = "select XMLDATA from supraspheres where type='contact'";
		if (getVerifyAuth() != null) {
			String spheres = getVerifyAuth().getFormattedStringWithAllSpheresIncludedForSQLQuery();
			if (StringUtils.isNotBlank(spheres)) {
				query += " and sphere_id in (" + spheres + ")";
			}
		} else {
			logger.error("VerifyAuth is null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Query: " + query);
		}
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public Vector<Document> getRecentContacts(String sphereId) {

		Integer conv = new Integer("4");

		int expdays = (conv.intValue() * 1000 * 60 * 60 * 24);

		long explong = System.currentTimeMillis();
		long startMoment = explong;
		long endMoment = startMoment - expdays;

		Long endMomentLong = new Long(endMoment);
		Long startMomentLong = new Long(startMoment);

		java.sql.Timestamp tsStart = new java.sql.Timestamp(startMomentLong
				.longValue());
		java.sql.Timestamp tsEnd = new java.sql.Timestamp(endMomentLong
				.longValue());

		SimpleDateFormat df2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		String newStart = df2.format(tsStart);
		String newEnd = df2.format(tsEnd);
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and (\""
				+ newStart
				+ "\" > moment and moment > \""
				+ newEnd
				+ "\") AND ("
				+ "type='contact'" + ") ORDER BY create_ts;";
		logger.warn("query: " + query);
		final Vector<Document> recentContacts = new Vector<Document>(
				safeQueryDocumentList(query));
		logger.warn("returning recent..." + recentContacts.size());
		return recentContacts;
	}
	
	public Vector<Document> getLastFiveContacts(String sphereId) {
		
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId
				+ "' and ("
				+ "type='contact'" + ") ORDER BY USED DESC LIMIT 5;";
		logger.warn("query: " + query);
		final Vector<Document> recentContacts = new Vector<Document>(
				safeQueryDocumentList(query));
		logger.warn("returning recent..." + recentContacts.size());
		return recentContacts;
	}


	/**
	 * @param query
	 */
	List<Document> safeQueryDocumentList(String query) {
		final List<Document> result = new ArrayList<Document>();
		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				String xmlText = rs.getString(1);
				try {
					Document doc = DocumentHelper.parseText( xmlText );
					result.add(doc);
				} catch (DocumentException ex) {
					logger.error( "Can't parse Xml: " + xmlText, ex);
				}
			}
		});
		return result;
	}
	
	public Document getStatisticsDoc(String sphereCore, String sphereId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereCore
				+ "' and type='stats' and XMLDATA like '%last_launched sphere_id=\""
				+ sphereId + "%'" + DbUtils.SUFFIX_FOR_TOP_RECORD;
		logger.info("STATEMENT: " + query);
		return safeQueryFirstDocument(query);
	}

	@SuppressWarnings("unchecked")
	public void setMarkForSphere(String personalSphere, String sphereId,
			String realName, String username, String localOrGlobal) {

		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ personalSphere
				+ "' and type='stats' and XMLDATA like '%last_launched sphere_id=\""
				+ sphereId + "%' " + DbUtils.SUFFIX_FOR_TOP_RECORD;

		logger.info("statement in setmark: " + query);

		final Document statisticsDoc = safeQueryFirstDocument(query);

		String moment = this.utils.getCurrentMoment();

		if (statisticsDoc != null) {
			if (localOrGlobal.equals("local")) {
				statisticsDoc.getRootElement().addElement("mark").addAttribute(
						"moment", moment)
						.addAttribute("contact_name", realName).addAttribute(
								"username", username);

				if (statisticsDoc.getRootElement().element("last_mark") != null) {
					statisticsDoc.getRootElement().element("last_mark")
							.detach();
					statisticsDoc.getRootElement().addElement("last_mark")
							.addAttribute("moment", moment).addAttribute(
									"contact_name", realName).addAttribute(
									"username", username);

				} else {

					statisticsDoc.getRootElement().addElement("last_mark")
							.addAttribute("moment", moment).addAttribute(
									"contact_name", realName).addAttribute(
									"username", username);

				}

				if (statisticsDoc.getRootElement().element("since_local_mark") != null) {
					// statisticsDoc.getRootElement().element("since_local_mark").detach();
					Vector messages = new Vector(statisticsDoc.getRootElement()
							.element("since_local_mark").elements());
					for (int i = 0; i < messages.size(); i++) {
						Element one = (Element) messages.get(i);
						one.detach();

					}

					statisticsDoc.getRootElement().element("since_local_mark")
							.addAttribute("replies_to_mine", "0");
					statisticsDoc.getRootElement().element("since_local_mark")
							.addAttribute("since_mark", "0");

				} else {

					statisticsDoc.getRootElement().addElement(
							"since_local_mark").addAttribute("total_in_sphere",
							new Integer(countDocs(sphereId)).toString())
							.addAttribute("replies_to_mine", "0").addAttribute(
									"since_mark", "0");

				}

			} else {
				// Its global
				statisticsDoc.getRootElement().addElement("global_mark")
						.addAttribute("moment", moment).addAttribute(
								"contact_name", realName).addAttribute(
								"username", username);

				if (statisticsDoc.getRootElement().element("last_global_mark") != null) {
					statisticsDoc.getRootElement().element("last_global_mark")
							.detach();
					statisticsDoc.getRootElement().addElement(
							"last_global_mark").addAttribute("moment", moment)
							.addAttribute("contact_name", realName)
							.addAttribute("username", username);

				} else {

					statisticsDoc.getRootElement().addElement(
							"last_global_mark").addAttribute("moment", moment)
							.addAttribute("contact_name", realName)
							.addAttribute("username", username);

				}

				if (statisticsDoc.getRootElement().element("since_global_mark") != null) {
					// statisticsDoc.getRootElement().element("since_local_mark").detach();
					Vector messages = new Vector(statisticsDoc.getRootElement()
							.element("since_global_mark").elements());
					for (int i = 0; i < messages.size(); i++) {
						Element one = (Element) messages.get(i);
						one.detach();

					}

					statisticsDoc.getRootElement().element("since_global_mark")
							.addAttribute("replies_to_mine", "0");
					statisticsDoc.getRootElement().element("since_global_mark")
							.addAttribute("since_mark", "0");

				} else {

					statisticsDoc.getRootElement().addElement(
							"since_global_mark").addAttribute(
							"total_in_sphere",
							new Integer(countDocs(sphereId)).toString())
							.addAttribute("replies_to_mine", "0").addAttribute(
									"since_mark", "0");

				}

			}

		}
		if (statisticsDoc != null) {
			replaceDoc(statisticsDoc, personalSphere);
		}
	}

	public int countDocs(String sphereId) {
		final String query = "select count(recid) from supraspheres where sphere_id = '"
				+ sphereId+"'";
		return selectCount(query);
	}

	public int selectCount(String countQuery) {
		final AtomicInteger count = new AtomicInteger(0);
		safeExecuteQuery(countQuery, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				count.set(rs.getInt(1));
			}
		});
		return count.get();
	}

	public Vector<Document> getMembersForSphere(String sphereId) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphereId + "' and type='membership'";
		return new Vector<Document>(safeQueryDocumentList(query));
	}

	public Document makeCurrentSphereCore(String supraSphereName, String login,
			String sphereId, String sphereName, String sphereType) {
		return getEditableSupraSphere().makeCurrentSphereCore(supraSphereName, login,
				sphereId, sphereName, sphereType );
	}

	public XmldbUtils getUtils() {
		return this.utils;
	}

	public Convertor getConvertor() {
		return this.convertor;
	}

	/**
	 * @return the writeLock
	 */
	Object getWriteLock() {
		return writeLock;
	}

	/**
	 * @return the session
	 */
	public Hashtable getSession() {
		return this.session;
	}

	/**
	 * @param session2
	 * @param id
	 * @param loginSphere
	 * @return
	 */
	public Document removeSphereFromContactFavourites(Hashtable session,
			final String id, String loginSphere) {
		logger.info("start adding sphere to contact document");
		String login_name = (String) session.get("username");
		return updateContactDoc(loginSphere, login_name,
				new IDocumentHandler() {

					public void handleDocument(Document document) {
						ContactStatement contact = ContactStatement
								.wrap(document);

						for (FavouriteSphere fs : contact.getFavourites()
								.createIterableSnapshot()) {
							if (fs.getSystemName().equals(id)) {
								contact.getFavourites().remove(fs);
							}
						}

					}
				});
	}

	/**
	 * @param resultId
	 * @param currentSphere
	 * @return
	 */
	public Document getDocByMessageId(String messageId, String currentSphere) {
		final String query = "select XMLDATA from supraspheres where sphere_id = '"
				+ currentSphere
				+ "' AND message_id='"
				+ messageId
				+ "'"
				+ DbUtils.SUFFIX_FOR_TOP_RECORD;
		return safeQueryFirstDocument(query);
	}


	private static interface IDocumentHandler {
		void handleDocument(Document document);
	}

	/**
	 * 
	 */
	public void reIndexAllDocuments() {
		safeExecuteQuery("Select sphere_id,xmldata from supraspheres;", new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
			throws ResultSetRowHandlerException, SQLException {
				try {
					String sphere = rs.getString("sphere_id");
					Document doc = DocumentHelper.parseText(rs.getString("xmldata"));	
					try {			
						SphereIndex index = SphereIndex.get(sphere);
						index.addDoc(doc,true,false);
					} catch (IOException e) {
						logger.error("", e);
					}
				} catch (DocumentException ex) {
					ss.common.ExceptionHandler.handleException(this, ex);
				}
			}
		});		
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public ArrayList<String> getEmailsOfPossibleRecipients(final String sphereId) {
		if (logger.isDebugEnabled()) {
			logger.debug("get Emails of possible recipients performed");
		}
		final ArrayList<String> result = new ArrayList<String>();
		final ArrayList<String> addresses = new ArrayList<String>();
		
		final String login = (String) this.session
			.get(SessionConstants.USERNAME);
		final String personalSphereId = getVerifyAuth()
			.getPersonalSphereFromLogin(login);
		if (logger.isDebugEnabled()) {
			logger.debug("login name: " + login + ", personal sphereId: " + personalSphereId);
		}
		
		addresses.addAll(getEmailsOfPossibleRecipientsForSphere(sphereId));
		addresses.addAll(getEmailsOfPossibleRecipientsForSphere(personalSphereId));
		if (logger.isDebugEnabled()) {
			logger.debug("Not filtered addresses: " + addresses.size());
		}
		
		for (String str : addresses) {
			if (!result.contains(str)){
				result.add(str);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Returning filtered addresses: " + result.size());
		}
		return result;
	}
	
	private ArrayList<String> getEmailsOfPossibleRecipientsForSphere(final String sphereId){
		final ArrayList<String> addresses = new ArrayList<String>();
		try {
			if (sphereId != null) {
				final Vector<Document> emailDocumentsOfSphere = getEmailsForSphereId(sphereId);
				final Collection<String> contactsEmails = getEmailsForContactsInSphere(sphereId);
				if (emailDocumentsOfSphere != null) {
					for (Document doc : emailDocumentsOfSphere) {
						final ExternalEmailStatement st = ExternalEmailStatement
								.wrap(doc);
						final List<String> ccrecievers = SpherePossibleEmailsSet.parse(st.getCcrecievers());
						final List<String> bccrecievers = SpherePossibleEmailsSet.parse(st.getBccrecievers());
						final List<String> reciever = SpherePossibleEmailsSet.parse(st.getReciever());
						final List<String> giver = SpherePossibleEmailsSet.parse(st.getGiver());
						if (ccrecievers != null)
							addresses.addAll(ccrecievers);
						if (bccrecievers != null)
							addresses.addAll(bccrecievers);
						if (reciever != null)
							addresses.addAll(reciever);
						if (giver != null)
							addresses.addAll(giver);
					}
				}
				if (contactsEmails != null) {
					for (String str : contactsEmails) {
						addresses.add(str);
					}
				}
			}
		} catch (Throwable ex) {
			logger
					.error("Exception getting emails for sphere: " + sphereId,
							ex);
		}
		return addresses;
	}
	
	public void deleteSphereMessagesFromDataBase(Vector<String> spheres) {
		for(String sphereId : spheres) {
			String query = "DELETE FROM supraspheres WHERE sphere_id = '"+sphereId+"'";
			safeExecuteDelete(query);
		}
		for(String sphereId : spheres) {
			showSphereMessagesCount(sphereId);
		}
	}

	private void showSphereMessagesCount(final String id) {
		String query = "select xmldata from supraspheres where sphere_id = '"+id+"'";
		List<Document> doc = safeQueryDocumentList(query);
		logger.info("docs for "+id+" count : "+doc.size());
	}

	/**
	 * @param spheres
	 */
	public void conditionalDeleteSphereDefinitions(final Vector<String> spheres) {
		String query = "select xmldata, message_id, sphere_id from supraspheres where type='sphere'";
		
		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
				do {
					String sphereId = rs.getString("sphere_id");
					String messageId = rs.getString("message_id");
					Document sphereDoc;
					try {
						sphereDoc = DocumentHelper.parseText(rs.getString("xmldata"));
						SphereStatement sphere = SphereStatement.wrap(sphereDoc);
						
						if(spheres.contains(sphere.getSystemName())) {
							if(sphere.getSystemName().equals(sphereId)) {
								sphere.setDeleted(true);
								replaceDoc(sphere.getBindedDocument(), sphereId);
							} else {
								String removeQuery = "delete from supraspheres where type='sphere' and message_id='"+messageId+"' and sphere_id='"+sphereId+"'";
								safeExecuteDelete(removeQuery);
							}
						} else {
							boolean isReplace = false;
							for ( String idToDelete : spheres ) {
								ObjectRelation relation = sphere.getRelations().findBySphereId(idToDelete);
								if ( relation != null ) {
									sphere.getRelations().remove(relation);
									isReplace = true;
								}
							}
							if ( isReplace ) {
								replaceDoc(sphere.getBindedDocument(), sphereId);
							}
						}
					} catch (DocumentException ex) {
						logger.error("can't create document", ex);
					}	
				} while(rs.next());
			}
		});
		
/*		Set<String> messageIdsToRemove = new HashSet<String>();
		Set<String> systemNames = new HashSet<String>();
		
		for(Document doc : spheresDoc) {
			if(spheres.contains(SphereStatement.wrap(doc).getSystemName())) {
				SphereStatement sphere = SphereStatement.wrap(doc);
				sphere.setDeleted(true);
				replaceDoc(sphere.getBindedDocument(), sphere)
			}
		}*/
		
/*		String messageIdsSet = "(";
		String systemNameSet = "(";
		for(String messageId : messageIdsToRemove) {
			messageIdsSet += "'"+messageId+"',";
		}
		for(String systemName : systemNames) {
			systemNameSet += "'"+systemName+"',";
		}
		messageIdsSet = messageIdsSet.substring(0, messageIdsSet.length()-1);
		messageIdsSet += ")";
		systemNameSet = systemNameSet.substring(0, systemNameSet.length()-1);
		systemNameSet += ")";
		String removeQuery = "delete from supraspheres where message_id in "+messageIdsSet+" and sphere_id in "+systemNameSet;
		*/
		//logger.error(removeQuery);
		
/*		safeExecuteDelete(removeQuery);*/
		
//		safeExecuteQuery(query, new IResultSetRowHandler() {
//			public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
//				do {
//					logger.error("rs next : "+rs.getString("message_id")+" "+rs.getString("sphere_id"));
//				} while(rs.next());
//			}
//		});
		
	}
	
	public List<Element> findAllLocationsByMessageId(ss.domainmodel.Statement statement) {
		final String query = " select sphere_id, message_id from supraspheres WHERE message_id="
			+ DbUtils.quote( statement.getMessageId() );
		return getAllLocations(query);
	}

	/**
	 * @param contact
	 */
	public List<Element> findAllLocationsByContactName(ContactStatement contact) {
		final String firstName = contact.getFirstName();
		final String lastName = contact.getLastName();
		final String query = " select sphere_id, message_id from supraspheres WHERE " 
			+ " `type`='contact' "
			+ " AND `xmldata` "
			+ DbUtils.xmlValueAttributeCondition("first_name", firstName )
			+ " AND `xmldata` "
			+ DbUtils.xmlValueAttributeCondition("last_name", lastName );
		return getAllLocations(query);
	}

	/**
	 * @param query
	 * @return
	 */
	private List<Element> getAllLocations(final String query) {
		final List<Element> localtions = new ArrayList<Element>();
		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs) throws ResultSetRowHandlerException, SQLException {
				final SphereLocation location = new SphereLocation();
				location.setSystem( rs.getString( 1 ) );
				location.setMessage( rs.getString( 2 ) );
				localtions.add( location.getDocumentCopy().getRootElement() );
			}
		});
		return localtions;
	}
	
	public Hashtable<String, Document> getContactDocsInSpheresForContactName( final String contactName ){
		final Hashtable<String, Document> result = new Hashtable<String, Document>();
		StringTokenizer tokenizer = new StringTokenizer( contactName, " ");
		final String firstName = tokenizer.nextToken();
		final String lastName = tokenizer.nextToken();
		final String firstNameXMLDATA = "'%<first_name value=\"" + firstName + "\"/>%'";
		final String lastNameXMLDATA = "'%<last_name value=\"" + lastName + "\"/>%'";
		final String queryFist = "select sphere_id, xmldata from supraspheres where type='contact'" +
			" and XMLDATA like " + firstNameXMLDATA + " and XMLDATA like " + lastNameXMLDATA;
		safeExecuteQuery(queryFist, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
				throws ResultSetRowHandlerException, SQLException {
				String sphereId = rs.getString(1);
				String xml = rs.getString(2);
				try {
					Document doc = DocumentHelper.parseText(xml);
					result.put(sphereId, doc);
				} catch (DocumentException ex) {
					logger.error("Parse failed. Xml " + xml);
					throw new ResultSetRowHandlerException(ex);
				}
			}
		});
		return result;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public AbstractDocument getContactMessageIds(final String contactName) {
		final Document doc = DocumentHelper.createDocument();
		doc.add(DocumentHelper.createElement("root"));
		
		try {
			StringTokenizer tokenizer = new StringTokenizer( contactName, " ");
			final String firstName = tokenizer.nextToken();
			final String lastName = tokenizer.nextToken();
			final String firstNameXMLDATA = "'%<first_name value=\"" + firstName + "\"/>%'";
			final String lastNameXMLDATA = "'%<last_name value=\"" + lastName + "\"/>%'";
			String queryFist = "select message_id, sphere_id, thread_id from supraspheres where type='contact'" +
				" and XMLDATA like " + firstNameXMLDATA + " and XMLDATA like " + lastNameXMLDATA;
			safeExecuteQuery(queryFist, new IResultSetRowHandler() {
				public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
					String sphereId = rs.getString("sphere_id");
					String messageId = rs.getString("message_id");
					String threadId = rs.getString("thread_id");
					Element elem = DocumentHelper.createElement("element");
					elem.addAttribute("sphere_id", sphereId).addAttribute("message_id", messageId).
						addAttribute("thread_id", threadId);
					doc.getRootElement().add(elem);
				}
			});
			return (AbstractDocument)doc;
		} catch ( Exception ex ){
			logger.error("Could not get by tokens, will try second way", ex);
		}
		String query = "select xmldata, message_id, sphere_id from supraspheres where type='contact'";
		safeExecuteQuery(query, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				String docString = rs.getString("xmldata");
				String sphereId = rs.getString("sphere_id");
				String messageId = rs.getString("message_id");
				if(docString==null) {
					return;
				}
				Document document = null;
				try {
					document = DocumentHelper.parseText(docString);
					Statement statement = Statement.wrap(document);
					if(!statement.isContact()) {
						return;
					}
					ContactStatement contact = ContactStatement.wrap(document);
					if(!contact.getContactNameByFirstAndLastNames().equals(contactName)) {
						return;
					}
					Element elem = DocumentHelper.createElement("element");
					elem.addAttribute("sphere_id", sphereId).addAttribute("message_id", messageId);
					doc.getRootElement().add(elem);
				} catch (DocumentException ex) {
					ex.printStackTrace();
				}
			}
		});
		return (AbstractDocument)doc;
	}
	
	public Vector<Document> getAllNotesAboutContact(final VerifyAuth verifyAuth, final String contactName) {
		Vector<Document> notes = new Vector<Document>();
		Document doc = getContactMessageIds(contactName);
		if(doc==null) {
			return notes;
		}
		List<String> messageIds = new ArrayList<String>();
		Element root = doc.getRootElement();
		String inSpheresString = "";
		String inThreadsString = "";
		List<String> availableSpheres = verifyAuth.isAdmin() ? verifyAuth.getAllSpheres().toSpheresIds() : verifyAuth.getAvailableSpheres();
		for(Object el : root.elements("element")) {
			String sphereId = ((Element)el).attributeValue("sphere_id");
			String messageId = ((Element)el).attributeValue("message_id");
			String threadId = ((Element)el).attributeValue("thread_id");
			if(!verifyAuth.isAdmin() && !availableSpheres.contains(sphereId)) {
				continue;
			}
			inSpheresString += "'" + sphereId + "',";
			if ( threadId == null ) {
				threadId = messageId;
			}
			inThreadsString += "'" + threadId + "',";
			messageIds.add(messageId);
		}
		inSpheresString = inSpheresString.substring(0, inSpheresString.length()-1);
		inThreadsString = inThreadsString.substring(0, inThreadsString.length()-1);
		String query = "select xmldata from supraspheres where sphere_id in ("+ inSpheresString +")" +
				" and THREAD_ID in (" + inThreadsString + ") and type in ('terse','message') order by create_ts";
		List<Document> docs = safeQueryDocumentList(query);
		Set<String> subjects = new HashSet<String>();
		for(Document document : docs) {
			Statement statement = Statement.wrap(document);
			if(statement.getResponseId()!=null && messageIds.contains(statement.getResponseId()) && (!subjects.contains(statement.getSubject()) || statement.isMessage())) {
				notes.add(document);
				subjects.add(statement.getSubject());
			}
		}
		return notes;
	}

	/**
	 * @param sphereId
	 * @param messageId
	 * @return
	 */
	public Vector<Document> getAttachments(String sphereId, String messageId) {
		String query = "select xmldata from supraspheres where sphere_id='"+sphereId+"' and type='file'";
		List<Document> fileDocs = safeQueryDocumentList(query);
		Vector<Document> filteredFileDos = new Vector<Document>();
		for(Document doc : fileDocs) {
			FileStatement file = FileStatement.wrap(doc);
			if(StringUtils.isBlank(file.getResponseId()) || !file.getResponseId().equals(messageId)) {
				continue;
			}
			filteredFileDos.add(doc);
		}
		return filteredFileDos;
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public Vector<Document> getFilesFromSphere(String sphereId) {
		String query = "select xmldata from supraspheres where sphere_id='"+sphereId+"' and type='file'";
		List<Document> fileDocs = safeQueryDocumentList(query);
		Vector<Document> vector = new Vector<Document>();
		vector.addAll(fileDocs);
		return vector;
	}
	
	public Vector<Document> getFilesFromSphereInThread( final String sphereId, final String threadId ) {
		final String query = "select xmldata from supraspheres where sphere_id='"+sphereId+"' and type='file'"
				+ " and thread_id='" + threadId + "'";
		List<Document> fileDocs = safeQueryDocumentList(query);
		Vector<Document> vector = new Vector<Document>();
		vector.addAll(fileDocs);
		return vector;
	}
	
	public Set<String> getSpheresWithFile(final String dataId) {
		String query = "select xmldata from supraspheres where type='file'";
		List<Document> fileDocs = safeQueryDocumentList(query);
		Set<String> spheres = new HashSet<String>();
		for(Document doc : fileDocs) {
			FileStatement file = FileStatement.wrap(doc);
			if(file.getDataId().equals(dataId)) {
				spheres.add(file.getCurrentSphere());
			}
		}
		return spheres;
	}

	/**
	 * @param sphereList
	 * @param dataId
	 */
	public List<Document> removeFileFromSpheres(List<String> sphereList, String dataId) {
		String inSphere = "";
		for(String sphere : sphereList) {
			inSphere += sphere+",";
		}
		inSphere = inSphere.length()>0 ? inSphere.substring(0, inSphere.length()-1) : inSphere;
		String query = "select xmldata from supraspheres where type='file' and sphere_id in ("+inSphere+")";
		List<Document> filesToRemoveList = new ArrayList<Document>();
		List<Document> resultList = safeQueryDocumentList(query);
		for(Document doc : resultList) {
			FileStatement file = FileStatement.wrap(doc);
			if(StringUtils.isNotBlank(file.getDataId()) && file.getDataId().equals(dataId)) {
				filesToRemoveList.add(doc);
			}
		}
		for(Document doc : filesToRemoveList) {
			try {
				removeDoc(doc, FileStatement.wrap(doc).getCurrentSphere());
			} catch (SQLException ex) {
				logger.error("Can't remove file"+doc.asXML(), ex);
			}
		}
		return filesToRemoveList;
	}

	/**
	 * @param systemName
	 */
	public void removeAllAdminContactsFromSphere(final String systemName) {
		if(systemName==null) {
			logger.error("Cannot delete contacts cause given sphereId is null");
			return;
		}
		final Vector<Document> docs = getAllMessages(systemName);
		if ( docs != null ) {
			for (Document doc : docs) {
				if (doc != null) {
					if ( Statement.wrap(doc).isContact() ) {
						try {
							removeDoc(doc, systemName);
						} catch (SQLException ex) {
							logger.error( "Error removing doc",ex);
						}
					}
				}
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isClubdealAvailableForMember(final String contactName, final String systemName) {
		if(contactName==null || systemName==null) {
			return false;
		}
		String query = "select xmldata from supraspheres where type='contact' and sphere_id='"+systemName+"'";
		List<Document> docs = safeQueryDocumentList(query);
		for(Document doc : docs) {
			ContactStatement contact = ContactStatement.wrap(doc);
			if(contact.getContactNameByFirstAndLastNames().equals(contactName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param roleToRemove
	 */
	public void renameSphereRole(final String roleToRemove, final String replacement) {
		if(!SphereRoleObject.isValid(roleToRemove)) {
			return;
		}
		final String newRoleName = SphereRoleObject.isValid(replacement) ? replacement : SphereRoleObject.getDefaultName();
		String selectQuery = "select xmldata, sphere_id from supraspheres where type='sphere'";
		safeExecuteQuery(selectQuery, new IResultSetRowHandler() {
			public void handleRow(ResultSet rs)
					throws ResultSetRowHandlerException, SQLException {
				String xmldata = rs.getString("xmldata");
				String sphereId = rs.getString("sphere_id");
				if(StringUtils.isBlank(xmldata) || StringUtils.isBlank(sphereId)) {
					return;
				}
				try {
					Document sphereDoc = DocumentHelper.parseText(xmldata);
					SphereStatement sphere = SphereStatement.wrap(sphereDoc);
					if(!SphereRoleObject.isValid(sphere.getRole()) || !sphere.getRole().equals(roleToRemove)) {
						return;
					}
					sphere.setRole(newRoleName);
					replaceDoc(sphere.getBindedDocument(), sphereId);
				} catch (DocumentException ex) {
					logger.error("It's not document", ex);
				}
			}
		});
	}
//	
//	public void logAllSpheresRoles() {
//		String selectQuery = "select xmldata, sphere_id from supraspheres where type='sphere'";
//		List<Document> docs = safeQueryDocumentList(selectQuery);
//		for(Document doc : docs) {
//			SphereStatement sphere = SphereStatement.wrap(doc);
//		}
//	}

	/**
	 * @param contactName
	 * @param role
	 * @return
	 */
	public Collection<SphereStatement> getSpheresByRole(String contactName,
			List<String> roles) {
		String query = "select xmldata from supraspheres where type='sphere'";
		final Set<SphereStatement> spheres = new HashSet<SphereStatement>();
		final Set<String> sphereIds = new HashSet<String>();
		final List<Document> spheresDocs = safeQueryDocumentList(query);
		for(Document doc : spheresDocs) {
			SphereStatement sphere = SphereStatement.wrap(doc);
			if(!roles.contains(sphere.getRole())) {
				continue;
			}
			if(!sphere.getSphereMembers().contains(contactName)) {
				continue;
			}
			if(sphere.getSystemName()==null || sphere.getDisplayName()==null) {
				continue;
			}
			if(!sphereIds.contains(sphere.getSystemName())) {
				spheres.add(sphere);
				sphereIds.add(sphere.getSystemName());
			}
		}
		return spheres;
	}
}
