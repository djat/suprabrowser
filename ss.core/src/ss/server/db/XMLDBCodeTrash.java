package ss.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.DateUtils;
import ss.global.SSLogger;
import ss.refactor.supraspheredoc.old.Utils;
import ss.search.SearchEngine;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.domainmodel2.db.statements.IStableConnectionProvider;
import ss.util.SupraXMLConstants;



class XMLDBCodeTrash {

	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XMLDBCodeTrash.class);

	private XMLDB xmldbOwner;
	
	
	
	/**
	 * 
	 */
	private XMLDBCodeTrash() {
		super();
	}

	public synchronized Vector listTables() {
		Vector<String> tables = new Vector<String>();
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		IStableConnectionProvider provider = DBPool.bind();
		try {
			conn = provider.getConnection();
			statement = conn.createStatement();
			final String query = "show tables";
			rs = statement.executeQuery(query);
			while (rs .next()) {
				tables.add(rs.getString(1));
			}
		} catch (SQLException exc) {
			logger.error("SQL Exception in listTables", exc);
		} finally {
			if ( rs != null ) {
				try {
					rs.close();
				} catch (SQLException ex) {
					logger.error( "Can't close result set", ex );
				}				
			}
			if ( statement != null ) {
				try {
					statement.close();
				} catch (SQLException ex) {
					logger.error( "Can't close statement", ex );
				}				
			}
			if ( conn != null ) {
				try {
					conn.close();
				} catch (SQLException ex) {
					logger.error( "Can't close connection", ex );
				}				
			}
			DBPool.unbind(provider);
		}
		return tables;

	}

	/**
	 * Gets the perspectiveDefinition attribute of the XMLDB object
	 * 
	 * @param system_name
	 *            Description of the Parameter
	 * @param perspective_name
	 *            Description of the Parameter
	 * @return The perspectiveDefinition value
	 */
	public Document getPerspectiveDefinition(String system_name,
			String perspective_name) {

		Document doc = null;
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		try {
			String statement = null;

			statement = "select XMLDATA from supraspheres "
					+ "where sphere_id = '" + system_name
					+ "' and XMLDATA like '%<perspective name=\""
					+ perspective_name + "\"%'";

			java.sql.ResultSet rset = null;

			try {
				Statement stmt = (com.mysql.jdbc.Statement) conn
						.createStatement();
				stmt.getConnection();

				rset = stmt.executeQuery(statement);

				while (rset.next()) {

					try {

						doc = DocumentHelper.parseText(rset.getString(1));

					} catch (DocumentException de) {
						logger.error("Exc while parsing xml document", de);
					}
				}
			} catch (SQLException se) {
				logger.error("exc while getting prospective definition", se);
			}
		} catch (NullPointerException npe) {
			logger.error("exc while getting prospective definition", npe);
		} finally {
			OldDBPool.unbind(dbConnection);
		}
		return doc;

	}

	/**
	 * Description of the Method
	 * 
	 * @param memDoc
	 *            Description of the Parameter
	 * @param old_login
	 *            Description of the Parameter
	 */
	public synchronized void replaceMember(Document memDoc, String old_login) {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ (String) getSession().get("supra_sphere")
				+ "' and ((type='membership') AND XMLDATA LIKE '%contact_name value=\""
				+ old_login + "\"%')";

		java.sql.ResultSet rset = null;

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {
				try {
					Document document = DocumentHelper.parseText(rset
							.getString(1));

					Element message_id = document.getRootElement().element(
							"message_id");
					String moment = document.getRootElement().element(
							"last_updated").attributeValue("value");

					memDoc.getRootElement().remove(message_id);
					memDoc.getRootElement().addElement("message_id")
							.addAttribute("value",
									message_id.attributeValue("value"));
					memDoc.getRootElement().addElement("last_updated")
							.addAttribute("value", moment);

					replaceDoc(memDoc, (String) getSession()
							.get("supra_sphere"));
				} catch (DocumentException exc) {
					logger.error(
							"Document exception while getting sphere document",
							exc);
				}
			}
		} catch (SQLException exc) {
			logger.error("SQL exception while replacing member", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}
	}

	/**
	 * @param date
	 * @return
	 */
	public Date parseDate(String date) {
		return DateUtils.canonicalStringToDate(date);
	}

	/**
	 * @param number
	 * @return
	 */
	public String getDateStringFromLong(long number) {
		java.sql.Timestamp t = new java.sql.Timestamp(number);
		return DateUtils.dateToCanonicalString(t);
	}

	/**
	 * Description of the Method
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @param login_name
	 *            Description of the Parameter
	 */
	public synchronized void registerForSphere(String sphere_id,
			String contact_name, String login_name) {
		Document sphere_definition = getSphereDefinition((String) getSession()
				.get("supra_sphere"), sphere_id);
		sphere_definition.getRootElement().addElement("member").addAttribute(
				"contact_name", contact_name).addAttribute("login_name",
				login_name);
		replaceDoc(sphere_definition, (String) getSession().get("supra_sphere"));
	}

	/**
	 * Gets the all attribute of the XMLDB object
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The all value
	 */
	@SuppressWarnings("unchecked")
	public synchronized Vector getAll(String sphere_id) {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		Vector all = new Vector();
		long longnum = System.currentTimeMillis();
		long days = 4 * 1000 * 60 * 60 * 24;
		long newlong = longnum - days;
		java.sql.Timestamp ts = new java.sql.Timestamp(newlong);
		String newdate = DateUtils.dateToCanonicalString(ts);
		java.sql.ResultSet rset = null;

		// createTable(sphere_id);
		
		logger.error("get all");

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and moment > \""
				+ newdate
				+ "\" AND (type='message'||type='"
				+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
				+ "'||type='file'||type='"+SupraXMLConstants.TYPE_VALUE_RESULT+"'|| " 
				+ "type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE+"' ||type='bookmark'"
				+"||type='comment'||type='edit'||type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE+"'||type='terse'||type='reply'||type='library') ORDER BY create_ts";

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {

				try {
					Document document = DocumentHelper.parseText(rset
							.getString(1));
					all.insertElementAt(document, 0);
				} catch (DocumentException exc) {
					logger.error("exc while processing the document", exc);
				}
			}
		} catch (SQLException exc) {
			logger.error("SQL Exception in getAll", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return all;

	}

	/**
	 * Gets the salt attribute of the XMLDB object
	 * 
	 * @param contact_name
	 *            Description of the Parameter
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The salt value
	 */
	@SuppressWarnings("unchecked")
	public synchronized Hashtable getSalt(String contact_name, String sphere_id) {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();
		Hashtable salt_contact = new Hashtable();

		String salt = null;
		String verifier = null;

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + " where type='membership'";

		java.sql.ResultSet rset = null;

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {
				try {
					Document document = DocumentHelper.parseText(rset
							.getString(1));

					if ((document.getRootElement().element("login_name")
							.attributeValue("value")).equals(contact_name)) {
						salt = document.getRootElement().element("verifier")
								.attributeValue("salt");

						salt_contact.put("salt", salt);
						verifier = document.getRootElement()
								.element("verifier").getText();
						String contact = document.getRootElement().element(
								"contact_name").attributeValue("value");
						salt_contact.put("contact", contact);
					}
				} catch (DocumentException exc) {
					logger.error("Document Exception in getSalt", exc);
				}
			}
		} catch (SQLException exc) {
			logger.error("SQL exception", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}
		if (salt == null || verifier == null) {
			salt = "unreal";
		}
		return salt_contact;
	}

	/**
	 * Gets the contactFromEmail attribute of the XMLDB object
	 * 
	 * @param sphere_id
	 *            Description of the Parameter
	 * @param email
	 *            Description of the Parameter
	 * @return The contactFromEmail value
	 */
	public synchronized String getContactFromEmail(String sphere_id,
			String email) {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		String full_name = null;
		// logger.info("contact_Nmae in getemail:
		// "+contact_name);

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id
				+ "' and type='contact' AND XMLDATA like '%"
				+ email + "%'";

		java.sql.ResultSet rset = null;

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {

				try {

					Document document = DocumentHelper.parseText(rset
							.getString(1));

					String last_name = document.getRootElement().element(
							"last_name").attributeValue("value");

					if (last_name.length() > 0) {
						full_name = document.getRootElement().element(
								"first_name").attributeValue("value")
								+ " "
								+ document.getRootElement()
										.element("last_name").attributeValue(
												"value");
					} else {

						full_name = document.getRootElement().element(
								"first_name").attributeValue("value");

					}

				} catch (DocumentException de) {

				}
			}
		} catch (SQLException exc) {
			logger.error("SQL exception", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}
		return full_name;
	}

	/**
	 * Gets the verifier attribute of the XMLDB object
	 * 
	 * @param contact_name
	 *            Description of the Parameter
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The verifier value
	 */
	public synchronized String getVerifier(String contact_name, String sphere_id) {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ sphere_id + "' and type='membership'";

		java.sql.ResultSet rset = null;
		String verifier = null;
		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {

				try {

					Document document = DocumentHelper.parseText(rset
							.getString(1));

					if ((document.getRootElement().element("login_name")
							.attributeValue("value")).equals(contact_name)) {

						verifier = document.getRootElement()
								.element("verifier").getText();
					}
				} catch (DocumentException de) {

				}

			}

		} catch (SQLException exc) {
			logger.error("SQL exception", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}
		if (verifier == null) {
			verifier = "none";
		}
		return verifier;
	}

	/**
	 * @param sphereDoc
	 * @param loginName
	 */
	public void copySphereToMemberSphereCore(Document sphereDoc,
			String loginName) {

		String homeSphere = getUtils().getHomeSphereFromLogin(loginName);
		insertDoc(sphereDoc, homeSphere);
	}

	/**
	 * @param supraSphereName
	 * @param sphereId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized Vector getMembersWithSphereCore(String supraSphereName,
			String sphereId) {
		Vector result = new Vector();
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		Document doc = null;

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ supraSphereName
				+ "' and XMLDATA like '%<type value=\""
				+ "suprasphere" + "\"%';";

		java.sql.ResultSet rset = null;

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {
				try {
					doc = DocumentHelper.parseText(rset.getString(1));

					String apath = "//suprasphere/member/sphere_core[@system_name=\""
							+ sphereId + "\"]";

					try {
						Element elem = (Element) doc.selectObject(apath);

						Element parent = elem.getParent();
						String contact = parent.attributeValue("contact_name");
						result.add(contact);

					} catch (ClassCastException cce) {

						Vector all = new Vector((ArrayList) doc
								.selectObject(apath));

						for (int i = 0; i < all.size(); i++) {

							Element one = (Element) all.get(i);

							Element parent = one.getParent();
							String contact = parent
									.attributeValue("contact_name");
							result.add(contact);
						}
					}
				} catch (DocumentException de) {
					logger.error(de.getMessage(), de);
				}
			}
		} catch (Exception se) {
			logger.error(se.getMessage(), se);

		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return result;

	}

	/**
	 * Description of the Method
	 * 
	 * @param sphere
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @param login_name
	 *            Description of the Parameter
	 * @param sphere_core
	 *            Description of the Parameter
	 * @param sphere_core_system
	 *            Description of the Parameter
	 */
	@SuppressWarnings("unchecked")
	public synchronized void registerOneMember(String sphere,
			String contact_name, String login_name, String sphere_core,
			String sphere_core_system) {

		try {
			Document doc = getSupraSphereDocument();

			long longnum = getNextTableId();

			String sphere_id = (Long.toString(longnum));

			DefaultElement newelm = new DefaultElement("member");

			newelm.addAttribute("contact_name", contact_name).addAttribute(
					"login_name", login_name);
			newelm.addElement("sphere_core").addAttribute("display_name",
					sphere_core).addAttribute("system_name", sphere_id)
					.addAttribute("sphere_type", "member");
			newelm.addElement("login_sphere").addAttribute("display_name",
					(String) getSession().get("real_name")).addAttribute(
					"system_name", sphere_core_system).addAttribute(
					"sphere_type", "member");
			// newelm.addElement("persona").addAttribute("name",persona_name);
			newelm.addElement("perspective").addAttribute("name", sphere)
					.addAttribute("value", "default");
			newelm.element("perspective").addElement("thread_types");
			newelm.element("perspective").element("thread_types").addElement(
					"message");
			newelm.element("perspective").element("thread_types").addElement(
					"bookmark");
			newelm.element("perspective").element("thread_types").addElement(
					"file");

			newelm.element("perspective").addElement("keyword").addAttribute(
					"value", contact_name);
			newelm.element("perspective").addElement("recent").addAttribute(
					"value", "false");
			newelm.element("perspective").addElement("active").addAttribute(
					"value", "false");
			newelm.element("perspective").addElement("mark").addAttribute(
					"value", "");

			Vector pairs = selectMembers(sphere_core);

			Vector new_spheres = new Vector();

			// logger.info("pairs size: "+pairs.size());

			for (int i = 0; i < pairs.size(); i++) {

				String coll_name = (String) pairs.get(i);
				// logger.info("pair name: "+coll_name);
				if (coll_name.equals((String) getSession().get("real_name"))) {
					longnum = getNextTableId();

					sphere_id = (Long.toString(longnum));

					new_spheres.add(sphere_id);

					newelm
							.addElement("sphere")
							.addAttribute("display_name", coll_name)
							.addAttribute("system_name", sphere_id)
							.addAttribute("default_delivery", "confirm_receipt")
							.addAttribute("sphere_type", "member");

					String apath = "//sphere/members/member[@contact_name=\""
							+ coll_name + "\"]";

					try {
						Element elem = (Element) doc.selectObject(apath);

						elem.addElement("sphere").addAttribute("display_name",
								contact_name).addAttribute("system_name",
								sphere_id).addAttribute("default_delivery",
								"confirm_receipt").addAttribute("sphere_type",
								"member");

					} catch (ClassCastException exc) {
						logger.error("Class Cast Exception", exc);
					}

				}
			}

			newelm.addElement("sphere").addAttribute("display_name",
					(String) getSession().get("supra_sphere")).addAttribute(
					"system_name", sphere_id).addAttribute("default_delivery",
					"normal");

			doc.getRootElement().element("members").add(newelm);
			replaceDoc(doc, sphere);
		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

	/**
	 * Gets the ratifiedLibs attribute of the XMLDB object
	 * 
	 * @return The ratifiedLibs value
	 */
	@SuppressWarnings("unchecked")
	public synchronized Vector getRatifiedLibs() {
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();
		Vector libs = new Vector();
		Document doc = null;

		try {
			String statement = null;

			statement = "select XMLDATA from supraspheres where sphere_id = '"
					+ (String) getSession().get("supra_sphere")
					+ "' and (type='library')";

			java.sql.ResultSet rset = null;

			try {
				Statement stmt = (com.mysql.jdbc.Statement) conn
						.createStatement();
				stmt.getConnection();

				rset = stmt.executeQuery(statement);

				while (rset.next()) {

					try {

						doc = DocumentHelper.parseText(rset.getString(1));
						libs.add(doc);
					} catch (DocumentException exc) {
						logger.error("Document Exception", exc);
					}

				}
			} catch (SQLException exc) {
				logger.error("SQL exception", exc);
			}
		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return libs;

	}

	/**
	 * @param sphereId
	 * @return
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public Vector getContactDocsForSphere(String sphereId)
			throws DocumentException { // Code never
		// tested....

		logger.info("Starting getcontactdocsforSphere  : " + sphereId);

		Vector memberDocs = new Vector();

		String cpath = "//suprasphere/member/sphere[@system_name='"
				+ (String) getSession().get("sphere_id")
				+ "' and @enabled='true']";

		Document ssDoc = getSupraSphereDocument();

		Vector result = new Vector();

		try {

			Element member = (Element) ssDoc.selectObject(cpath);
			result.add(member);

		} catch (ClassCastException cce) {

			logger.error(cce.getMessage(), cce);

			result = new Vector((List) ssDoc.selectObject(cpath));
		}

		java.sql.Connection conn = null;
		DBConnection dbConnection = OldDBPool.bind();
		try {
			conn = dbConnection.getConnection();

			logger.info("Result size: " + result.size());

			for (int i = 0; i < result.size(); i++) {
				Element member = (Element) result.get(i);
				String username = member.attributeValue("login_name");

				String loginSphereId = getUtils().getLoginSphereSystemName(username);

				String statement = "select XMLDATA from supraspheres where sphere_id = '"
						+ loginSphereId
						+ "' and type='contact' and XMLDATA like '%<login value=\""
						+ username + "%'";

				java.sql.ResultSet rset = null;

				try {
					Statement stmt = (com.mysql.jdbc.Statement) conn
							.createStatement();
					stmt.getConnection();

					rset = stmt.executeQuery(statement);

					while (rset.next()) {
						try {

							Document document = DocumentHelper.parseText(rset
									.getString(1));
							memberDocs.add(document);

						} catch (DocumentException de) {

						}
					}
				} catch (SQLException exc) {
					logger.error("SQL exception", exc);
				}
			}
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return memberDocs;

	}

	/**
	 * @param session
	 * @param privateList
	 * @param doc
	 * @param sphereId
	 * @throws DocumentException
	 */
	public void updateStats(Hashtable session, Vector privateList,
			Document doc, String sphereId) throws DocumentException {
		//
		// logger.info("updatestats");
		//
		// logger.info("private list size: " +
		// privateList.size());
		// for (int i = 0; i < privateList.size(); i++) {
		//
		// Document contactDoc = (Document) privateList.get(i);
		//
		// String contactName = NameTranslation
		// .createContactNameFromContactDoc(contactDoc);
		//
		// String personalSphere = getPersonalSphere((String)
		// session
		// .get("supra_sphere"),
		// contactDoc.getRootElement().element("login")
		// .attributeValue("value"), contactName);
		//
		// java.sql.Connection conn = null;
		//
		// String statement = "select XMLDATA from supraspheres
		// where sphere_id = '"
		// + personalSphere
		// + "' and type='stats' and XMLDATA like
		// '%last_launched sphere_id=\""
		// + sphereId + "%'";
		//
		// logger.info("statement in setmark: " + statement);
		//
		//      
		// java.sql.ResultSet rset = null;
		//
		// String verifier = null;
		//
		// boolean found = false;
		// try {
		// conn = getConnection();
		//
		// Statement stmt = (com.mysql.jdbc.Statement)
		// conn.createStatement();
		// stmt.getConnection();
		//
		// rset = stmt.executeQuery(statement);
		//
		// while (rset.next()) {
		//          
		// found = true;
		// Document statisticsDoc = DocumentHelper
		// .parseText(rset.getString(1));
		//          
		// if
		// (statisticsDoc.getRootElement().element("since_local_mark")
		// != null) {
		// statisticsDoc.getRootElement().element("since_local_mark")
		// .addElement("id").addAttribute(
		// "value",
		// doc.getRootElement().element("message_id").attributeValue(
		// "value"));
		// Element sinceLocal =
		// statisticsDoc.getRootElement().element(
		// "since_local_mark");
		//            
		// String sinceLaunched = sinceLocal
		// .attributeValue("since_last_launched");
		//            
		// sinceLaunched = VariousUtils
		// .increaseStringByNumeroUno(sinceLaunched);
		//            
		// String total =
		// sinceLocal.attributeValue("total_in_sphere");
		// total =
		// VariousUtils.increaseStringByNumeroUno(total);
		//            
		// String replies =
		// sinceLocal.attributeValue("replies_to_mine");
		//            
		// if (doc.getRootElement().element("response_id") !=
		// null) {
		// Document parentDoc = getParentDoc(doc, sphereId);
		// if
		// (parentDoc.getRootElement().element("giver").attributeValue(
		// "value").equals(contactName)) {
		// replies =
		// VariousUtils.increaseStringByNumeroUno(replies);
		//                
		// }
		// }
		// String since =
		// sinceLocal.attributeValue("since_mark");
		//            
		// since =
		// VariousUtils.increaseStringByNumeroUno(since);
		//            
		// sinceLocal.addAttribute("total_in_sphere", total);
		// sinceLocal.addAttribute("replies_to_mine", replies);
		// sinceLocal.addAttribute("since_mark", since);
		// sinceLocal.addAttribute("since_last_launched",
		// sinceLaunched);
		//            
		// }
		// else {
		//            
		// String repliesToMine = "0";
		// statisticsDoc.getRootElement().addElement("since_local_mark")
		// .addElement("id").addAttribute(
		// "value",
		// doc.getRootElement().element("message_id").attributeValue(
		// "value"));
		// if (doc.getRootElement().element("response_id") !=
		// null) {
		// Document parentDoc = getParentDoc(doc, sphereId);
		// if
		// (parentDoc.getRootElement().element("giver").attributeValue(
		// "value").equals(contactName)) {
		// repliesToMine = "1";
		// }
		// }
		//            
		// statisticsDoc.getRootElement().element("since_local_mark")
		// .addAttribute("total_in_sphere",
		// (new
		// Integer(countDocs(sphereId))).toString()).addAttribute(
		// "replies_to_mine",
		// repliesToMine).addAttribute("since_mark",
		// "1").addAttribute("sine_last_launched", "1");
		//            
		// }
		//          
		// if
		// (statisticsDoc.getRootElement().element("since_global_mark")
		// != null) {
		// statisticsDoc.getRootElement().element("since_global_mark")
		// .addElement("id").addAttribute(
		// "value",
		// doc.getRootElement().element("message_id").attributeValue(
		// "value"));
		// Element sinceGlobal =
		// statisticsDoc.getRootElement().element(
		// "since_global_mark");
		//            
		// String sinceLaunched = sinceGlobal
		// .attributeValue("since_last_launched");
		//            
		// sinceLaunched = VariousUtils
		// .increaseStringByNumeroUno(sinceLaunched);
		//            
		// String total =
		// sinceGlobal.attributeValue("total_in_sphere");
		// total =
		// VariousUtils.increaseStringByNumeroUno(total);
		//            
		// String replies =
		// sinceGlobal.attributeValue("replies_to_mine");
		//            
		// if (doc.getRootElement().element("response_id") !=
		// null) {
		// Document parentDoc = getParentDoc(doc, sphereId);
		// if
		// (parentDoc.getRootElement().element("giver").attributeValue(
		// "value").equals(contactName)) {
		// replies =
		// VariousUtils.increaseStringByNumeroUno(replies);
		//                
		// }
		// }
		// String since =
		// sinceGlobal.attributeValue("since_mark");
		//            
		// since =
		// VariousUtils.increaseStringByNumeroUno(since);
		//            
		// sinceGlobal.addAttribute("total_in_sphere", total);
		// sinceGlobal.addAttribute("replies_to_mine", replies);
		// sinceGlobal.addAttribute("since_mark", since);
		// sinceGlobal.addAttribute("since_last_launched",
		// sinceLaunched);
		//            
		// }
		// else {
		//            
		// String repliesToMine = "0";
		// statisticsDoc.getRootElement().addElement("since_global_mark")
		// .addElement("id").addAttribute(
		// "value",
		// doc.getRootElement().element("message_id").attributeValue(
		// "value"));
		// if (doc.getRootElement().element("response_id") !=
		// null) {
		// Document parentDoc = getParentDoc(doc, sphereId);
		// if
		// (parentDoc.getRootElement().element("giver").attributeValue(
		// "value").equals(contactName)) {
		// repliesToMine = "1";
		// }
		// }
		//            
		// statisticsDoc.getRootElement().element("since_global_mark")
		// .addAttribute("total_in_sphere",
		// (new
		// Integer(countDocs(sphereId))).toString()).addAttribute(
		// "replies_to_mine",
		// repliesToMine).addAttribute("since_mark",
		// "1").addAttribute("sine_last_launched", "1");
		//            
		// }
		//          
		// replaceDoc(statisticsDoc, personalSphere);
		// }
		// }
		// catch (SQLException exc) {
		// logger.error("SQL exception", exc);
		// }
		// finally {
		// closeConn(conn);
		// }
		// }
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public Document getUniqueSphereDefinition(String sphereId) {
		return getSphereDefinition(sphereId, sphereId);
	}

	/**
	 * @param sphere
	 * @param sphere_document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized Vector getForSphere(String sphere,
			Document sphere_document) {

		boolean isSearchOn = false;
		boolean isKeywordSearchOn = false;
		boolean isAuthorSearchOn = false;
		boolean isTypeSearchOn = false;

		boolean spheres = false;
		String keywordSearch = "";
		String authorSearch = "";
		
		logger.error(" get for sphere in code trash");

		if (sphere_document.getRootElement().element("query") != null) {

			isTypeSearchOn = true;

		}
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();
		Vector all = new Vector();
		try {

			String statement_filter = "";

			try {
				Statement stmt = (com.mysql.jdbc.Statement) conn
						.createStatement();
				stmt.getConnection();
			} catch (SQLException exc) {
				logger
						.error(
								"sql exception while getting statement in getForSphere",
								exc);
			}
			Vector statements = new Vector();

			if (sphere_document != null) {

				Vector types = new Vector(sphere_document.getRootElement()
						.element("thread_types").elements());

				String statement_types = "";
				if (types.size() > 1) {
					statement_types = "(";
					for (int i = 0; i < types.size(); i++) {

						Element one = (Element) types.get(i);

						String modify = one.attributeValue("modify");
						boolean view = false;
						if ((modify.equals("any") || modify.equals("own") || modify
								.equals("view"))) {

							view = true;

						}

						if (i == 0) {
							statement_types = statement_types
									+ "type='reply'||type='comment'||type='edit'||type='";
							if (view == true) {

								statement_types = statement_types
										+ one.getName() + "'";

							}
						} else {
							if (view == true) {
								statement_types = statement_types + "||type='"
										+ one.getName() + "'";
							}
						}

						Element filter = one.element("filter");

						if (filter != null) {

							statement_filter = filter.attributeValue("value");

							Perl5Util perl = new Perl5Util();

							if (statement_filter.lastIndexOf('{') != -1) {
								String substitute = new String(
										"s/\\{\\$contact_name\\}/"
												+ "\""
												+ (String) getSession().get(
														"real_name") + "\""
												+ "/g");
								String after = perl.substitute(substitute,
										statement_filter);

								statement_filter = after;
							}
						}
					}
					statement_types = statement_types + ")";

				} else {
					if (types.size() == 1) {

						Element one = (Element) types.elementAt(0);

						statement_types = "type='" + one.getName() + "'";

						Element filter = one.element("filter");

						if (filter != null) {
							statement_filter = filter.attributeValue("value");
							String replace = statement_filter.replaceAll("~",
									"\"");
							statement_filter = replace;
						}
						String test = one.getName();
						if (test.equals("sphere")) {
							spheres = true;
						}
					}
				}

				Element search = sphere_document.getRootElement().element(
						"search");

				if (search != null) {
					isSearchOn = true;
					if (search.element("keywords") != null) {

						isKeywordSearchOn = true;
						keywordSearch = sphere_document.getRootElement()
								.element("search").element("keywords")
								.attributeValue("value");

					}
					if (search.element("author") != null) {

						isAuthorSearchOn = true;
						authorSearch = sphere_document.getRootElement()
								.element("search").element("author")
								.attributeValue("value");
					}
				}

				String exp = sphere_document.getRootElement().element(
						"expiration").attributeValue("value");

				exp = exp.toLowerCase();
				if (exp.equals("since local mark")) {

					String personalSphere = getUtils().getPersonalSphere(
							(String) getSession().get("username"),
							(String) getSession().get("real_name"));

					Document stats = getStatisticsDoc(personalSphere, sphere);
					Element mark = stats.getRootElement().element("last_mark");

					if (mark == null) {
						statements.removeAllElements();
					}
				} else if (!exp.equals("all") && !exp.equals("none")) {
					logger.info("not all, not none");

					long explong = System.currentTimeMillis();
					long expnum = 0;
					long expdays = 0;
					if (exp.lastIndexOf("hour") != -1) {

						StringTokenizer st = new StringTokenizer(exp, " ");
						String begin = st.nextToken();

						Integer conv = new Integer(begin);

						expdays = (conv.intValue() * 1000 * 60 * 60);
						logger.info("HOURS!!!: " + expdays);

					} else if (exp.lastIndexOf("week") != -1) {

						StringTokenizer st = new StringTokenizer(exp, " ");
						String begin = st.nextToken();

						Long conv = new Long(begin);

						expdays = (conv.longValue() * 1000 * 60 * 60 * 24 * 7);

					} else {

						StringTokenizer st = new StringTokenizer(exp, " ");
						String begin = st.nextToken();

						Integer conv = new Integer(begin);

						expdays = (conv.intValue() * 1000 * 60 * 60 * 24);

					}

					expnum = explong - expdays;

					java.sql.Timestamp t = new java.sql.Timestamp(expnum);
					String a_moment = DateUtils.dateToCanonicalString(t);

					String one_statement = "select XMLDATA from supraspheres where sphere_id = '"
							+ sphere
							+ "' and (moment > '"
							+ a_moment
							+ "' AND ("
							+ statement_types
							+ ")) ORDER BY create_ts;";

					statements.add(one_statement);

				} else {
					logger.info("either all or none");

					if (exp.equals("none")) {
						statements.removeAllElements();
					} else {
						String one_statement = "select XMLDATA from supraspheres where sphere_id = '"
								+ sphere
								+ "' where (("
								+ statement_types
								+ ") AND XMLDATA LIKE '%"
								+ statement_filter
								+ "%') ORDER BY create_ts;";
						statements.add(one_statement);
					}
				}
			} else {
				long longnum = System.currentTimeMillis();
				long days = 4 * 1000 * 60 * 60 * 24;
				long newlong = longnum - days;

				java.sql.Timestamp ts = new java.sql.Timestamp(newlong);
				String newdate = DateUtils.dateToCanonicalString(ts);

				String one_statement = "select XMLDATA from supraspheres where sphere_id = '"
						+ sphere
						+ "' and moment > \""
						+ newdate
						+ "\" AND (type='message'||type='"
						+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
						+ "'||type='file'||type='"+SupraXMLConstants.TYPE_VALUE_RESULT+"'||type='bookmark'"
						+"||type='comment'||type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE+"'||type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE+"'||type='terse'||type='edit'||type='reply'||type='library'||type='contact'||type='sphere') ORDER BY create_ts";

				statements.add(one_statement);
			}

			java.sql.ResultSet rset = null;
			String one_statement = null;

			try {
				Statement stmt = (com.mysql.jdbc.Statement) conn
						.createStatement();

				stmt.getConnection();

				for (int i = 0; i < statements.size(); i++) {
					one_statement = (String) statements.get(i);

					logger.info("TRYING ONE STATEMNT: " + one_statement);
					rset = stmt.executeQuery(one_statement);

					while (rset.next()) {

						try {
							Document document = DocumentHelper.parseText(rset
									.getString(1));
							document.getRootElement().addElement(
									"current_sphere").addAttribute("value",
									sphere);

							if (isSearchOn) {

								if ((isKeywordSearchOn == true && isAuthorSearchOn == false)) {
									// logger.info("KEYWORD ON");

									SearchEngine se = new SearchEngine();
									if (se.searchForKeywords(document,
											keywordSearch, false)) {

										// if
										// (document.getRootElement().element(

										Element assoc = document
												.getRootElement().element(
														"associations");

										if (assoc != null) {

											Vector elems = new Vector(assoc
													.elements());

											for (int j = 0; j < elems.size(); j++) {

												Element one = (DefaultElement) elems
														.get(j);

												String message_id = one
														.attributeValue("message_id");
												String sphere_id = one
														.attributeValue("sphere_id");

												Document spec = getSpecificID(
														sphere_id, message_id);

												Hashtable entire = getUtils()
														.getAllOfThread(spec,
																sphere_id);

												Vector resp = (Vector) entire
														.get("responses");
												if (!getUtils()
														.containsDocument(all,
																spec)) {
													all.addAll(resp);
													all.add(spec);

												}

											}

										}

										if (document.getRootElement().element(
												"response_id") != null) {

											Document rootDoc = getUtils()
													.getRootDoc(document,
															sphere);

											if (rootDoc != null) {

												if (!getUtils()
														.containsDocument(all,
																rootDoc)) {

													Hashtable response = getUtils()
															.getAllOfThread(
																	rootDoc,
																	sphere);

													Vector responses = (Vector) response
															.get("responses");

													all.addAll(responses);
													all.add(rootDoc);
												}
											}
										} else {

											if (!getUtils().containsDocument(
													all, document)) {
												Hashtable response = getUtils()
														.getAllOfThread(
																document,
																sphere);

												Vector responses = (Vector) response
														.get("responses");

												all.addAll(responses);
												all.add(document);
											}

										}
									} else {

									}

								} else {

									if (document.getRootElement().element(
											"response_id") != null) {

										Document rootDoc = getUtils()
												.getRootDoc(document, sphere);

										if (rootDoc != null) {

											if (!getUtils().containsDocument(
													all, rootDoc)) {

												Hashtable response = getUtils()
														.getAllOfThread(
																rootDoc, sphere);

												Vector responses = (Vector) response
														.get("responses");

												all.addAll(responses);
												all.add(rootDoc);
											}
										}
									} else {

										if (!getUtils().containsDocument(all,
												document)) {
											Hashtable response = getUtils()
													.getAllOfThread(document,
															sphere);

											Vector responses = (Vector) response
													.get("responses");

											all.addAll(responses);

											all.add(document);
										}

									}

								}

							} else if (isAuthorSearchOn) {
								SearchEngine se = new SearchEngine();

								if (se.searchForAuthor(document, authorSearch)) {

									if (document.getRootElement().element(
											"response_id") != null) {
										Document parentDoc = getParentDoc(
												document, sphere);

										Hashtable response = getUtils()
												.getAllOfThread(parentDoc,
														sphere);

										Vector responses = (Vector) response
												.get("responses");

										all.addAll(responses);
										all.add(parentDoc);
									} else {
										Hashtable response = getUtils()
												.getAllOfThread(document,
														sphere);

										Vector responses = (Vector) response
												.get("responses");

										all.addAll(responses);
										all.add(document);
									}
								}
							} else if (isTypeSearchOn == true) {
								if (document.getRootElement().element(
										"response_id") != null) {
									Document rootDoc = getUtils().getRootDoc(
											document, sphere);

									if (rootDoc != null) {
										if (!getUtils().containsDocument(all,
												rootDoc)) {
											Hashtable response = getUtils()
													.getAllOfThread(rootDoc,
															sphere);
											Vector responses = (Vector) response
													.get("responses");

											all.addAll(responses);
											all.add(rootDoc);
										}
									}
								} else {
									if (!getUtils().containsDocument(all,
											document)) {
										Hashtable response = getUtils()
												.getAllOfThread(document,
														sphere);

										Vector responses = (Vector) response
												.get("responses");

										all.addAll(responses);
										all.add(document);
									}
								}
							} else {
								all.add(document);
							}
						} catch (DocumentException de) {
							logger.error("Exc while parsing xml document", de);
						}
					}
				}
			} catch (SQLException se) {
				logger.error(
						"exc while getting prospective definition: query = "
								+ one_statement, se);
			}
		} catch (NullPointerException npe) {
			logger.error("exc while getting prospective definition", npe);
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		Vector newall = new Vector();

		if (spheres == true) {
			Vector available_spheres = getAvailableSpheres(getSession());

			for (int i = 0; i < available_spheres.size(); i++) {

				Document test = (Document) available_spheres.get(i);

				String system = test.getRootElement().attributeValue(
						"system_name");

				boolean found = false;

				for (int j = 0; j < all.size(); j++) {
					Document testall = (Document) all.get(j);
					String systemall = testall.getRootElement().attributeValue(
							"system_name");

					if (system.equals(systemall)) {
						found = true;
					}
				}

				if (found == true) {
					newall.add(test);
				}
			}

			all = newall;
		}
		return all;
	}

	/**
	 * Gets the availableSpheres attribute of the XMLDB object
	 * 
	 * @param session
	 *            Description of the Parameter
	 * @return The availableSpheres value
	 */
	@SuppressWarnings("unchecked")
	public synchronized Vector getAvailableSpheres(Hashtable session) {

		Vector available = new Vector();
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();

		String contact_name = (String) session.get("real_name");

		Document sphere = null;

		String statement = "select XMLDATA from supraspheres where sphere_id = '"
				+ (String) session.get("supra_sphere")
				+ "' and ((type='sphere') AND XMLDATA LIKE '%member contact_name=\""
				+ contact_name + "\"%')";

		java.sql.ResultSet rset = null;

		try {
			Statement stmt = (com.mysql.jdbc.Statement) conn.createStatement();
			stmt.getConnection();

			rset = stmt.executeQuery(statement);

			while (rset.next()) {

				try {
					sphere = DocumentHelper.parseText(rset.getString(1));
					available.add(sphere);
				} catch (DocumentException exc) {
					logger.error("Document Exception in getAvailableSpheres",
							exc);
				}
			}
		} catch (SQLException exc) {
			logger.error("SQL Exception in getAvailableSpheres", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return available;
	}

	/**
	 * Description of the Method
	 * 
	 * @param sphere
	 *            Description of the Parameter
	 * @param thread_id
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	@SuppressWarnings("unchecked")
	public Vector xx(XMLDB xmldb, String sphere, String thread_id) {
		// Like get all, but something else
		DBConnection dbConnection = OldDBPool.bind();
		java.sql.Connection conn = dbConnection.getConnection();
		Vector all = new Vector();
		try {
			String statement = null;
			
			logger.error("---------- xx ----------");

			statement = "select XMLDATA from supraspheres where sphere_id = '"
					+ sphere
					+ "' and type='message'||type='"
					+ SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL
					+ "'||type='file'||type='"+SupraXMLConstants.TYPE_VALUE_RESULT+"'||type='bookmark'"
					+ "||type='comment'||type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE+"'||type='"+SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE+"'||type='edit'||type='terse'||type='reply'||type='library') AND XMLDATA like '%"
					+ thread_id + "%' ORDER BY create_ts";

			java.sql.ResultSet rset = null;

			try {
				Statement stmt = (com.mysql.jdbc.Statement) conn
						.createStatement();
				stmt.getConnection();

				rset = stmt.executeQuery(statement);

				while (rset.next()) {
					try {
						Document document = DocumentHelper.parseText(rset
								.getString(1));

						all.add(document);
					} catch (DocumentException exc) {
						logger.error("document exception", exc);
					}
				}
			} catch (SQLException exc) {
				logger.error("SQL exception", exc);
			}
		} catch (NullPointerException exc) {
			logger.error("NPE exception", exc);
		} finally {
			OldDBPool.unbind(dbConnection);
		}

		return all;
	}

	/**
	 * @param sphereDoc
	 * @param homeSphere
	 */
	private void insertDoc(Document sphereDoc, String homeSphere) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	private XmldbUtils getUtils() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param sphereId
	 * @param sphereId2
	 * @return
	 */
	private Document getSphereDefinition(String sphereId, String sphereId2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param sphere_core
	 * @return
	 */
	private Vector selectMembers(String sphere_core) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @throws DocumentException
	 */
	private Document getSupraSphereDocument() throws DocumentException {
		return Utils.getUtils(this.xmldbOwner).getSupraSphereDocument();
	}

	/**
	 * @return
	 */
	private long getNextTableId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param sphere_id
	 * @param message_id
	 * @return
	 */
	private Document getSpecificID(String sphere_id, String message_id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param document
	 * @param sphere
	 * @return
	 */
	private Document getParentDoc(Document document, String sphere) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param personalSphere
	 * @param sphere
	 * @return
	 */
	private Document getStatisticsDoc(String personalSphere, String sphere) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param memDoc
	 * @param string
	 */
	private void replaceDoc(Document memDoc, String string) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the session
	 */
	private Hashtable getSession() {
		return this.xmldbOwner.getSession();
	}

}


class OldDBPool {

    
    private static final String JDBC_DRIVER_NAME = "com.mysql.jdbc.Driver";

    private static AtomicReference<OldDBPool> pool = new AtomicReference<OldDBPool>(
	    null);

    private String dbURL = null;

    private Vector<DBConnection> freeConn = new Vector<DBConnection>(40);

    private Logger logger = SSLogger.getLogger(this.getClass());

    private Vector<DBConnection> usedConn = new Vector<DBConnection>(40);

    private OldDBPool() {
	loadDBURL();
	createDriverInstance();
    }

    private DBConnection bindConnection() {
	synchronized (this.freeConn) {
	    if (this.freeConn.size() < 1) {
		this.freeConn.add(createConnection());
	    }
	    int index = this.freeConn.size() - 1;
	    DBConnection connection = this.freeConn.get(index);
	    this.freeConn.remove(index);
	    this.usedConn.add(connection);
	    return connection;
	}
    }

    private void checkState() {
	int usedSize = this.usedConn.size();
	if (usedSize > 0) {
	    this.logger.error("There is " + usedSize + " connection"
		    + (usedSize > 1 ? "s" : "") + " not unbinded!");
	} else {
	    this.logger.info("All OK");
	}
    }

    private DBConnection createConnection()
	    throws InstantiationConnectionException {
	try {
	    Connection conn = DriverManager.getConnection(this.dbURL);
	    setUpConnectionProperties(conn);
	    return new DBConnection(conn);
	} catch (SQLException e) {
	    String message = "Could not instantiate connection : " + this.dbURL;
	    this.logger.error(message, e);
	    throw new InstantiationConnectionException(message, e);
	}
    }

    private void createDriverInstance() {
	try {
	    Class.forName(JDBC_DRIVER_NAME).newInstance();
	} catch (InstantiationException e) {
	    String message = "Could not intstatiat driver instance";
	    this.logger.error(message, e);
	    throw new DriverNotCreatedException(message, e);
	} catch (IllegalAccessException e) {
	    String message = "Driver default constructor not visible";
	    this.logger.error(message, e);
	    throw new DriverNotCreatedException(message, e);
	} catch (ClassNotFoundException e) {
	    String message = "Driver Class not found";
	    this.logger.error(message, e);
	    throw new DriverNotCreatedException(message, e);
	}
    }

    @Override
    protected void finalize() throws Throwable {
	checkState();
	super.finalize();
    }

    private void freeAll() {
	synchronized (this.freeConn) {
	    for (DBConnection con : this.freeConn) {
		try {
		    con.getConnection().close();
		} catch (SQLException e) {
		    this.logger.error("", e);
		}
	    }
	    this.freeConn.removeAllElements();
	    for (DBConnection con : this.usedConn) {
		try {
		    con.getConnection().close();
		} catch (SQLException e) {
		    this.logger.error("", e);
		}
	    }
	    this.usedConn.removeAllElements();
	}
    }

    private void loadDBURL() {
	    this.dbURL = DbUrlProvider.INSTANCE.getDbUrl();
	}

    /**
         * @param conn
         * @throws SQLException
         */
    private void setUpConnectionProperties(Connection conn) throws SQLException {
	conn.setAutoCommit(true);
    }

    private void unbindConnection(DBConnection connection) {
	synchronized (this.freeConn) {
	    boolean unbined = this.usedConn.remove(connection);
	    if (!unbined) {
		throw new NoSuchConnectionException(
			"unBinded connection was not managed by this instance. Details:"
				+ connection.toString());
	    }
	    this.freeConn.add(connection);
	}
    }

    /**
         * @return return binded db connection
         */
    public static DBConnection bind() {
	return getPool().bindConnection();
    }

    public static OldDBPool getPool() {
    	if (pool.get() == null) {
			pool.compareAndSet(null, new OldDBPool());
		}
		return pool.get();
	}

    public static void recreate() {
	OldDBPool oldPool = getPool();
	oldPool.checkState();
	oldPool.freeAll();
	pool.set(new OldDBPool());
    }

    /**
         * @param connection returned connection to pool so it can be reused
         *                later
         */
    public static void unbind(DBConnection connection) {
	getPool().unbindConnection(connection);
    }

  
    public static class DriverNotCreatedException extends RuntimeException {

	/**
		 * 
		 */
		private static final long serialVersionUID = -7872368838488001175L;

	public DriverNotCreatedException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    public static class InstantiationConnectionException extends RuntimeException {

	/**
		 * 
		 */
		private static final long serialVersionUID = -449418544775757354L;

	public InstantiationConnectionException(String message, Throwable cause) {
	    super(message, cause);
	}

    }

    public static class NoSuchConnectionException extends RuntimeException {

	/**
		 * 
		 */
		private static final long serialVersionUID = -6445991759368383166L;

	public NoSuchConnectionException(String message) {
	    super(message);
	}

    }

	/**
	 * 
	 */
	public int freeConnSize() {
		return this.freeConn.size();
	}

	/**
	 * 
	 */
	public int usedConnSize() {
		return this.usedConn.size();
	}
	
	

}



class DBConnection {

    private Connection connection;

    public DBConnection(Connection connection) {
	this.connection = connection;
    }

    Connection getConnection() {
	return this.connection;
    }

    @Override
    public String toString() {
	return this.connection.toString();
    }

	/**
	 * @return
	 * @throws SQLException 
	 */
	Statement createStatement() throws SQLException {
		return this.connection.createStatement();
	}
    
    

}
