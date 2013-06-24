package ss.server.networking;

/*
 This matches DialogsMainCli.java on the server end. It has a corresponding method for each protocol
 known. It creates a handler for each connection in the static "handlers" Vector, with the unique
 session id for each client used in the header by calling "setName".
 When it's created, it creates an instance of the database XMLDB and a "VerifyAuth" class for each user,
 which is used to check privileges with xpath in the form "//asset_type/privilege_request".
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.DmpFilter;
import ss.common.ArgumentNullPointerException;
import ss.common.CompareUtils;
import ss.common.DmCommand;
import ss.common.ExceptionHandler;
import ss.common.GenericXMLDocument;
import ss.common.ListUtils;
import ss.common.MapUtils;
import ss.common.SSProtocolConstants;
import ss.common.SphereDefinitionCreator;
import ss.common.StringUtils;
import ss.common.TimeLogWriter;
import ss.common.VerifyAuth;
import ss.common.converter.ConvertingElementFactory;
import ss.common.converter.DocumentConverterAndIndexer;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.file.DefaultDataForSpecificFileProcessingProvider;
import ss.common.file.ParentStatementData;
import ss.common.file.ReturnData;
import ss.common.file.SpecificFileProcessor;
import ss.common.networking2.ProtocolUtils;
import ss.common.textformatting.simple.ParsingResult;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.LoginSphere;
import ss.domainmodel.MemberRelation;
import ss.domainmodel.PrivateSphereReference;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.ProtocolLifetimeAdapter;
import ss.framework.networking2.ProtocolLifetimeEvent;
import ss.framework.networking2.VoidCommandHandler;
import ss.global.SSLogger;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.rss.RSSParser;
import ss.search.URLParser;
import ss.server.MethodProcessing;
import ss.server.db.XMLDB;
import ss.server.db.XMLDBOld;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.email.EmailDeliverer;
import ss.server.networking.processing.FileProcessor;
import ss.server.networking.protocol.actions.ActionRegistrator;
import ss.server.networking.protocol.getters.GettersRegistrator;
import ss.server.networking.util.Expression;
import ss.server.networking.util.Filter;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.server.networking.util.UnaryOperation;
import ss.server.networking2.ServerProtocolManager;
import ss.smtp.defaultforwarding.EmailForwarder;
import ss.smtp.defaultforwarding.ForwardingElement;
import ss.smtp.reciever.EmailProcessor;
import ss.smtp.reciever.MailData;
import ss.smtp.reciever.RecieveList;
import ss.smtp.reciever.Reciever;
import ss.smtp.reciever.file.CreatedFileInfo;
import ss.util.EmailUtils;
import ss.util.NameTranslation;
import ss.util.SessionConstants;
import ss.util.StringProcessor;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

/**
 * Description of the Class
 * 
 * @author david
 * @created September 19, 2003
 */
public class DialogsMainPeer {

	private final static String fsep;
	private final static String bdir;

	static {
		fsep = System.getProperty("file.separator");
		bdir = System.getProperty("user.dir") + fsep + "roots";
	}

	private final Hashtable startUpSession;

	// TODO: find where is doc root setupped
	public final Document currentQueries = DocumentHelper
			.createDocument(DocumentHelper.createElement("queries"));

	private static Hashtable<String, String> passphraseChangeSessions = new Hashtable<String, String>();

	private static final Logger logger = SSLogger
			.getLogger(DialogsMainPeer.class);

	private VerifyAuth verifyAuth = null;

	private final XMLDB xmldb;

	private final HandlerCollection handlers = new HandlerCollection(this);

	private final Hashtable<HandlerKey, String> nameTable = new Hashtable<HandlerKey, String>();

	private String name;

	private Hashtable currentCommandSession = null;

	private final Protocol protocol;

	private final DmCommandHandler dmCommandHandler = new DmCommandHandler();

	private final SphereDefinitionProcessor sphereDefinitionProcessor = new SphereDefinitionProcessor();

	public DialogsMainPeer(Hashtable session, DataInputStream cdatain,
			DataOutputStream cdataout) {
		this.startUpSession = session;
		if (this.startUpSession == null) {
			throw new ArgumentNullPointerException("startUpSession");
		}
		this.protocol = new Protocol(cdatain, cdataout, ProtocolUtils
				.generateProtocolDisplayName("DMP", (String) session
						.get(SC.USERNAME)));
		this.protocol.registerHandler(this.dmCommandHandler);
		ActionRegistrator actionRegistrator = new ActionRegistrator(this,
				this.protocol);
		actionRegistrator.registerHandlers();
		GettersRegistrator getterRegistrator = new GettersRegistrator(this,
				this.protocol);
		getterRegistrator.registerHandlers();
		this.protocol.addProtocolListener(new ProtocolLifetimeAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see ss.common.networking2.ProtocolLifetimeAdapter#beginClose(ss.common.networking2.ProtocolLifetimeEvent)
			 */
			@Override
			public void beginClose(ProtocolLifetimeEvent e) {
				super.beginClose(e);
				dispose();
			}

		});
		setVerifyAuth(new VerifyAuth(this.startUpSession));
		this.xmldb = new XMLDB(this.startUpSession);
		getXmldb().setVerifyAuth(getVerifyAuth());
		try {
			SupraSphereProvider.INSTANCE.configureVerifyAuth( getVerifyAuth() );
			String username = (String) this.startUpSession.get(SC.USERNAME);
			if (!username.equals((String) this.startUpSession
					.get(SC.SUPRA_SPHERE))) {
				String loginSphere = getXmldb().getUtils()
						.getLoginSphereSystemName(username);
				getVerifyAuth().setContactDocument(
						getXmldb().getContactDoc(loginSphere, username));
			}
		} catch (NullPointerException exc) {
			logger.error("NullPointer Exception in DialogsMainPeer", exc);
		} 
	}

	/**
	 * @return Gets the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name
	 * 
	 * @param name
	 *            name
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the hashOf attribute of the DialogsMainPeer object
	 * 
	 * @param s
	 *            Description of the Parameter
	 * @return The hashOf value
	 */

	public void setQueryMoments(String queryId, String relativeStartMoment,
			String finalEndMoment, String currentPage) {

		logger.info("if query is null something wrong: " + queryId);
		if (queryId != null) {
			this.currentQueries.getRootElement().addElement("query")
					.addAttribute("id", queryId).addAttribute(
							"relativeStartMoment", relativeStartMoment)
					.addAttribute(SC.FINAL_END_MOMENT, finalEndMoment)
					.addAttribute("currentPage", currentPage);
		}

	}

	/**
	 * @param command
	 */
	private void handleCommand(DmCommand command) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("At least got Protocol: " + command);
			}
			final Hashtable commandSession = ((Hashtable) command.getData()
					.get(SC.SESSION));
			if (commandSession == null) {
				logger.info(String.format(
						"Session recived from command %s is null",
						new Object[] { command }));
			}
			this.setCurrentCommandSession(commandSession);
			if (this.handlers.handle(command)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Packet handled :" + command);
				}
			} else {
				logger.error("VERY IMPORTANT, THIS HAD NO MATCH: " + command);
			}
		} catch (RuntimeException ex) {
			logger.fatal("Fail to handle command " + command, ex);
		}
	}

	/**
	 * @param currentCommandSession
	 */
	private void setCurrentCommandSession(Hashtable currentCommandSession) {
		this.currentCommandSession = currentCommandSession;
	}

	private void updatePresenceAboutLogoff() {
		String loginName = get(HandlerKey.USERNAME);
		final VerifyAuth localVerifyAuth = getVerifyAuth();
		String contactName = localVerifyAuth != null ? localVerifyAuth
				.getRealName(loginName) : null;
		if (loginName == null || contactName == null) {
			logger.warn("Can't updatePresenceAboutLogoff" + loginName + ", "
					+ contactName);
			return;
		}
		try {
			if (!isConnectedAnotherLocation(contactName)) {
				FilteredHandlers filteredHandlers = FilteredHandlers
						.getExactNotHandlersForHandler(this);
				logger.info("CHANGE_PRESENCE removed " + contactName);
				for (DialogsMainPeer handler : filteredHandlers) {
					handler.sendRefreshPresence(contactName, false);
				}
			} else {
				logger.info("have another connection for " + loginName + ", "
						+ contactName);
			}
		} catch (RuntimeException ex) {
			logger.error("updatePresenceAboutLogoff failed", ex);
		}
	}

	/**
	 * @deprecated
	 * @param session
	 * @param sphereDefinition
	 * @param sphereId
	 * @param currentMoment
	 * @param personalSphere
	 */
	@SuppressWarnings("unused")
	private void processSphereStats(Hashtable session,
			Document sphereDefinition, String sphereId, String currentMoment,
			String personalSphere) {

		Document statsDoc = getXmldb().getStatisticsDoc(personalSphere,
				sphereId);

		// if this is the first time the user uses SupraSphere setup the stats
		if (statsDoc == null) {

			GenericXMLDocument genericDoc = new GenericXMLDocument();
			Document newStatsDoc = genericDoc.XMLDoc((String) session
					.get("real_name")
					+ "'s Stats");
			newStatsDoc.getRootElement().addElement("type").addAttribute(
					"value", "stats");
			newStatsDoc.getRootElement().addElement("thread_type")
					.addAttribute("value", "stats");
			newStatsDoc.getRootElement().addElement("launched").addAttribute(
					"sphere_id", (String) session.get("sphere_id"))
					.addAttribute("username", (String) session.get("username"))
					.addAttribute("contact_name",
							(String) session.get("real_name")).addAttribute(
							"moment", currentMoment);
			newStatsDoc.getRootElement().addElement("last_launched")
					.addAttribute("sphere_id",
							(String) session.get("sphere_id")).addAttribute(
							"username", (String) session.get("username"))
					.addAttribute("contact_name",
							(String) session.get("real_name")).addAttribute(
							"moment", currentMoment);

			String repliesToMine = "0";
			newStatsDoc.getRootElement().addElement("since_local_mark")
					.addElement("id").addAttribute("value", sphereId);
			newStatsDoc.getRootElement().element("since_local_mark")
					.addAttribute(
							"total_in_sphere",
							(new Integer(getXmldb().countDocs(sphereId)))
									.toString()).addAttribute(
							"replies_to_mine", repliesToMine).addAttribute(
							"since_mark", "0").addAttribute(
							"since_last_launched", "0");

			Element sphereDef = (Element) sphereDefinition.getRootElement()
					.clone();

			if (sphereDef.element("thread_type") != null) {
				sphereDef.element("thread_type").detach();
			}
			if (sphereDef.element("type") != null) {
				sphereDef.element("type").detach();
			}
			if (sphereDef.element("thread_id") != null) {
				sphereDef.element("thread_id").detach();
			}
			if (sphereDef.element("message_id") != null) {
				sphereDef.element("message_id").detach();
			}
			if (sphereDef.element("current_sphere") != null) {
				sphereDef.element("current_sphere").detach();
			}
			if (sphereDef.element("original_id") != null) {
				sphereDef.element("original_id").detach();
			}
			newStatsDoc.getRootElement().addElement("last_query")
					.add(sphereDef);
			getXmldb().insertDoc(newStatsDoc, personalSphere);

		} else {
			// Process the stats of an existing user.

			statsDoc.getRootElement().addElement("launched").addAttribute(
					"sphere_id", (String) session.get("sphere_id"))
					.addAttribute("username", (String) session.get("username"))
					.addAttribute("contact_name",
							(String) session.get("real_name")).addAttribute(
							"moment", currentMoment);
			if (statsDoc.getRootElement().element("last_launched") != null) {
				statsDoc.getRootElement().element("last_launched").detach();

			}
			if (statsDoc.getRootElement().element("last_query") != null) {
				statsDoc.getRootElement().element("last_query").detach();

			}
			Element sphereDef = (Element) sphereDefinition.getRootElement()
					.clone();

			if (sphereDef.element("thread_type") != null) {
				sphereDef.element("thread_type").detach();
			}
			if (sphereDef.element("type") != null) {
				sphereDef.element("type").detach();
			}
			if (sphereDef.element("thread_id") != null) {
				sphereDef.element("thread_id").detach();
			}
			if (sphereDef.element("message_id") != null) {
				sphereDef.element("message_id").detach();
			}
			if (sphereDef.element("current_sphere") != null) {
				sphereDef.element("current_sphere").detach();
			}
			if (sphereDef.element("original_id") != null) {
				sphereDef.element("original_id").detach();
			}

			statsDoc.getRootElement().element("since_local_mark").addAttribute(
					"since_last_launched", "0");
			statsDoc.getRootElement().addElement("last_query").add(sphereDef);
			statsDoc.getRootElement().addElement("last_launched").addAttribute(
					"sphere_id", (String) session.get("sphere_id"))
					.addAttribute("username", (String) session.get("username"))
					.addAttribute("contact_name",
							(String) session.get("real_name")).addAttribute(
							"moment", currentMoment);
			getXmldb().replaceDoc(statsDoc, personalSphere);

		}
	}

	public void deliverMail(final Hashtable session) {
		try {
			new EmailDeliverer( this ).deliverMail(session);
		} catch (Exception ex) {
			logger.error( "Error in email delivering", ex );
		}
	}

	/**
	 * 
	 * Opens the default spheres based on what is stored in their profile. This
	 * gets created when the "Save Tab Order" gets saved.
	 * 
	 * 
	 * @param session
	 */

	@SuppressWarnings("unchecked")
	public void sendDefaultSpheres(Hashtable session) {

		Element buildOrder = getVerifyAuth().getBuildOrder();

		if (buildOrder != null) {

			Vector elements = new Vector(buildOrder.elements());

			for (int i = 0; i < elements.size(); i++) {

				Element order = (Element) elements.get(i);

				String systemName = order.attributeValue("system_name");
				String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
				String username = (String) session.get(SC.USERNAME);
				String loginSphere = getVerifyAuth().getLoginSphere(username);
				logger.info("Sending next sphere: " + systemName);
				if (!systemName.equals(loginSphere)) {
					Hashtable<String, String> testSession = (Hashtable) session
							.clone();
					testSession.put(SC.SPHERE_ID, systemName);
					Document sphereDefinition = getXmldb().getSphereDefinition(
							supraSphere, systemName);

					if (sphereDefinition == null) {
						SphereDefinitionCreator sdc = new SphereDefinitionCreator();

						sphereDefinition = sdc.createDefinition(getVerifyAuth()
								.getDisplayName(systemName), systemName);

					}

					this.sendDefinitionMessages(testSession, sphereDefinition,
							getVerifyAuth(), "false");
					logger.info("Sent sphere: " + systemName);
				}
			}
		}
	}

	public void sendExistingQuery(Document sphereDefinition, String queryId,
			String relativeBeginMoment, String finalEndMoment, int currentPage)
			throws NumberFormatException, DocumentException {
		String sphere_id = (String) getSession().get(SC.SPHERE_ID);
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(this.startUpSession);
		for (DialogsMainPeer handler : filteredHandlers) {
			logger.info("sphereid in senddef: " + sphere_id);
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, this.startUpSession);

			String data_id = handler.getXmldb().getUtils().getInheritedName(
					sphere_id);

			String sphereCore = handler.getXmldb().getUtils().getSphereCore(
					this.startUpSession);

			logger.info("sphere core" + sphereCore);

			logger.info("Data id: " + data_id);

			logger.info("BEFORE REALLY: " + relativeBeginMoment + " : "
					+ finalEndMoment);
			Hashtable reallyAll = handler.getXmldb().getForSphereLight(
					this.startUpSession, handler, data_id, sphereDefinition,
					new Long(relativeBeginMoment), new Long(finalEndMoment));

			Vector order = (Vector) reallyAll.get(SC.ORDER);
			Vector contactsOnly = (Vector) reallyAll.get(SC.CONTACTS_ONLY);

			String pages = null;
			String pagesType = null;

			try {
				pagesType = (String) reallyAll.get(SC.TOTAL_PAGES_TYPE);
				logger.info("putting totalpages: " + pagesType);
				dmpResponse.setStringValue(SC.TOTAL_PAGES_TYPE, pagesType);
				if (pagesType != null) {
					reallyAll.remove(SC.TOTAL_PAGES_TYPE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				finalEndMoment = ((Long) reallyAll.get(SC.FINAL_END_MOMENT))
						.toString();
				if (finalEndMoment != null) {
					reallyAll.remove(finalEndMoment);
				}
				relativeBeginMoment = ((Long) reallyAll
						.get(SC.RELATIVE_BEGIN_MOMENT)).toString();
				if (relativeBeginMoment != null) {
					reallyAll.remove(relativeBeginMoment);
				}
				pages = (String) reallyAll.get(SC.TOTAL_PAGES);

				dmpResponse.setStringValue(SC.TOTAL_PAGES, pages);

				if (pages != null) {
					reallyAll.remove(SC.TOTAL_PAGES);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}

			reallyAll.remove(SC.ORDER);

			/*
			 * #NOT_USED boolean isTopDown =
			 * handler.getVerifyAuth().getTreeOrder();
			 */

			// Vector reallyAll =
			// bNode.getEntireThreadFromResponses((String)session.get("sphere_id"),responses);
			if (finalEndMoment != null) {

				this
						.setQueryMoments(queryId, relativeBeginMoment,
								finalEndMoment, new Integer(currentPage + 1)
										.toString());

				logger
						.info("set query moments: "
								+ this.currentQueries.asXML());

			}

			dmpResponse.setVectorValue(SC.CONTACTS_ONLY, contactsOnly);
			reallyAll.remove(SC.CONTACTS_ONLY);
			dmpResponse.setMapValue(SC.ALL, reallyAll);
			dmpResponse.setVectorValue(SC.ORDER, order);

			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.RECEIVE_RESULTS_FROM_XMLSEARCH);
			dmpResponse.setStringValue(SC.CURRENT_PAGE, new Integer(
					currentPage + 1).toString());
			dmpResponse
					.setDocumentValue(SC.SPHERE_DEFINITION, sphereDefinition);
			dmpResponse.setStringValue(SC.SPHERE, sphere_id);
			// dmpResponse.puta("allMessages",all);
			logger.info("About to send: ");
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
			sendFromQueue(dmpResponse, handler.getName());

		}

	}

	public void updateAllLocations(final Document document,
			final Hashtable session) {
		Thread t = new Thread() {

			@SuppressWarnings( { "unchecked", "static-access" })
			public void run() {
				String sphereIdd = (String) session.get(SC.SPHERE_ID);
				String real_name = (String) session.get(SC.REAL_NAME);
				Document doc = null;

				try {
					doc = document;

					Vector locations = null;
					if (doc.getRootElement().element("locations") != null) {
						locations = new Vector(doc.getRootElement().element(
								"locations").elements());
					}

					if (locations == null) {
						final DmpResponse dmpResponse = new DmpResponse();
						dmpResponse.setStringValue(SC.PROTOCOL,
								SSProtocolConstants.UPDATE_DOCUMENT);
						// dmpResponse.puta("isUpdate","onlyIfExists");

						dmpResponse.setStringValue(SC.SPHERE, sphereIdd);

						String data_sphere = getXmldb().getUtils()
								.getInheritedName(sphereIdd);
						Document res = getXmldb().voteDoc(doc, data_sphere,
								real_name);

						res.getRootElement().addElement("current_sphere")
								.addAttribute("value", sphereIdd);

						dmpResponse.setDocumentValue(SC.DOCUMENT, res);

						for (DialogsMainPeer handler : DmpFilter
								.filter(sphereIdd)) {
							handler.sendFromQueue(dmpResponse);
						}
					} else {
						logger.info("sending to locaitons...");

						for (int i = 0; i < locations.size(); i++) {

							Element loc = (Element) locations.get(i);
							String locSphereId = loc
									.attributeValue("ex_system");

							logger.info("Sending to this loc sphere id: "
									+ locSphereId);

							final DmpResponse dmpResponse = new DmpResponse();
							dmpResponse.setStringValue(SC.PROTOCOL,
									SSProtocolConstants.UPDATE_DOCUMENT);

							dmpResponse.setStringValue(SC.SPHERE, locSphereId);
							dmpResponse.setStringValue(SC.IS_UPDATE,
									"onlyIfExists");
							String data_sphere = getXmldb().getUtils()
									.getInheritedName(locSphereId);

							Document res = getXmldb().voteDoc(doc, data_sphere,
									real_name);

							if (res != null) {
								res.getRootElement().addElement(
										"current_sphere").addAttribute("value",
										locSphereId);

								dmpResponse.setDocumentValue(SC.DOCUMENT, res);
							} else {
								dmpResponse.setDocumentValue(SC.DOCUMENT, doc);

							}
							for (DialogsMainPeer handler : DmpFilter
									.filter(locSphereId)) {
								handler.sendFromQueue(dmpResponse);
							}
						}

					}
				} catch (NullPointerException exc) {
					logger.error("NPE in updateAllLocations", exc);
				}
			}
		};
		t.start();

	}

	@SuppressWarnings("unchecked")
	public void replaceAndUpdateAllLocations(Hashtable session,
			Document document) {
		Vector<Element> locations;
		if (document.getRootElement().element("locations") != null) {
			locations = new Vector<Element>(document.getRootElement().element(
					"locations").elements());
		}
		else {
			logger.warn( "locations is null in replaceAndUpdateAllLocations" );
			return;
		}
		logger.info( "replaceAndUpdateAllLocations, locations count " + locations.size() );
		
		for (Element location : locations) {
			final String locSphereId = location.attributeValue("ex_system");
			final String locMessageId = location.attributeValue("ex_message");
			logger.info("Sending to this loc sphere id: " + locSphereId);
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
			String data_sphere = getXmldb().getUtils().getInheritedName(
					locSphereId);
			logger.info("REPLACING DOC: " + data_sphere);
			if (data_sphere == null) {
				data_sphere = locSphereId;
			}
			Document otherLocDoc = getXmldb().getSpecificID(locSphereId,
					locMessageId);
			Document toReplaceDoc = (Document) document.clone();
			if (otherLocDoc != null) {
				try {
					logger.warn("it was null: " + locSphereId
							+ " locMessage: " + locMessageId);

					toReplaceDoc.getRootElement().element("thread_id")
							.addAttribute(
									"value",
									otherLocDoc.getRootElement().element(
											"thread_id")
											.attributeValue("value"));
				} 
				catch (Exception ex) {
					logger.error( "Can't configure doc to replace", ex);
				}
				toReplaceDoc.getRootElement().element("message_id")
						.addAttribute(
								"value",
								otherLocDoc.getRootElement().element(
										"message_id").attributeValue("value"));
				toReplaceDoc.getRootElement().element("original_id")
						.addAttribute(
								"value",
								otherLocDoc.getRootElement().element(
										"original_id").attributeValue("value"));

				Document res = getXmldb().replaceDoc(toReplaceDoc, locSphereId);

				dmpResponse.setStringValue(SC.SPHERE, locSphereId);
				if (res.getRootElement().element("current_sphere") != null) {
					res.getRootElement().element("current_sphere").detach();
				}

				res.getRootElement().addElement("current_sphere").addAttribute(
						"value", locSphereId);
				dmpResponse.setDocumentValue(SC.DOCUMENT, res);

				for (DialogsMainPeer handler : DmpFilter.filter(locSphereId) ) {
					@Refactoring(classify=SupraSphereRefactor.class, message="Keywords will not update", level = Refactoring.Level.POTENTIAL_BUG)
					String type = res.getRootElement().element("type")
							.attributeValue("value");
					if (!type.equals("keywords")) {
							// Only send the acknowledgement to the
							// one that sent the original message
							// Check to see if the person
							// acknowledging has sufficient weight
							// to represent the acnowledgement
							handler.sendFromQueue(dmpResponse);
					}					
				}
			}
		}
	}

	public Vector<String> createMemberPresence(Hashtable finalsession,
			Vector contactsOnly) {

		Vector<String> available = new Vector<String>();

		for (int i = 0; i < contactsOnly.size(); i++) {
			Document doc = (Document) contactsOnly.get(i);
			String type = doc.getRootElement().element("type").attributeValue(
					"value");
			if (type.equals("contact")) {
				String first_name = (doc.getRootElement().element("first_name")
						.attributeValue("value"));
				String last_name = (doc.getRootElement().element("last_name")
						.attributeValue("value"));
				/* #NOT_USED String login = */doc.getRootElement().element(
						"login").attributeValue("value");
				if ((first_name.length() > 0) && (last_name.length() > 0)) {
					String full_name = (doc.getRootElement().element(
							"first_name").attributeValue("value")
							+ " " + doc.getRootElement().element("last_name")
							.attributeValue("value"));
					available.add(full_name);
				} else if (first_name.length() > 0) {
					available.add(first_name);
				} else if (last_name.length() > 0) {
					available.add(last_name);
				}
			}

		}

		// return getOnlineForVectorOfStrings(available, finalsession);
		// NB : removed getOnlineForVectorOfStrings for
		// setUpPresenceInfoForNonGroups
		return available;
	}

	public void updateAuthOfOtherPeers() {

		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
				.getHandlers()) {
			SupraSphereProvider.INSTANCE.configureVerifyAuth(handler.getVerifyAuth());
			// TODO: need to send update verify? 
		}
	}

	public static void sendUpdateToAllMembersOfSphere(Document document,
			String sphereId) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setDocumentValue(SC.DOCUMENT, document);
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.UPDATE_DOCUMENT);
		dmpResponse.setStringValue(SC.SPHERE, sphereId);
		for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
			handler.sendFromQueue(dmpResponse);
		}
	}

	/**
	 * @deprecated
	 * @param session
	 * @param sendSession
	 */
	public void sendResultToAllSessions(Hashtable session,
			final DmpResponse dmpResponse) {
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getSphereUserHandlersFromSession(session);
		for (DialogsMainPeer handler : filteredHandlers) {
			handler.sendFromQueue(dmpResponse);
		}
	}

	public Vector getOnlineForVectorOfStrings(Vector available,
			Hashtable finalsession) {

		Vector<String> newavailable = new Vector<String>();

		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(finalsession);
		for (DialogsMainPeer handler : filteredHandlers) {

			if (logger.isDebugEnabled()) {
				logger.debug("Got available : "
						+ ListUtils.valuesToString(available));
			}

			for (int i = 0; i < available.size(); i++) {
				String check = (String) available.get(i);
				logger.info("checking now in getmember: " + check);
				boolean found = false;

				for (DialogsMainPeer handler2 : DialogsMainPeerManager.INSTANCE
						.getHandlers()) {

					String there = handler2.get(HandlerKey.USERNAME);
					// TODO bug? may be handler2 instead of handler?
					String checkLogin = handler.getVerifyAuth().getRealName(
							there);

					if (checkLogin != null && check.equals(checkLogin)) {
						found = true;
						break;
					} else {
						found = false;
					}
				}

				if (found == true) {
					String newstring = "*" + check + "*";
					newavailable.add(newstring);
				} else {
					newavailable.add(check);
				}

			}
		}
		return newavailable;
	}

	public boolean isContactOnline(String contactName, Hashtable finalsession) {
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(finalsession);
		for (DialogsMainPeer handler : filteredHandlers) {
			logger.info("checking now in getmember: " + contactName);
			for (DialogsMainPeer handler2 : DialogsMainPeerManager.INSTANCE
					.getHandlers()) {
				String handName = handler2.getName();
				/* #NOT_USED String checkLogin = */handler.getVerifyAuth()
						.getRealName(contactName);
				if (handName.lastIndexOf(contactName) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public static boolean isContactOnline(String contactName) {
		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
				.getHandlers()) {
			if (CompareUtils.equals(handler.getVerifyAuth().getUserSession()
					.getRealName(), contactName)) {
				return true;
			}
		}
		return false;
	}

	public Vector<String> getOtherSphereIds(Hashtable session,
			Document sphereDefinition) {

		Vector<String> surrounding = new Vector<String>();
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String realName = (String) session.get(SC.REAL_NAME);
		String username = (String) session.get(SC.USERNAME);
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : filteredHandlers) {

			String scope = sphereDefinition.getRootElement().element("scope")
					.attributeValue("value");

			if (scope.equals("Everyone else's bookmarks and feeds")) {

				String displayName = getVerifyAuth().getDisplayName(sphereId);
				String sphereType = getVerifyAuth().getSphereType(sphereId);

				if (sphereType.equals("member")) {
					if (displayName.equals(realName)) {
						sphereType = "personal";
					}

					if (!sphereType.equals("personal")) {
						surrounding.add(handler.getVerifyAuth()
								.getPrivateForSomeoneElse(displayName));
					}
				}
				if (sphereType.equals("group")) {

					Vector members = handler.getXmldb().getSubPresence(
							sphereId, sphereId);
					for (int j = 0; j < members.size(); j++) {

						String one = (String) members.get(j);

						if (!one.equals(realName)) {
							surrounding.add(handler.getVerifyAuth()
									.getPrivateForSomeoneElse(one));
						}
					}
				}
			} else if (scope.equals("Mine and everyone's bookmarks and feeds")) {

				String displayName = getVerifyAuth().getDisplayName(sphereId);
				String sphereType = getVerifyAuth().getSphereType(sphereId);

				String mySphereId = getXmldb().getUtils()
						.getHomeSphereFromLogin(username);
				surrounding.add(mySphereId);

				if (sphereType.equals("member")) {
					if (displayName.equals(realName)) {
						sphereType = "personal";
					}

					if (!sphereType.equals("personal")) {
						surrounding.add(handler.getVerifyAuth()
								.getPrivateForSomeoneElse(displayName));
					}
				}
				if (sphereType.equals("group")) {

					Vector members = handler.getXmldb().getSubPresence(
							sphereId, sphereId);

					for (int j = 0; j < members.size(); j++) {
						String one = (String) members.get(j);

						if (!one.equals(realName)) {
							surrounding.add(handler.getVerifyAuth()
									.getPrivateForSomeoneElse(one));
						}
					}
				}
			}
		}
		return surrounding;

	}

	/**
	 * 
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	// TODO convert openBackGround to boolean
	public void sendDefinitionMessages(Hashtable session,
			Document sphereDefinition, VerifyAuth verifyAuth,
			String openBackground) {
		if (isSphereForMemberEnabled(sphereDefinition, session)
				|| isAdmin(session) || isClubdealEnabled(sphereDefinition, session)) {
			this.sphereDefinitionProcessor.sendGroupSphere(this, session,
					sphereDefinition, verifyAuth, openBackground);
		} else {
			sendAccessDeniedMessage(session, sphereDefinition);
		}
	}

	/**
	 * @param sphereDefinition
	 * @param session
	 * @return
	 */
	private boolean isClubdealEnabled(Document sphereDefinition,
			Hashtable session) {
		SphereStatement sphere = SphereStatement.wrap(sphereDefinition);
		if(!sphere.isClubDeal()) {
			return false;
		}
		Vector<Document> contactDocs = this.getXmldb().getAllContactsForMembers(sphere.getSystemName());
		for(Document doc : contactDocs) {
			if(ContactStatement.wrap(doc).getLogin().equals(session.get(SessionConstants.USERNAME))) {
				return true;
			}
		}
		return false;
	}

	public void sendPrivateSphereDefinition(Hashtable session,
			Document sphereDefinition, VerifyAuth verifyAuth,
			String openBackground) {
		this.sphereDefinitionProcessor.sendPrivateSphere(this, session,
				sphereDefinition, verifyAuth, openBackground);
	}

	/**
	 * @param session
	 * @return
	 */
	private boolean isAdmin(Hashtable session) {
		String contactName = (String) session.get(SessionConstants.REAL_NAME);
		String loginName = (String) session.get(SessionConstants.USERNAME);
		return this.xmldb.getVerifyAuth().isAdmin(contactName, loginName);
	}

	public void sendAccessDeniedMessage(Hashtable session, Document sphereDoc) {
		FilteredHandlers handlers = FilteredHandlers
				.getSphereUserHandlersFromSession(session);

		DmpResponse response = new DmpResponse();
		response.setStringValue(SessionConstants.DISPLAY_NAME, SphereStatement
				.wrap(sphereDoc).getDisplayName());
		response.setStringValue(SessionConstants.SPHERE_ID, SphereStatement
				.wrap(sphereDoc).getSystemName());
		response.setStringValue(SessionConstants.PROTOCOL,
				SSProtocolConstants.ACCESS_DENIED);

		for (DialogsMainPeer handler : handlers) {
			handler.sendFromQueue(response);
		}
	}

	/**
	 * @param sphereDefinition
	 * @param session
	 * @return
	 */
	@Refactoring(classify=SupraSphereRefactor.class, message="Very strange way to detect isSphereForMemberEnabled")
	private boolean isSphereForMemberEnabled(Document sphereDefinition,
			Hashtable session) {
		try {
			ISupraSphereFacade supraSphere = getXmldb().getSupraSphere();
			String sphereId = SphereStatement.wrap(sphereDefinition)
					.getSystemName();
			String memberLogin = (String) session
					.get(SessionConstants.USERNAME);
			SupraSphereMember member = supraSphere.findMemberByLogin(memberLogin);
			SphereItem item = member.getSphereBySystemName(sphereId);
			if (item == null) {
				return false;
			}
			return item.isEnabled()
					|| item.getSphereType() == SphereItem.SphereType.MEMBER;
		} catch (NullPointerException ex) {
			logger.error("doc exception", ex);
		}
		return false;
	}

	/**
	 * Trace for sendFromQueue function
	 * 
	 * @param update
	 */
	@SuppressWarnings("unused")
	private void traceSendFromQueue(final DmpResponse dmpResponse) {
		if (!logger.isDebugEnabled()) {
			return;
		}
		logger.debug("Send to clients DmCommand: "
				+ dmpResponse.getStringValue(SC.PROTOCOL));
		// if ( dmpResponse.getProtocolName().equals("update") ) {
		// logger.debug( "Update by " + DebugUtils.getCurrentStackTrace() );
		// }
		// if ( dmpResponse.hasKey( SessionConstants.SUPRA_SPHERE_DOCUMENT ) ) {
		// final Document supraSphereDocument = (Document)
		// dmpResponse.getObject( SessionConstants.SUPRA_SPHERE_DOCUMENT );
		// logger.debug( "Update supra sphere document by " +
		// DebugUtils.getCurrentStackTrace() + " suprasphere " +
		// supraSphereDocument );
		// }
	}

	public void sendFromQueue(final DmpResponse dmpResponse) {
		traceSendFromQueue(dmpResponse);
		DmCommand dmCommand = new DmCommand(dmpResponse);
		dmCommand.beginExecute(this.protocol);
	}

	public static void sendFromQueue(final DmpResponse dmpResponse,
			final String handlerName) {
		DialogsMainPeer targetPeer = DialogsMainPeerManager.INSTANCE
				.findHandler(handlerName);
		if (targetPeer != null) {
			targetPeer.sendFromQueue(dmpResponse);
		} else {
			logger.error("Can't find handler by " + handlerName);
		}
	}

	public boolean isConnectedAnotherLocation(String real_name) {
		boolean isConnected = false;

		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
				.getHandlers()) {
			String loginName = handler.getVerifyAuth().getLoginForContact(
					real_name);
			Filter filter = createUsernameFilter(loginName);

			if (filter.filter(handler)) {
				isConnected = true;
			}
		}
		return isConnected;

	}

	private static Filter createUsernameFilter(String username) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.USERNAME, username));
		return filter;
	}

	public void sendUpdateVerify(Document supraSphereDocument, String sessionId) {

		logger.info("Trying..." + sessionId);

		this.getVerifyAuth().setSphereDocument(supraSphereDocument);

		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT);
		dmpResponse.setDocumentValue(SC.SUPRA_SPHERE_DOCUMENT,
				supraSphereDocument);
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SESSION, sessionId,
				UnaryOperation.NOT));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		for (DialogsMainPeer handler : filteredHandlers) {
			logger.info("Sending to this handler..." + handler.getName());
			handler.getVerifyAuth().setSphereDocument(supraSphereDocument);
			handler.sendFromQueue(dmpResponse);

		}
	}

	public void sendUpdateVerifyToAll(Document supraSphereDocument) {

		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
				.getHandlers()) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT);
			dmpResponse.setDocumentValue(SC.SUPRA_SPHERE_DOCUMENT,
					supraSphereDocument);
			logger.info("Sending to this handler..." + handler.getName());
			handler.sendFromQueue(dmpResponse);

		}

	}

	public Hashtable<String, String> getEmailInfo(Hashtable session,
			String contactName) {
		//
		String realName = (String) session.get(SC.REAL_NAME);
		String sAddress = (String) session.get(SC.ADDRESS);
		String username = (String) session.get(SC.USERNAME);
		String sphereId = (String) session.get(SC.SPHERE_ID);

		String systemName = getVerifyAuth().getSystemName(realName);

		String address = getXmldb().getEmailAddress(systemName, contactName);

		String sphereDomain = getXmldb().getUtils().getSphereDomain();

		if (sphereDomain.equals("$loginAddress")) {
			logger.warn("That was true");
			sphereDomain = sAddress;
		}

		if (address == null) {

			// address = "__NOBODY__";
			LoginSphere loginSphere = getXmldb().getUtils().findLoginSphereElement(
					username);
			String loginSphereId = null;

			if (loginSphere != null) {
				logger.warn("login sphere was not null");

				loginSphereId = loginSphere.getSystemName();
				String contactAddress = getXmldb().getEmailAddress(
						loginSphereId, contactName);
				if (contactAddress != null) {
					address = contactAddress;
				}
				logger.warn("Address here: " + address);

				if (address == null) {

					contactAddress = getXmldb().getEmailAddress(sphereId,
							contactName);
					if (contactAddress != null) {
						address = contactAddress;
					} else {
						address = "__NOBODY__";
					}

				}

			} else {
				logger.warn("Login sphere was null!!! ");
			}
		}
		logger.warn("putting email address: " + address);
		Hashtable<String, String> emailInfo = new Hashtable<String, String>();
		emailInfo.put("email_address", address);
		emailInfo.put("sphere_domain", sphereDomain);

		return emailInfo;

		//

	}

	/**
	 * @deprecated
	 * @param contactDoc
	 * @param sessionId
	 */
	public void sendUpdateContactDoc(Document contactDoc, String sessionId) {
		logger.info("Trying..." + sessionId);
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.UPDATE_CONTACT_DOC);
		dmpResponse.setDocumentValue(SC.CONTACT_DOC, contactDoc);
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SESSION, sessionId,
				UnaryOperation.NOT));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		for (DialogsMainPeer handler : filteredHandlers) {
			logger.info("Sending to this handler..." + handler.getName());
			handler.sendFromQueue(dmpResponse);
		}
	}

	public static void sendForAllRefreshPresence(String contactName,
			String sphereId) {
		boolean isOnline = isContactOnline(contactName);
		for (DialogsMainPeer handler : DmpFilter.filter(sphereId)) {
			try {
				final String handlerLogin = handler.getVerifyAuth()
						.getUserSession().getUserLogin();
				if (logger.isDebugEnabled()) {
					logger.debug("Checking send add presence for: "
							+ handlerLogin);
				}
				handler.sendRefreshPresence(contactName, isOnline, sphereId);
			} catch (Exception ex) {
				ExceptionHandler.handleException(ex);
			}
		}
	}

	/**
	 * @param sphereId
	 * @param contactName
	 * @param isOnline
	 * @param handler
	 */
	private void sendRefreshPresence(String contactName, boolean isOnline,
			String sphereId) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.REFRESH_PRESENCE);
		dmpResponse.setStringValue(SC.CONTACT_NAME, contactName);
		dmpResponse.setBooleanValue(SC.IS_ONLINE, isOnline);
		dmpResponse.setSphereId(sphereId);
		if (logger.isDebugEnabled()) {
			String handlerLogin = getVerifyAuth().getUserSession()
					.getUserLogin();
			logger.debug("Sending Refresh presence for: " + handlerLogin
					+ " in " + sphereId + " about " + contactName);
		}
		sendFromQueue(dmpResponse);
	}

	/**
	 * @param sphereId
	 * @param contactName
	 * @param isOnline
	 * @param handler
	 */
	public void sendRefreshPresence(String contactName, boolean isOnline) {
		sendRefreshPresence(contactName, isOnline, null);
	}

	public static String getCurrentMoment() {
		// Locale.setDefault(Locale.ENGLISH);
		Date current = new Date();
		return DateFormat.getTimeInstance(DateFormat.LONG).format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

	}

	public void registerSessionForPassphraseChange(String string) {
		passphraseChangeSessions.put(string, string);
		logger.warn("ADDED : " + string);
	}

	public boolean checkAndRemovePassphraseChangeSession(String string) {
		if (passphraseChangeSessions.containsKey(string)) {
			passphraseChangeSessions.remove(string);
			return true;
		} else {
			logger.info(" passphraseChangeSessions is : "
					+ MapUtils.allValuesToString(passphraseChangeSessions));
			return false;
		}
	}

	/**
	 * @param verifyAuth
	 *            The verifyAuth to set.
	 */
	public void setVerifyAuth(VerifyAuth verifyAuth) {
		this.verifyAuth = verifyAuth;
	}

	/**
	 * @return Returns the verifyAuth.
	 */
	public VerifyAuth getVerifyAuth() {
		return this.verifyAuth;
	}

	/**
	 * @return Returns the xmldb.
	 */
	public XMLDB getXmldb() {
		return this.xmldb;
	}

	public XMLDBOld getXmlDbOld() {
		return XMLDBOld.get(getXmldb());
	}

	public static String createHandlerNameFromSession(Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String username = (String) session.get(SC.USERNAME);
		String sessionName = (String) session.get(SC.SESSION);
		return createHandlerName(supraSphere, username, sessionName);
	}

	public static String createHandlerName(String supraSphere, String username,
			String sessionName) {
		return supraSphere + "," + username + "," + sessionName;
	}

	public boolean equalsWithNameFromSession(Hashtable session) {
		return this.getName().equals(createHandlerNameFromSession(session));
	}

	public boolean equalsName(String supraSphere, String username,
			String sessionName) {
		return this.getName().equals(
				createHandlerName(supraSphere, username, sessionName));
	}

	public static String createHandlerNamePrefixFromSession(Hashtable session) {
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String username = (String) session.get(SC.USERNAME);
		return createHandlerNamePrefix(supraSphere, username);
	}

	public static String createHandlerNamePrefix(String supraSphere,
			String username) {
		return supraSphere + "," + username;
	}

	public boolean startWithName(String supraSphere, String username) {
		return this.getName().startsWith(
				createHandlerNamePrefix(supraSphere, username));
	}

	public boolean startWithNameFromSession(Hashtable session) {
		return this.getName().startsWith(
				createHandlerNamePrefixFromSession(session));
	}

	public static String createSphereIdApath(String sphereSystemName) {
		return "//suprasphere/member/sphere[@system_name=\"" + sphereSystemName
				+ "\"]";
	}

	public static String createEnabledSphereIdApath(String sphereId) {
		return "//suprasphere/member/sphere[@system_name='" + sphereId
				+ "' and @enabled='true']";
	}

	public static String createUsernameApath(String username) {
		return "//suprasphere/member[@login_name=\"" + username + "\"]";
	}

	public void registryHandlerAndStart(String sphere, String username,
			String session) {
		this.nameTable.put(HandlerKey.SUPRA_SPHERE, sphere);
		this.nameTable.put(HandlerKey.USERNAME, username);
		this.nameTable.put(HandlerKey.SESSION, session);
		this.setName(sphere + "," + username + "," + session);
		DialogsMainPeerManager.INSTANCE.register(this);
		this.protocol.start(ServerProtocolManager.INSTANCE);
		this.sphereDefinitionProcessor.start(this.protocol.getDisplayName());
	}

	public String get(HandlerKey key) {
		return this.nameTable.get(key);
	}

	public void sendBootstrapComplete() {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.BOOTSTRAP_COMPLETE);
		logger.info("Sending bootstrap complete");
		sendFromQueue(dmpResponse, getName());
	}

	void sendInviteComplete(Document invitingContactDoc, VerifyAuth verifyAuth,
			String s, String v, String messageId) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.INVITE_COMPLETE);

		dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, verifyAuth);
		dmpResponse.setStringValue(SC.VERIFIER, v);

		dmpResponse.setStringValue(SC.SALT, s);
		dmpResponse.setStringValue(SC.USERNAME, messageId);

		dmpResponse.setDocumentValue(SC.INVITING_CONTACT_DOC,
				invitingContactDoc);

		// logger.warn("Sending invite complete:
		// "+invitingContactDoc.asXML());

		sendFromQueue(dmpResponse, getName());
	}

	public void sendPromptPassphraseChange(String loginSphereId, String URL,
			String tempUsername) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.PROMPT_PASSPHRASE_CHANGE);
		dmpResponse.setStringValue(SC.TEMP_USERNAME, tempUsername);
		dmpResponse.setStringValue(SC.INVITE_URL, URL);
		dmpResponse.setStringValue(SC.LOGIN_SPHERE, loginSphereId);
		sendFromQueue(dmpResponse, getName());
	}

	/**
	 * Retruns session hastable
	 */
	public Hashtable getSession() {
		return this.currentCommandSession != null ? this.currentCommandSession
				: getStartUpSession();
	}

	/**
	 * Retruns session hastable
	 */
	public Hashtable getStartUpSession() {
		return this.startUpSession;
	}

	/**
	 * @return the protocol
	 */
	public HandlerCollection getHandlers() {
		return this.handlers;
	}

	/**
	 * @return
	 */
	public String getUserLogin() {
		if (this.verifyAuth != null) {
			return this.verifyAuth.getUserSession().getUserLogin();
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getUserContactName() {
		if (this.verifyAuth != null) {
			return this.verifyAuth.getUserSession().getRealName();
		}
		return null;
	}

	/**
	 * 
	 */
	public void dispose() {
		unregister();
		updatePresenceAboutLogoff();
		if (this.sphereDefinitionProcessor != null) {
			this.sphereDefinitionProcessor.shootdown();
		}
		if (this.protocol != null) {
			this.protocol.beginClose();
		}
	}

	/**
	 * 
	 */
	private void unregister() {
		DialogsMainPeerManager.INSTANCE.unregister(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.protocol + " [" + getName() + "]" + this.isAlive();
	}

	/**
	 * @return
	 */
	private boolean isAlive() {
		return this.protocol != null && this.protocol.isValid();
	}

	public Protocol getProtocol() {
		return this.protocol;
	}

	class DmCommandHandler extends VoidCommandHandler<DmCommand> {
		/**
		 * @param messageClass
		 */
		public DmCommandHandler() {
			super(DmCommand.class);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.common.networking2.VoidCommandHandler#execute(ss.common.networking2.Command)
		 */
		@Override
		protected void execute(DmCommand command) throws CommandHandleException {
			handleCommand(command);
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public ArrayList<String> getEmailsOfPossibleRecipients(String sphereId) {
		return this.xmldb.getEmailsOfPossibleRecipients(sphereId);
	}

	/**
	 * @param supraDoc
	 */
	public static void updateVerifyAuthForAll(Document supraDoc) {
		if ( supraDoc == null ) {
			return;
		}
		for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
				.getHandlers()) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.UPDATE_VERIFY);
			final VerifyAuth handlerVerifyAuth = handler.getVerifyAuth();
			if ( handlerVerifyAuth != null ) {
				handlerVerifyAuth.setSphereDocument(supraDoc);
				dmpResponse.setMapValue(SC.SESSION, handler.getSession());
				dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, handlerVerifyAuth);				
			}
			handler.sendFromQueue(dmpResponse);
		}
	}

}
