package ss.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import ss.server.domainmodel2.db.statements.IStableConnectionProvider;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

// TODO : complete db usage refactoring 
public class Convertor {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Convertor.class);
	
	//private final XMLDB xmldb;
	
	
	/**
	 * @param xmldb
	 */
	public Convertor(final XMLDB xmldb) {
		super();
	//	this.xmldb = xmldb;
	}

	/**
	 * converts from individual sphere tables to single sphere table with
	 * additional sphere_id column
	 */
	public void upgradeSphereTableStructure() throws SQLException {
		IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection connection = null;
		try {
			connection = connectionProvider.getConnection();
			createUnifiedTable(connection);
			combinedSphereTables(connection);
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
	}

	public void main(String[] args) {
		try {
			// xmldb.loadProperties();
			// xmldb.initVerifyAuth();
			upgradeSphereTableStructure();

			/*
			 * AcrossTableUtils atu = new AcrossTableUtils(xmldb); Hashtable
			 * spheres = atu.getAllSpheresWithoutDuplicates(); for
			 * (Enumeration enumer =
			 * spheres.keys();enumer.hasMoreElements();) { String checking =
			 * (String)enumer.nextElement(); if
			 * (checking.equals("2585713758200240623")) { logger.info("Found
			 * it"); } else { logger.info("Did not find it: "+checking); }
			 */

			/*
			 * if
			 * (xmldb.verifyAuth.check("//suprasphere/member[@login_name=\"david\"]")) {
			 * logger.info("Checked out"); } else { logger.info("DIc not
			 * check out"); }
			 */
			// logger.info("update completed successfully!");
		} catch (Exception exc) {
			logger.error("update failed.", exc);
		}
	}

	/**
	 * createUnifiedTable creates the new 'spheres' table
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public synchronized void createUnifiedTable(Connection connection)
			throws SQLException {

		java.sql.Statement statement = null;
		try {

			StringBuffer SQLBuffer = new StringBuffer();

			SQLBuffer
					.append("create table if not exists supraspheres (")
					.append(" sphere_id varchar(50) not null,")
					.append(
							" recid int unsigned auto_increment primary key,")
					.append(" xmldata mediumtext not null,")
					.append(" type varchar(100) not null,")
					.append(" moment datetime not null,")
					.append(" thread_type varchar(100) not null,")
					.append(
							" create_ts timestamp not null default current_timestamp,")
					.append(" thread_id bigint not null,")
					.append(" message_id bigint not null,")
					.append(
							" isResponse int(1) unsigned not null DEFAULT 0,")
					.append(" used datetime DEFAULT null,")
					.append(" modified datetime not null,")
					.append(
							" total_accrued int unsigned not null DEFAULT 0)");

			statement = connection.createStatement();

			statement.executeUpdate(SQLBuffer.toString());

		} catch (SQLException exc) {
			logger.error("SQL exc while creating spheres table", exc);
			throw exc;
		}
	}

	/**
	 * combinedSphereTables will insert all individual sphere tables into
	 * the 'spheres' table with sphere_id as the former table name
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public synchronized void combinedSphereTables(Connection connection)
			throws SQLException {
		final IStableConnectionProvider connectionProvider = DBPool.bind();
		Connection innerConnection = null;
		List<String> tableNameMask = XmldbUtils.getTableNameMask();

		try {
			Vector tables = listTables();
			tables.removeAll(tableNameMask);

			Enumeration sphereTableNames = tables.elements();
			String tableStringName = null;
			String selectQuery = null;

			connection = connectionProvider.getConnection();
			PreparedStatement insertStatement = getInsertStatement(connection);

			while (sphereTableNames.hasMoreElements()) {
				tableStringName = (String) sphereTableNames.nextElement();
				try {
					selectQuery = createSelectQuery(tableStringName);

					java.sql.Statement statement = connection
							.createStatement();
					ResultSet resultSet = statement
							.executeQuery(selectQuery);
					insertStatement.setString(1, tableStringName);
					Date current = new Date();
					String nonNullMoment = DateFormat.getTimeInstance(
							DateFormat.LONG).format(current)
							+ " "
							+ DateFormat.getDateInstance(DateFormat.MEDIUM)
									.format(current);

					while (resultSet.next()) {
						String xml = resultSet.getString("xmldata");
						insertStatement.setString(2, xml);
						insertStatement.setString(3, resultSet
								.getString("type"));
						try {

							String moment = resultSet
									.getTimestamp("moment").toString();
							if (moment.startsWith("0000")) {

								insertStatement.setTimestamp(4, resultSet
										.getTimestamp(nonNullMoment));
							} else {
								insertStatement.setTimestamp(4, resultSet
										.getTimestamp("moment"));
							}

						} catch (Exception e) {

						}
						insertStatement.setString(5, resultSet
								.getString("thread_type"));
						try {

							String time = resultSet.getTimestamp("time")
									.toString();
							if (time.startsWith("0000")) {
								insertStatement.setTimestamp(6, resultSet
										.getTimestamp(nonNullMoment));

							} else {
								insertStatement.setTimestamp(6, resultSet
										.getTimestamp("time"));
							}

						} catch (Exception e) {

						}
						String threadId = resultSet.getString("thread_id");
						Document doc = null;
						try {
							doc = DocumentHelper.parseText(xml);
							if (doc.getRootElement().element("message_id") == null) {
								String messageId = VariousUtils
										.createMessageId();
								doc.getRootElement().addElement(
										"message_id").addAttribute("value",
										messageId);
								logger.info("THIS HAD NO MESSAGE ID: "
												+ doc.asXML());

							}
						} catch (DocumentException e1) {
							logger.error("", e1);
						}

						if (threadId == null) {
							logger.info("It was null");
							if (doc.getRootElement().element("thread_id") == null) {
								logger.info("thread was null");
								XMLSchemaTransform
										.setThreadAndOriginalAsMessage(doc);
								threadId = doc.getRootElement().element(
										"thread_id")
										.attributeValue("value");
								xml = doc.asXML();

							} else {

								logger.info("Thread was not null");
								threadId = doc.getRootElement().element(
										"thread_id")
										.attributeValue("value");
								if (threadId == null) {

									XMLSchemaTransform
											.setThreadAndOriginalAsMessage(doc);
									threadId = doc.getRootElement()
											.element("thread_id")
											.attributeValue("value");
									xml = doc.asXML();
									logger.info("It was null after assigning from doc...");
								}

							}

							logger.info("New thread id : " + threadId);

						}

						if (threadId.equals("null")) {

							logger.info("It was null");
							if (doc.getRootElement().element("thread_id") == null) {
								logger.info("thread was null");
								XMLSchemaTransform
										.setThreadAndOriginalAsMessage(doc);
								threadId = doc.getRootElement().element(
										"thread_id")
										.attributeValue("value");
								xml = doc.asXML();

							} else {

								logger.info("Thread was not null");
								threadId = doc.getRootElement().element(
										"thread_id")
										.attributeValue("value");
								if (threadId == null) {

									XMLSchemaTransform
											.setThreadAndOriginalAsMessage(doc);
									threadId = doc.getRootElement()
											.element("thread_id")
											.attributeValue("value");
									xml = doc.asXML();
									logger.info("It was null after assigning from doc...");
								}

							}

							logger.info("New thread id : " + threadId);

						}

						String messageId = resultSet
								.getString("message_id");
						if (messageId == null) {
							messageId = doc.getRootElement().element(
									"message_id").attributeValue("value");

						} else if (messageId.equals("null")) {
							messageId = doc.getRootElement().element(
									"message_id").attributeValue("value");

						}

						
						Long messageLong = null;

						try {
							messageLong = Long.parseLong(messageId);
						} catch (NumberFormatException npe) {
							logger.info("Message problem");
							logger.error(npe.getMessage(), npe);
							Integer intLong = Integer.parseInt(messageId);
							messageLong = new Long(intLong);
						}

						
						Long threadLong = null;

						try {
							threadLong = Long.parseLong(threadId);
						} catch (NumberFormatException npe) {
							logger.info("Before parsing: " + threadId);
							Integer intLong = Integer.parseInt(threadId);
							threadLong = new Long(intLong);
						}

						insertStatement
								.setLong(7, Long.parseLong(threadId));
						insertStatement.setLong(8, Long
								.parseLong(messageId));
						insertStatement.setInt(9, resultSet
								.getInt("isResponse"));
						try {

							insertStatement.setTimestamp(10, resultSet
									.getTimestamp("used"));

						} catch (Exception e) {

						}

						Timestamp timeStamp = null;
						try {
							timeStamp = resultSet.getTimestamp("modified");
						} catch (Exception e) {
							timeStamp = resultSet.getTimestamp("time");
						}

						if (timeStamp != null)
							insertStatement.setTimestamp(11, timeStamp);
						else
							insertStatement.setTimestamp(11, new Timestamp(
									System.currentTimeMillis()));

						insertStatement.executeUpdate();
					}
				} catch (SQLException exc) {
				} catch (NumberFormatException exc) {
					// ignore and skip non sphere tables
				}
			}
		} catch (SQLException exc) {
			logger.error(exc.getMessage(), exc);
			logger.error("error while copying sphere tables", exc);
			throw exc;
		} finally {
			if (innerConnection != null) {
				try {
					innerConnection.close();
				} catch (SQLException ex) {
					logger.error("Can't close connection", ex);
				}
			}
			DBPool.unbind(connectionProvider);
		}
	}

	PreparedStatement getInsertStatement(Connection insertConnection)
			throws SQLException {

		StringBuffer insertBuffer = new StringBuffer();
		insertBuffer.append("insert into supraspheres ").append(
				"(sphere_id, xmldata, type, moment, thread_type, ").append(
				"create_ts, thread_id, message_id, isResponse,").append(
				"used, modified) ").append("values ").append(
				"(?,?,?,?,?,?,?,?,?,?,?)");

		return insertConnection.prepareStatement(insertBuffer.toString());
	}

	String createSelectQuery(String sphere_id) {
		StringBuffer buffer = new StringBuffer();
		buffer
				.append(
						"select recid, xmldata, type, moment, thread_type, ")
				.append("sphere, time, thread_id, message_id, isResponse, ")
				.append("used, modified ").append("from `").append(
						sphere_id).append("`");

		return buffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Vector listTables() {
		// Original method moved to OldXmlDb
		Vector ret = new Vector();
		ret.add("supraspheres");
		return ret;
	}
}