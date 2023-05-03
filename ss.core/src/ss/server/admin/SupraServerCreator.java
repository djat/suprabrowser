/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.server.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import ss.client.configuration.ApplicationConfiguration;
import ss.client.configuration.SphereConnectionUrl;
import ss.client.ui.email.EmailController;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.client.ui.viewers.NewContact;
import ss.common.CreateMembership;
import ss.common.ExceptionHandler;
import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.common.TimeLogWriter;
import ss.common.XmlDocumentUtils;
import ss.common.build.AntBuilder;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.DomainProvider;
import ss.domainmodel.configuration.EmailDomain;
import ss.domainmodel.configuration.EmailDomainsList;
import ss.framework.entities.xmlentities.XmlEntityUtils;
import ss.global.SSLogger;
import ss.global.LoggerConfiguration;
import ss.server.admin.gui.SupraServerCreateStartGUIParameters;
import ss.server.db.DBPool;
import ss.server.db.XMLDB;
import ss.server.domainmodel2.ServerDataProviderConnector;
import ss.server.networking.util.WorkflowConfigurationSetup;
import ss.util.LocationUtils;
import ss.util.VariousUtils;

/**
 * @author david TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SupraServerCreator {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger;
	
	private static final String LOGIN_PARAMS_FILE_NAME = "./create_server_params.xml";

	private final Random tableIdGenerator = new Random();

	final XMLDB xmldb = new XMLDB();

	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.DEFAULT);
		logger = ss.global.SSLogger.getLogger(SupraServerCreator.class);
		try {
			/*SupraServerCreateStartGUIParameters params = new SupraServerCreateStartGUIParameters(
					"Pancham Goyal",
					"Pancham", "Pancham",
					"127.0.0.1", 3000, 
					"Pancham",
					"Pancham", "root", "QweAsd!23"
					);
			*/
			new SupraServerCreator().createDatabase(args, false, null);
		} catch (Throwable ex) {
			logger.fatal("SupraServerCreator failed. Args "
					+ ListUtils.allValuesToString(args), ex);
		}
	}

	/**
	 * @param args
	 */
	public void createDatabase(String[] args, boolean guiCreation, SupraServerCreateStartGUIParameters guiParams) {
		if (logger == null){
			logger = ss.global.SSLogger.getLogger(SupraServerCreator.class);
		}
		final TimeLogWriter timeLogWriter = new TimeLogWriter(
				SupraServerCreator.class, "createDatabase started");
		String contactName;
		String loginName;
		String supraSphereName;
		String mainDomain;
		String passphrase;
		int port = 3000;
		String databaseName;
		String databaseUserName = "root";
		String databasePassword = "";		
		if (guiCreation) {
			if (guiParams == null){
				logger.fatal("guiParams is null");
				return;
			}
			contactName = guiParams.getContactName();
			loginName = guiParams.getLoginName();
			supraSphereName = guiParams.getSupraSphereName();
			mainDomain = guiParams.getMainDomain();
			passphrase = guiParams.getPassphrase();
			databaseName = guiParams.getDatabaseName();
			port = guiParams.getPort();
			databaseUserName = guiParams.getDatabaseUserName(); 
			databasePassword = guiParams.getDatabasePassword();
		} else {
			final File file = new File(LOGIN_PARAMS_FILE_NAME);
			if (!file.exists()) {
				logger.fatal("Params file not found!");
				return;
			}
			final Document document;
			try {
				document = XmlDocumentUtils.load(file);
			} catch (DocumentException ex) {
				logger.fatal("Can't restore params", ex);
				return;
			}
			final CreateServerParams params = CreateServerParams.wrap(document);
			if (!checkParamsNotNull(params)) {
				return;
			}
			databaseName = params.getDatabaseName();
			contactName = params.getContactName();
			loginName = params.getLoginName();
			supraSphereName = params.getSupraSphereName();
			mainDomain = params.getMainDomain();
			passphrase = params.getPassphrase();
			if (StringUtils.isBlank(passphrase)) {
				logger.warn("Passphrase from file is blank, try command line");
				passphrase = args[0];
				if (StringUtils.isBlank(passphrase)) {
					logger.fatal("Passphrase can not be blank");
					return;
				}
			}
		}
		logger.info("contactName: " + contactName);
		logger.info("loginName: " + loginName);
		logger.info("supraSphereName: " + supraSphereName);
		logger.info("Main Domain: " + mainDomain);
		
		logger.info("port: " + port);
		logger.info("databaseName: " + databaseName);
		logger.info("databaseUserName: " + databaseUserName);
		logger.info("databasePassword: " + databasePassword);
		
		timeLogWriter.logAndRefresh("Params loaded");
		try {
			createDynClient( mainDomain, port, supraSphereName );
			createDynServer( port, databaseName, databaseUserName, databasePassword );
			logger.info("contact name: " + contactName);
			final SupraServerCreator ssc = new SupraServerCreator();
			SsDomain.initialize(new ServerDataProviderConnector());
			ss.common.LocationUtils.init();
			timeLogWriter.logAndRefresh("SsDomain initialized");

			ssc.createProperties("mysql");
			DBPool.recreate();
			ssc.createDatabase(databaseName);
			ssc.createProperties(databaseName);
			DBPool.recreate();
			this.xmldb.loadProperties();

			// Creating other tables
			ss.server.domainmodel2.db.TableStructureManager.INSTANCE
					.recreateDm2Tables();
			ss.server.errorreporting.RecreateErrorReportingTables
					.recreateErrorReportingTables();

			ssc.createTable("supraspheres");
			timeLogWriter.logAndRefresh("Database created and properties loaded");

			Document doc = ssc.createSupraSphereDocument(supraSphereName,
					contactName, loginName);
			String apath = "//suprasphere/member[@contact_name=\""
					+ contactName + "\"]/sphere[@display_name=\"" + contactName
					+ "\"]";
			logger.info("APATH: " + apath);
			final String personalSphere = ((Element) doc.selectObject(apath))
					.attributeValue("system_name");
			logger.info("personal sphere system id: " + personalSphere);
			timeLogWriter.logAndRefresh("SupraSphere created");

			SphereStatement emailSphere = EmailController
					.createEmailSphereStatement(loginName, contactName,
							supraSphereName);
			doc.getRootElement().element("member").addElement("sphere")
					.addAttribute("display_name", emailSphere.getDisplayName())
					.addAttribute("system_name", emailSphere.getSystemName())
					.addAttribute("sphere_type", "group").addAttribute(
							"default_delivery", "normal").addAttribute(
							"enabled", "true");

			setDomain(mainDomain);
			final SupraSphereStatement supra = SupraSphereStatement.wrap(doc);
			SphereEmail sphereEmail = new SphereEmail();
			sphereEmail.setSphereId(emailSphere.getSystemName());
			SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
					SpherePossibleEmailsSet.createAddressString(contactName,
							loginName, DomainProvider.getDefaultDomain()));
			sphereEmail.setEmailNames(set);
			sphereEmail.setEnabled(true);
			sphereEmail.setIsMessageIdAdd(false);
			supra.getSpheresEmails().put(sphereEmail);

			SphereEmail supraSphereEmail = new SphereEmail();
			supraSphereEmail.setSphereId(supraSphereName);
			SpherePossibleEmailsSet supraSet = new SpherePossibleEmailsSet(
					SpherePossibleEmailsSet.createAddressString(
							supraSphereName, supraSphereName, DomainProvider.getDefaultDomain()));
			supraSphereEmail.setEmailNames(supraSet);
			supraSphereEmail.setEnabled(true);
			supraSphereEmail.setIsMessageIdAdd(true);
			supra.getSpheresEmails().put(supraSphereEmail);
			
			timeLogWriter.logAndRefresh("Email aliases setted");
			
			final String cliURL = createURLFromDynClient();
			final String errorSphereName = "Error reporting sphere";
			final String errorSphereId = Long.toString(Math.abs((new Random()).nextLong()));
			doc.getRootElement().element("member").addElement("sphere")
			.addAttribute("display_name", errorSphereName)
			.addAttribute("system_name", errorSphereId)
			.addAttribute("sphere_type", "group").addAttribute(
					"default_delivery", "normal").addAttribute(
					"enabled", "true");

			this.xmldb.insertDoc(doc, supraSphereName);
			createErrorReportingSphere(ssc, supraSphereName, errorSphereName, errorSphereId, contactName, loginName, cliURL, personalSphere);

			doc = ssc.createMembership(contactName, loginName, passphrase);
			this.xmldb.insertDoc(doc, supraSphereName);
			
			timeLogWriter.logAndRefresh("Membership created");

			Document cliLogin = DocumentHelper.createDocument();
			cliLogin.setRootElement(new DefaultElement("login_info"));
			cliLogin.getRootElement().addElement("prev_logins");
			cliLogin.getRootElement().element("prev_logins")
					.addElement("login").addAttribute("moment", "")
					.addAttribute("username", loginName).addAttribute(
							"passphrase",
							doc.getRootElement().element("machine_pass")
									.getText());

			String fsep = System.getProperty("file.separator");

			final File lastLoginFile = LocationUtils.getLastLoginFile();
			try {
				XmlDocumentUtils.save(lastLoginFile, cliLogin);
			} catch (DocumentException ex) {
				ExceptionHandler.handleException(SupraServerCreator.class, ex);
			}
			
			timeLogWriter.logAndRefresh("properties file saved");

			doc = ssc.createMembership(supraSphereName, supraSphereName,
					supraSphereName);
			this.xmldb.insertDoc(doc, supraSphereName);
			
			timeLogWriter.logAndRefresh("Another membership created");
			
			cliLogin = DocumentHelper.createDocument();
			cliLogin.setRootElement(new DefaultElement("login_info"));
			cliLogin.getRootElement().addElement("prev_logins");
			cliLogin.getRootElement().element("prev_logins")
					.addElement("login").addAttribute("moment", "")
					.addAttribute("username", supraSphereName).addAttribute(
							"passphrase",
							doc.getRootElement().element("machine_pass")
									.getText());

			final File serverLoginFile = new File(System
					.getProperty("user.dir")
					+ fsep + "server_login.xml");
			try {
				XmlDocumentUtils.save(serverLoginFile, cliLogin);
			} catch (DocumentException ex) {
				ExceptionHandler.handleException(SupraServerCreator.class, ex);
			}
			
			timeLogWriter.logAndRefresh("Another properties file saved");
			
			doc = ssc.createSphereDocument(supraSphereName, contactName,
					loginName);
			String sphereId = doc.getRootElement()
					.attributeValue("system_name");
			String displayName = doc.getRootElement().attributeValue(
					"display_name");

			doc.getRootElement().addElement("locations").addElement("sphere")
					.addAttribute("URL", cliURL).addAttribute("ex_system",
							sphereId).addAttribute("ex_display", displayName)
					.addAttribute(
							"ex_message",
							doc.getRootElement().element("message_id")
									.attributeValue("value"));
			doc.getRootElement().element("locations").addElement("sphere")
					.addAttribute("URL", cliURL).addAttribute("ex_system",
							personalSphere).addAttribute("ex_display",
							contactName).addAttribute(
							"ex_message",
							doc.getRootElement().element("message_id")
									.attributeValue("value"));

			this.xmldb.insertDoc(doc, supraSphereName);

			// xmldb.insertDoc(doc,sphereId);

			this.xmldb.insertDoc(doc, personalSphere);
			
			timeLogWriter.logAndRefresh("Sphere doc created");

			WorkflowConfigurationSetup.setupWorkflowConfigurationForSphere(doc);
			logger.info("SPHERE ID : " + sphereId);
			
			timeLogWriter.logAndRefresh("Workflow configuration setted up");

			// logger.info("DONE 2: "+doc.asXML());
			//        
			Document conDoc = ssc.createContact(contactName, loginName);

			conDoc.getRootElement().addElement("locations")
					.addElement("sphere").addAttribute("URL", cliURL)
					.addAttribute("ex_system", sphereId).addAttribute(
							"ex_display", displayName).addAttribute(
							"ex_message",
							conDoc.getRootElement().element("message_id")
									.attributeValue("value"));
			conDoc.getRootElement().element("locations").addElement("sphere")
					.addAttribute("URL", cliURL).addAttribute("ex_system",
							personalSphere).addAttribute("ex_display",
							contactName).addAttribute(
							"ex_message",
							conDoc.getRootElement().element("message_id")
									.attributeValue("value"));

			timeLogWriter.logAndRefresh("Contact created");
			
			this.xmldb.insertDoc(conDoc, supraSphereName);
			// xmldb.insertDoc(conDoc,sphereId);

			this.xmldb.insertDoc(conDoc, personalSphere);

			this.xmldb.insertDoc(emailSphere.getBindedDocument(), emailSphere
					.getSystemName());

			this.xmldb.insertDoc(emailSphere.getBindedDocument(),
					supraSphereName);
			
			timeLogWriter.logAndRefresh("docs inserted");
			
			WorkflowConfigurationSetup
					.setupWorkflowConfigurationForSphere(emailSphere);

			this.xmldb.insertDoc(conDoc, emailSphere.getSystemName());
			this.xmldb.insertDoc(conDoc, errorSphereId);
			
			timeLogWriter.logAndRefresh("Workflow for email box setted up");

			AntBuilder ab = new AntBuilder();
			ab.setBaseAndSrcDirs();
			ab.removeOtherPlatformSWTJar();

			/* this function automatically creates the parent directories */
			File f = VariousUtils.getSupraFile("roots" + fsep + supraSphereName
					+ fsep + "Assets" + fsep + "Library", ".tempFile");
			f = VariousUtils.getSupraFile("roots" + fsep + supraSphereName
					+ fsep + "File", ".tempFile");

			f = VariousUtils.getSupraFile("urls");
			if (!f.exists()) {
				f.mkdir();
			}

			logger.warn("Setting supraspherename: " + supraSphereName);
			ssc.saveSupraSphereToDynClient(supraSphereName);
			
			timeLogWriter.logAndRefresh("Ant builder finished");

			try {
				this.xmldb.getConvertor().upgradeSphereTableStructure();
			} catch (SQLException e) {
				logger.error("", e);
			}
			
			timeLogWriter.logTime("finished");
			//        
			// logger.info("DOC: "+conDoc.asXML());

		} catch (ArrayIndexOutOfBoundsException aie) {
			logger.error("", aie);
		} catch (SQLException ex) {
			logger.error("SQL exception", ex);
		}
	}

	/**
	 * @param params
	 */
	private boolean checkParamsNotNull(CreateServerParams params) {
		if (StringUtils.isBlank(params.getContactName())){
			logger.fatal("Contact name can not be blank");
			return false;
		}
		if (StringUtils.isBlank(params.getDatabaseName())){
			logger.fatal("Database name can not be blank");
			return false;
		}
		if (StringUtils.isBlank(params.getLoginName())){
			logger.fatal("Login can not be blank");
			return false;
		}
		if (StringUtils.isBlank(params.getSupraSphereName())){
			logger.fatal("SupraSphere name can not be blank");
			return false;
		}
		if (StringUtils.isBlank(params.getMainDomain())){
			logger.fatal("MainDomain name can not be blank");
			return false;
		}
		return true;
	}

	public SupraServerCreator() {
	}

	public void createProperties(String databaseName) {
		File file = VariousUtils.getSupraFile("dyn_server.xml");
		Document doc = loadDynFile(file);
		if (doc != null) {
			String finalDBName = convertNameToDatabase(databaseName);
			String dbUser = doc.getRootElement().element("mysql")
					.attributeValue("db_user");
			String dbPass = doc.getRootElement().element("mysql")
					.attributeValue("db_pass");
			String newURL = createDbURL(finalDBName, dbUser, dbPass);
			try {
				doc.getRootElement().element("mysql").addAttribute("url",
						newURL);
			} catch (Exception e) {
				logger.error("", e);
			}
			saveDynServer(file, doc);
		}
	}

	private void createDynServer( int port, String dataBaseName, String userName, String password ) {
		final File file = VariousUtils.getSupraFile("dyn_server.xml");
		DynServerEntity dynServerEntity = new DynServerEntity();
		dynServerEntity.initalize(port, dataBaseName, userName, password);
		XmlEntityUtils.safeSave(file, dynServerEntity);
		System.out.println( "Recreate dyn_server.xml " + file );		
	}
	
	private void createDynClient( String address, int port, String supraSphereName ) {
		final File file = VariousUtils.getSupraFile("dyn_client.xml");
		ApplicationConfiguration clientApplicationConfiguration = new ApplicationConfiguration( file );
		final SphereConnectionUrl connectionUrl = new SphereConnectionUrl();
		connectionUrl.setPort(port);
		connectionUrl.setServer(address);
		connectionUrl.setSphereId(supraSphereName);
		clientApplicationConfiguration.setConnectionUrl(connectionUrl);
		clientApplicationConfiguration.save();		
		System.out.println( "Recreate dyn_client.xml " + file );
	}
	
	/**
	 * @param doc
	 * @param file
	 * @return
	 */
	private Document loadDynFile(File file) {
		Document doc = null;
		SAXReader reader1 = new SAXReader();
		try {
			doc = reader1.read(file);
		} catch (DocumentException e) {
			logger.error("", e);
		}
		return doc;
	}

	/**
	 * @param file
	 * @param doc
	 */
	private boolean saveDynServer(File file, Document doc) {
		boolean saved = false;
		OutputFormat format = OutputFormat.createPrettyPrint();
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();
			saved = true;
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
		return saved;
	}

	/**
	 * @param finalDBName
	 * @param dbUser
	 * @param dbPass
	 * @return
	 */
	static String createDbURL(String finalDBName, String dbUser, String dbPass) {
		String newURL = null;
		if (dbPass.length() > 0) {
			newURL = "jdbc:mysql://127.0.0.1/" + finalDBName
					+ "?autoReconnect=true&characterEncoding=Cp1252&user="
					+ dbUser + "&password=" + dbPass;
		} else {
			newURL = "jdbc:mysql://127.0.0.1/" + finalDBName
					+ "?autoReconnect=true&characterEncoding=Cp1252&user="
					+ dbUser;
		}
		return newURL;
	}

	public synchronized long getNextTableId() {
		return Math.abs(this.tableIdGenerator.nextLong());
	}

	public static String createURLFromDynClient() {
		String cliURL = null;
		File file = VariousUtils.getSupraFile("dyn_client.xml");
		SAXReader reader1 = new SAXReader();
		Document doc;
		try {
			doc = reader1.read(file);

			cliURL = "sphere::"
					+ doc.getRootElement().element("address").attributeValue(
							"value")
					+ ":"
					+ doc.getRootElement().element("port").attributeValue(
							"value")
					+ ","
					+ doc.getRootElement().element("supra_sphere")
							.attributeValue("value");

		} catch (DocumentException e) {
			logger.error("", e);
		}
		return cliURL;
	}

	public void saveSupraSphereToDynClient(String supraSphere) {
		File file = VariousUtils.getSupraFile("dyn_client.xml");
		SAXReader reader1 = new SAXReader();
		Document doc = null;
		try {
			doc = reader1.read(file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (doc == null) {
			/*
			 * the dyn_client file was not there, so create a doc from the
			 * string VariousUtils.
			 */
			try {
				doc = reader1.read(new ByteArrayInputStream(
						VariousUtils.DYN_CLIENT_TEMPLATE.getBytes()));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		doc.getRootElement().element("supra_sphere").addAttribute("value",
				supraSphere);
		try {
			FileOutputStream fout = new FileOutputStream(file);
			XMLWriter writer = new XMLWriter(fout, format);
			writer.write(doc);
			writer.close();
			fout.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public Document createMembership(String contactName, String username,
			String passphrase) {
		CreateMembership membership = new CreateMembership();

		Document memDoc = membership.createMember(contactName, username,
				passphrase);

		Document throwAway = membership.createMember("asdf", "asdf", "asdf");
		String verifier = throwAway.getRootElement().element("verifier")
				.getText();

		Document forMachine = membership.createMember(contactName, username,
				verifier);

		String machineVerifier = forMachine.getRootElement()
				.element("verifier").getText();
		String machineSalt = forMachine.getRootElement().element("verifier")
				.attributeValue("salt");

		memDoc.getRootElement().addElement("machine_verifier").addAttribute(
				"salt", machineSalt).addAttribute("profile_id",
				"0000000000000000000").setText(machineVerifier);
		memDoc.getRootElement().addElement("machine_pass").setText(verifier);

		Element root = memDoc.getRootElement();
		long longnum = System.currentTimeMillis();

		String message_id = (Long.toString(longnum));

		Date current = new Date();
		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("subject").addAttribute("value",
				"New Membership: " + username);
		root.addElement("giver").addAttribute("value", contactName);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);
		return memDoc;
	}

	public boolean createTable(String supraSphereName) {
		boolean created = false;
		XMLDB xmldb = new XMLDB();
		created = xmldb.createTable(supraSphereName);
		logger.info("returning created; " + created);
		return created;
	}

	public Document createContact(String contactName, String username) {

		// nc.createSShell();
		/*
		 * FIXME - This function requires that there be spaces in the
		 * contactName.
		 */
		StringTokenizer st = new StringTokenizer(contactName, " ");

		String firstName = st.nextToken();
		String lastName = st.nextToken();

		Document contactDoc = NewContact.XMLContactDoc(username, contactName,
				firstName, lastName);

		Element root = contactDoc.getRootElement();
		// long longnum = System.currentTimeMillis();
		String message_id = VariousUtils.createMessageId();

		Date current = new Date();
		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("subject").addAttribute("value",
				"New Contact: " + contactName);
		root.element("giver").addAttribute("value", contactName);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);

		return contactDoc;
	}

	/* FIXME - The data model for sphere documents is embedded in this code. */
	/* Needs separation of data and code (or at least documentation). */
	public Document createSphereDocument(String sphereName, String contactName,
			String loginName) {

		Document doc = null;

		doc = DocumentHelper.createDocument();
		doc.addElement("sphere");
		Element root = doc.getRootElement();
		doc.getRootElement().addAttribute("display_name", sphereName)
				.addAttribute("system_name", sphereName).addAttribute("sphere_type", "group");

		root.addElement("inherit").addElement("data").addAttribute("value",
				sphereName);
		// .addElement("sphere").addAttribute("display_name",sphereName).addAttribute("system_name",sphereName);
		root.addElement("ui").addElement("middle_chat").addAttribute("value",
				"true");
		root.element("ui").addElement("tree_order").addAttribute("value",
				"top_down");
		root.addElement("subject").addAttribute("value",
				"New Sphere: " + sphereName);
		root.addElement("giver").addAttribute("value", contactName);

		Date current = new Date();
		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		// long longnum = System.currentTimeMillis();

		String message_id = VariousUtils.createMessageId();

		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);
		root.addElement("status").addAttribute("value", "confirmed");
		root.addElement("type").addAttribute("value", "sphere");
		root.addElement("expiration").addAttribute("value", "all");
		root.addElement("default_delivery").addAttribute("value", "normal");
		root.addElement("default_type").addAttribute("value", "terse");
		root.addElement("thread_type").addAttribute("value", "sphere");

		root.addElement("thread_types");
		root.element("thread_types").addElement("terse").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("message").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("bookmark").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("externalemail").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("contact").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("rss").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("keywords").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("file").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.addElement("thread_types").addElement("sphere").addAttribute(
				"modify", "own").addAttribute("enabled", "true");

		root.addElement("voting_model").addElement("tally").addAttribute(
				"number", "0.0").addAttribute("value", "0.0");
		root.addElement("body").addElement("orig_body");
		root.addElement("member").addAttribute("contact_name", contactName)
				.addAttribute("login_name", loginName);

		return doc;
	}

	/*
	 * FIXME - Needs separation of data and code (or at least documentation).
	 */
	public Document createSupraSphereDocument(String supraSphereName,
			String contactName, String loginName) {

		Document doc = null;

		doc = DocumentHelper.createDocument();
		doc.addElement("suprasphere");
		Element root = doc.getRootElement();

		doc.getRootElement().addAttribute("name", supraSphereName);

		root.addElement("inherit").addElement("data").addElement("sphere")
				.addAttribute("display_name", supraSphereName).addAttribute(
						"system_name", supraSphereName);
		root.addElement("ui").addElement("middle_chat").addAttribute("value",
				"true");
		root.element("ui").addElement("tree_order").addAttribute("value",
				"top_down");

		SupraSphereStatement supraStatement = SupraSphereStatement.wrap(doc);
		supraStatement.getAdmins().addAdmin(loginName, contactName);
		supraStatement.getAdmins().makePrimary(loginName, contactName);
//		root.addElement("admin").addElement("supra").addAttribute(
//				"contact_name", contactName).addAttribute("login_name",
//				loginName);

		Date current = new Date();

		String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
				current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		// long longnum = System.currentTimeMillis();

		String message_id = VariousUtils.createMessageId();

		// String response_id = null;

		// root.addElement("email").addElement("sphere_domain").addAttribute("value","suprasecure.com");
		root.addElement("email").addElement("sphere_domain").addAttribute(
				"value", "$loginAddress");
		root.addElement("original_id").addAttribute("value", message_id);
		root.addElement("message_id").addAttribute("value", message_id);
		root.addElement("thread_id").addAttribute("value", message_id);
		root.addElement("last_updated").addAttribute("value", moment);
		root.addElement("moment").addAttribute("value", moment);
		root.addElement("status").addAttribute("value", "confirmed");
		root.addElement("type").addAttribute("value", "suprasphere");
		root.addElement("thread_type").addAttribute("value", "suprasphere");
		root.addElement("subject").addAttribute("value",
				"New SupraSphere: " + supraSphereName);
		root.addElement("giver").addAttribute("value", contactName);
		root.addElement("voting_model").addElement("tally").addAttribute(
				"number", "0.0").addAttribute("value", "0.0");
		root.addElement("body").addElement("orig_body");

		root.addElement("member").addAttribute("contact_name", contactName)
				.addAttribute("login_name", loginName);
		root.element("member").addElement("sphere_core").addAttribute(
				"display_name", supraSphereName).addAttribute("system_name",
				supraSphereName).addAttribute("sphere_type", "group");
		root.element("member").addElement("login_sphere").addAttribute(
				"display_name", supraSphereName).addAttribute("system_name",
				supraSphereName).addAttribute("sphere_type", "group");
		root.element("member").addElement("sphere").addAttribute(
				"display_name", supraSphereName).addAttribute("system_name",
				supraSphereName).addAttribute("sphere_type", "group")
				.addAttribute("default_delivery", "normal").addAttribute(
						"enabled", "true");
		String tableId = new Long(getNextTableId()).toString();
		root.element("member").addElement("sphere").addAttribute(
				"display_name", contactName).addAttribute("system_name",
				tableId).addAttribute("sphere_type", "member").addAttribute(
				"default_delivery", "normal").addAttribute("enabled", "true");

		return doc;
	}

	public String convertNameToDatabase(String databaseName) {
		StringTokenizer st = new StringTokenizer(databaseName, " ");
		String finalDBName = "";
		int i = 0;
		while (st.hasMoreTokens()) {
			if (i == 0) {
				finalDBName = st.nextToken();
			}
			if (i >= 1) {
				finalDBName = finalDBName + "_" + st.nextToken();
			}
			i++;

		}
		if (finalDBName.length() == 0) {
			finalDBName = databaseName;
		}
		finalDBName = finalDBName.toLowerCase();
		return finalDBName;
	}

	public void createDatabase(String databaseName) throws SQLException {
		XMLDB xmldb = new XMLDB();
		String finalDBName = convertNameToDatabase(databaseName);
		xmldb.createDatabase(finalDBName);

	}
	
	private void setDomain(final String mainDomain) {
		ConfigurationValue configuration = SsDomain.CONFIGURATION
				.getMainConfigurationValue();
		EmailDomainsList domains = configuration.getDomains();
		EmailDomain domain = new EmailDomain();
		domain.setDomain(mainDomain);
		domains.put(domain);		
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
	}
	
	private void createErrorReportingSphere(SupraServerCreator ssc, String supraSphereName, String sphereName,
			String errorSphereId, String contactName,
			String loginName, String cliURL, String personalSphere) {
		Document doc = ssc.createSphereDocument(sphereName, contactName,
				loginName);
		SphereStatement.wrap(doc).setSystemName(errorSphereId);
		String sphereId = errorSphereId;
		String displayName = doc.getRootElement().attributeValue(
				"display_name");

		doc.getRootElement().addElement("locations").addElement("sphere")
				.addAttribute("URL", cliURL).addAttribute("ex_system",
						sphereId).addAttribute("ex_display", displayName)
				.addAttribute(
						"ex_message",
						doc.getRootElement().element("message_id")
								.attributeValue("value"));
		doc.getRootElement().element("locations").addElement("sphere")
				.addAttribute("URL", cliURL).addAttribute("ex_system",
						personalSphere).addAttribute("ex_display",
						contactName).addAttribute(
						"ex_message",
						doc.getRootElement().element("message_id")
								.attributeValue("value"));

		this.xmldb.insertDoc(doc, supraSphereName);
		this.xmldb.insertDoc(doc, sphereId);
		this.xmldb.insertDoc(doc, personalSphere);
	}
}
