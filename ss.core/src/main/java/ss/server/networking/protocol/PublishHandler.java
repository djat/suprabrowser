package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.ui.email.EmailAddressesContainer;
import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.rss.RSSParser;
import ss.search.URLParser;
import ss.server.MethodProcessing;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.smtp.eMailer;
import ss.smtp.defaultforwarding.EmailForwarder;
import ss.smtp.defaultforwarding.ForwardingElement;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

public class PublishHandler implements ProtocolHandler {

	private static final String ADDRESS = "address";

	private static final String BOOKMARK = "bookmark";

	private static final String COMMENT = "comment";

	private static final String REPLY = "reply";

	private static final String BODY = "body";
	
	private static final String RSS = "rss";

	private static final String MESSAGE = "message";

	private static final String SUBJECT = "subject";

	private static final String TERSE = "terse";

	private static final String TYPE = "type";

	private static final String MESSAGE_ID = "message_id";

	private static final String SPHERE_DOMAIN = "sphere_domain";

	private static final String EMAIL_ADDRESS = "email_address";

	private static final String VALUE = "value";

	private static final String GIVER = "giver";

	private static final String ORIG_BODY = "orig_body";

	private static final String _3000 = "3000";

	private static final String VERSION = "version";

	private static final String CONFIRMED = "confirmed";

	private static final String STATUS = "status";

	private static final String THREAD_TYPE = "thread_type";

	private static final String RESPONSE_ID = "response_id";

	private static final String DAVID_THOMSON = "David Thomson";

	private static final String EMAIL = "email";
	
	private static final String EXTERNALEMAIL = "externalemail";

	private static final String URL = "URL";

	public static final String CONTACT = "contact";

	public static final String KEYWORDS = "keywords";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String VOTE_MOMENT = "vote_moment";

	private static final String MEMBER = "member";

	private static final String TALLY = "tally";

	private static final String VOTING_MODEL = "voting_model";

	private static final String LAST_UPDATED = "last_updated";

	private static final String MOMENT = "moment";

	private static final String THREAD_ID = "thread_id";

	private static final String ORIGINAL_ID = "original_id";

	private DialogsMainPeer peer;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PublishHandler.class);

	public PublishHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.PUBLISH;
	}

	public void handle(Hashtable update) {
		handlePublish(update);
	}
	
	public static final void fillRequeredFileds( final Document doc, 
			final String real_name, final String moment, final String sphereId ) {
		String message_id = VariousUtils.createMessageId();
		Element email = doc.getRootElement();
		
		if (email.element(MESSAGE_ID) == null) {
			email.addElement(ORIGINAL_ID).addAttribute(VALUE, message_id);
			email.addElement(MESSAGE_ID).addAttribute(VALUE, message_id);
		} else if (email.element(MESSAGE_ID).attributeValue("value")==null) {
			email.element(MESSAGE_ID).addAttribute(VALUE, message_id);
			email.element(ORIGINAL_ID).addAttribute(VALUE, message_id);
		}
		
		if (email.element(THREAD_ID) == null) {
			email.addElement(THREAD_ID).addAttribute(VALUE, message_id);
		} else if (email.element(THREAD_ID).attributeValue("value")==null) {
			email.element(THREAD_ID).addAttribute(VALUE, message_id);
		}
		
		if (email.element(MOMENT) == null) {
			email.addElement(MOMENT).addAttribute(VALUE, moment);
			email.addElement(LAST_UPDATED).addAttribute(VALUE, moment);
		}

		try {
			email.element(VOTING_MODEL).element(TALLY).addElement(MEMBER)
					.addAttribute(VALUE, real_name).addAttribute(
							VOTE_MOMENT, moment);
		} catch (NullPointerException npe) {
			email.addElement(VOTING_MODEL).addElement(TALLY);
			email.element(VOTING_MODEL).element(TALLY).addElement(MEMBER)
					.addAttribute(VALUE, real_name).addAttribute(
							VOTE_MOMENT, moment);
		}
		
		if(email.element(CURRENT_SPHERE) == null) {
			doc.getRootElement().addElement(CURRENT_SPHERE).addAttribute(VALUE,
					sphereId);
		}
	}

	@SuppressWarnings("unchecked")
	private void handlePublish(final Hashtable update) {
		
		final Hashtable session = (Hashtable) update.get(SC.SESSION);			
		Document doc = (Document) update.get(SC.DOCUMENT);
		String remoteUsername = (String) update.get(SC.REMOTE_USERNAME);
		String repress = (String) update.get(SC.REPRESS_NOTIFICATION);
		String externalConnection = (String) update.get(SC.EXTERNAL_CONNECTION);
		
		String real_name = (String) session.get(SC.REAL_NAME);
		String sphereId = (String) session.get(SC.SPHERE_ID);		
		String multiLoc = (String) session.get(SC.MULTI_LOC_SPHERE);
		String realName = (String) session.get(SC.REAL_NAME);
		
		Boolean bool = (Boolean) update.get(SC.IS_SKIP_INSERTDOC_IN_PUBLISHHANDLER);
		final boolean skipInsertDoc = (bool != null) ? bool.booleanValue() : false;
		
		boolean registerWorkflow = false;
	
		try {

			boolean repressNotification = false;

			try {
				if ((repress != null)&&(repress.equals("true"))) {
					repressNotification = true;
				}

			} catch (NullPointerException npe) {

			}

			boolean external = false;

			try {
				if (externalConnection != null) {
					if (externalConnection.equals("true")) {
						external = true;
					}
				}

			} catch (NullPointerException npe) {

			}
			
			Element email = doc.getRootElement();

			String moment = DialogsMainPeer.getCurrentMoment();

			
			
			if(email.element("workflowResponse")!=null) {
				registerWorkflow = true;
				email.remove(email.element("workflowResponse"));
			}
						
			//  TODO:
			//	Fixes need to check if the value is null, not just the element..should be converted
			// to use domainmodel or a utility class that would check either no element or no value,
			// and if that condition fails, create both the element and the value if it needs it or just
			// the value if it doesn't need the actual element created . Otherwise, we could have a more
			// central document, element, value creation factory that would force the element always to
			// have a value, with the exception of certain element cases such as <body> -DJ@
			
			fillRequeredFileds( doc, real_name, moment, sphereId );

			String type = doc.getRootElement().element(TYPE).attributeValue(
					VALUE);
			
			if (logger.isDebugEnabled()) {
				logger.debug("new type : "+type);
			}

			final DmpResponse dmpResponse = new DmpResponse();

			if (logger.isDebugEnabled()) {
				logger.debug("published with current sphere: " + doc.asXML());
			}

			final MethodProcessing mp = new MethodProcessing();
			
			if (!type.equals(KEYWORDS)) {
			
				if ( !skipInsertDoc ) {
					this.peer.getXmldb().insertDoc(doc, sphereId);
				} 
				
				if(registerWorkflow) {			
					String messId = Statement.wrap(doc).getMessageId();
					registerWorkflowResponse(session, messId);
				}
				
				if (type.equals(BOOKMARK)) {
					doc = mp.convertBookmarkRSS(doc);
					
					final Document finalDoc = doc;
					final String newType = doc.getRootElement().element("type").attributeValue("value");
					
					if (!newType.equals(RSS)) {
					Thread t = new Thread() {
						public void run() {
							mp.processPublishedBookmark(session, finalDoc,
									DialogsMainPeerManager.INSTANCE.getHandlers(),
									PublishHandler.this.peer.getXmldb());
						
							
						}
					};
					t.start();
					}
				}
				

				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
				dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				
				FilteredHandlers userHandlers = FilteredHandlers.getUserAllHandlersFromSession(session);
				
				if ( (userHandlers != null) && !(userHandlers.isEmpty()) ) {
					if (logger.isDebugEnabled()) {
						logger.debug("found " + userHandlers.size() + " handlers for current user: " + realName);
					}
					for (DialogsMainPeer handler : userHandlers){
						handler.sendFromQueue(dmpResponse);
					}
				} else {
					logger.warn("Not found handlers for current user: " + realName + ", using this");
					this.peer.sendFromQueue(dmpResponse);
				}

				if (external) {
					dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "true");
					dmpResponse.setStringValue(SC.REMOTE_USERNAME, remoteUsername);

				} else {
					dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "false");
				}
				FilteredHandlers filteredHandlers = FilteredHandlers
						.getAllNonUserHandlersFromSession(session);
				for (DialogsMainPeer handler : filteredHandlers) {
					final String login = handler.get(HandlerKey.USERNAME);
					boolean sphereEnabled = handler.getVerifyAuth().isSphereEnabledForMember(sphereId, login);
					if (multiLoc == null) {
						if (sphereEnabled && !repressNotification) {
							logger.info("Do this first...");
							handler.sendFromQueue(dmpResponse);
						}
					} else {

						logger.info("multi loc not null!!!");
						boolean multilocEnabled = handler.getVerifyAuth().isSphereEnabledForMember(multiLoc, login);
						if (sphereEnabled) {
							logger
									.info("SENDING TO MULTI at their personal..."
											+ login);

							dmpResponse.setStringValue(SC.SPHERE, sphereId);
							handler.sendFromQueue(dmpResponse);

						} else if (multilocEnabled) {
							// Check to see if the the person
							// sending to is that person's
							// private sphere...

							logger.warn("SENDING TO MULTI LOC..." + login);

							dmpResponse.setStringValue(SC.SPHERE, multiLoc);
							handler.sendFromQueue(dmpResponse);
						}
					}
				}

			} else {
				logger.info("Will publish keyword!");
				this.peer.getXmldb().insertDoc(doc, sphereId);
			}

			if (type.equals(TERSE) || type.equals(MESSAGE)
					|| type.equals(REPLY) || type.equals(BOOKMARK)
					|| type.equals(CONTACT)||type.equals(EXTERNALEMAIL)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Processing common");
				}
				try {
					String text = "";
					try {
						text = doc.getRootElement().element(SUBJECT)
								.attributeValue(VALUE);
						// Quick fix to multiple publishing of bookmarks to terses that are replies to message.
						// Due to body for them is an original body of message
						if (!type.equals(TERSE)){
							text = text + " "
									+ doc.getRootElement().element(BODY).getText();
						}
					} catch (Exception ex) {
						logger.error("Exception in publish handler", ex);
					}

					if (type.equals(CONTACT)) {
						try {
							text = text
									+ doc.getRootElement().element(URL)
											.attributeValue(VALUE);
						} catch (Exception e) {

						}

					}
					if (logger.isDebugEnabled()) {
						logger.debug("Processing in common for text: " + text);
					}
					Vector urls = URLParser.getURLSInsideString(text);
					
					if (logger.isDebugEnabled()) {
						logger.debug("URL SIZE: "+urls.size());
					}

					for (int i = 0; i < urls.size(); i++) {
						String URL = (String) urls.get(i);
						
						if (logger.isDebugEnabled()) {
							logger.debug("Checking this one url found: "+URL);
						}

						Document exists = this.peer.getXmldb().checkIfSeen(
								sphereId, URL);

						if (exists == null || type.equals(TERSE)) {
							String title = RSSParser.getTitleFromURL(URL);

							if (StringUtils.isNotBlank(title)) {
							
							final Document createDoc = DocumentHelper
									.createDocument();
							email = createDoc.addElement(EMAIL);

							String messageId = VariousUtils.createMessageId();

							email.addElement(ORIGINAL_ID).addAttribute(VALUE,
									messageId);
							email.addElement(MESSAGE_ID).addAttribute(VALUE,
									messageId);

							email.addElement(MOMENT)
									.addAttribute(VALUE, moment);

							email.addElement(LAST_UPDATED).addAttribute(VALUE,
									moment);

							email.addElement(GIVER).addAttribute(
									VALUE,
									doc.getRootElement().element(GIVER)
											.attributeValue(VALUE));

							email.addElement(SUBJECT)
									.addAttribute(VALUE, title);
							email.addElement(ADDRESS).addAttribute(VALUE, URL);

							email.addElement("last_updated_by").addAttribute(
									VALUE, realName);
							email.addElement(TYPE)
									.addAttribute(VALUE, BOOKMARK);

							email.addElement(VOTING_MODEL).addElement(TALLY)
									.addElement(MEMBER).addAttribute(VALUE,
											DAVID_THOMSON).addAttribute(
											VOTE_MOMENT, moment);

							email.addElement(RESPONSE_ID).addAttribute(
									VALUE,
									doc.getRootElement().element(MESSAGE_ID)
											.attributeValue(VALUE));
							email.addElement(THREAD_ID).addAttribute(
									VALUE,
									doc.getRootElement().element(MESSAGE_ID)
											.attributeValue(VALUE));

							email.addElement(THREAD_TYPE).addAttribute(
									VALUE,
									doc.getRootElement().element(THREAD_TYPE)
											.attributeValue(VALUE));
							email.addElement(CURRENT_SPHERE).addAttribute(
								VALUE,
								doc.getRootElement().element(CURRENT_SPHERE)
										.attributeValue(VALUE));
							email.addElement(STATUS).addAttribute(VALUE,
									CONFIRMED);
							email.addElement(CONFIRMED).addAttribute(VALUE,
									"true");

							Element body = new DefaultElement(BODY);

							body.addElement(VERSION).addAttribute(VALUE, _3000);

							body.addElement(ORIG_BODY);

							email.add(body);

							
							this.peer.getXmldb().insertDoc(createDoc, sphereId);
							
							final Document finalDoc = createDoc;
							final String newType = createDoc.getRootElement().element("type").attributeValue("value");
							
							if (!newType.equals(RSS)) {
							Thread t = new Thread() {
								public void run() {
									mp.processPublishedBookmark(session, finalDoc,
											DialogsMainPeerManager.INSTANCE.getHandlers(),
											PublishHandler.this.peer.getXmldb());
								
									
								}
							};
							t.start();
							}

							dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);

							dmpResponse.setDocumentValue(SC.DOCUMENT, createDoc);
							dmpResponse.setStringValue(SC.SPHERE, sphereId);

							FilteredHandlers userHandlers = FilteredHandlers.getUserAllHandlersFromSession(session);
							if ( (userHandlers != null) && !(userHandlers.isEmpty()) ) {
								if (logger.isDebugEnabled()) {
									logger.debug("found " + userHandlers.size() + " handlers for current user: " + realName);
								}
								for (DialogsMainPeer handler : userHandlers){
									handler.sendFromQueue(dmpResponse);
								}
							} else {
								logger.warn("Not found handlers for current user: " + realName + ", using this");
								this.peer.sendFromQueue(dmpResponse);
							}
							final FilteredHandlers filteredHandlers = FilteredHandlers
									.getAllNonUserHandlersFromSession(session);
							for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,sphereId)) {
								if (!repressNotification) {
									logger.error("handler name : "+handler.getName());
									handler.sendFromQueue(dmpResponse);
								}
							}
							
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception in publishHandler",e);
				}

			}
			
			final Document finalDoc = doc;
			final Hashtable finalSession = session;

			processEmailForwardingRules(finalSession, finalDoc);

		} catch (Exception ex) {
			logger.error("Problem publishing for : " + realName,ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void registerWorkflowResponse(final Hashtable session, String message_id) {
		RegisterWorkflowResponseHandler workflowhandler = (RegisterWorkflowResponseHandler)this.peer
						.getHandlers().getProtocolHandler(SSProtocolConstants.REGISTER_WORKFLOW_RESPONSE);
		Hashtable newUpdate = new Hashtable();
		newUpdate.put(SessionConstants.RESULT_ID, session.get(SessionConstants.RESULT_ID));
		newUpdate.put(SessionConstants.SPHERE_ID, session.get(SessionConstants.CURRENT_SPHERE));
		Document workflowDoc = (Document)session.get(SessionConstants.WORKFLOW_DOC);
		WorkflowResponse wr = WorkflowResponse.wrap(workflowDoc);
		wr.setId(message_id);
		workflowDoc = wr.getBindedDocument();
		newUpdate.put(SessionConstants.DOCUMENT, workflowDoc);
		newUpdate.put(SessionConstants.SESSION, session.clone());
		
		workflowhandler.handleRegisterWorkflowResponse(newUpdate);
	}
	
	/**
	 * @param finalSession
	 * @param finalDoc
	 */
	private void processEmailForwardingRules(Hashtable session, Document doc) {
		EmailForwarder.INSTANCE.send(
				new ForwardingElement(doc, (String) session.get(SC.SPHERE_ID)));
	}

	public void processEmailForwardingRules1(final Hashtable session,
			final Document doc) {
		Thread t = new Thread() {
			private DialogsMainPeer peer = PublishHandler.this.peer;

			public void run() {

				String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
				String sphereId = (String) session.get(SC.SPHERE_ID);
				Vector emailAddresses = this.peer.getXmldb().getUtils()
						.getEmailForwardingRulesForSphere(supraSphere, sphereId);

				final String fromEmail = (String) this.peer.getEmailInfo(
						session,
						doc.getRootElement().element(GIVER).attributeValue(
								VALUE)).get(EMAIL_ADDRESS);

				final String fromDomain = (String) this.peer.getEmailInfo(
						session,
						doc.getRootElement().element(GIVER).attributeValue(
								VALUE)).get(SPHERE_DOMAIN);

				String sphereName = this.peer.getVerifyAuth().getDisplayName(
						sphereId);
				final String replySphere = sphereId
						+ "."
						+ doc.getRootElement().element(MESSAGE_ID)
								.attributeValue(VALUE) + "@" + fromDomain;
				String type = doc.getRootElement().element(TYPE)
						.attributeValue(VALUE);

				String subject = "";
				StringBuffer sb = new StringBuffer();

				if (type.equals(TERSE)) {
					subject = sphereName
							+ " : "
							+ doc.getRootElement().element(SUBJECT)
									.attributeValue(VALUE);

					String newSubject = subject;
					if (subject.length() >= 40) {

						newSubject = subject.substring(0, 40) + "...";

						logger.warn("now new subject: " + newSubject);

						sb.append(doc.getRootElement().element(SUBJECT)
								.attributeValue(VALUE));

						subject = newSubject;
					}

				} else if (type.equals(MESSAGE)) {
					sb.append(doc.getRootElement().element(BODY).getText());
					subject = sphereName
							+ " : "
							+ doc.getRootElement().element(SUBJECT)
									.attributeValue(VALUE);
				} else if (type.equals(REPLY)) {

					if (doc.getRootElement().element(BODY).element(COMMENT) != null) {
						sb.append(doc.getRootElement().element(BODY).element(
								COMMENT).getText());
					} else {
						sb.append(doc.getRootElement().element(BODY).getText());
					}

					subject = doc.getRootElement().element(SUBJECT)
							.attributeValue(VALUE);
				} else if (type.equals(BOOKMARK)) {

					subject = doc.getRootElement().element(SUBJECT)
							.attributeValue(VALUE);
					sb.append("Bookmark: "
							+ doc.getRootElement().element(ADDRESS)
									.attributeValue(VALUE) + "\n\n");
					sb.append(doc.getRootElement().element(BODY).getText());

				}

				String body = sb.toString();

				for (int i = 0; i < emailAddresses.size(); i++) {
					Element emailForwarding = (Element) emailAddresses.get(i);

					final String toEmail = emailForwarding
							.attributeValue(VALUE);

					eMailer em = new eMailer();
					em.send(new EmailAddressesContainer(toEmail, fromEmail), null,
							new StringBuffer(body), subject, replySphere);
				}
			}
		};
		t.start();

	}

}
