package ss.server.networking;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.DmCommand;
import ss.common.ListUtils;
import ss.common.MapUtils;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.framework.networking2.io.PacketIdGenerator;
import ss.server.db.XMLDB;
import ss.server.networking.protocol.callbacks.LargePacketEvent;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.util.NameTranslation;

public abstract class AbstractSphereSender implements Runnable {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractSphereSender.class);

	protected final DialogsMainPeer dialogMainPeerOwner;

	protected final Hashtable session;

	protected final Document sphereDefinition;

	protected final VerifyAuth verifyAuth;

	protected final String openBackground;

	/**
	 * @param dialogMainPeerOwner
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	public AbstractSphereSender(
			final DialogsMainPeer dialogMainPeerOwner, final Hashtable session,
			final Document sphereDefinition, final VerifyAuth verifyAuth,
			final String openBackground) {
		super();
		this.dialogMainPeerOwner = dialogMainPeerOwner;
		this.session = session;
		this.sphereDefinition = sphereDefinition;
		this.verifyAuth = verifyAuth;
		this.openBackground = openBackground;
	}

	public final void run() {
		try {
			prepareSession();
			String sphere_id = (String) this.session.get(SC.SPHERE_ID);
			DialogsMainPeer handler = null;
			FilteredHandlers filteredHandlers = FilteredHandlers
					.getExactHandlersFromSession(this.session);
			for (DialogsMainPeer oneHandler : filteredHandlers) {
				handler = oneHandler;
			}
			if (handler == null) {
				logger
						.error("Target handler is null. Can't find target DMP. Will use this.dialogMainPeerOwner."
								+ StringUtils.getLineSeparator()
								+ " Target session is: "
								+ MapUtils.allValuesToString(this.session)
								+ StringUtils.getLineSeparator()
								+ ListUtils
										.valuesToString(DialogsMainPeerManager.INSTANCE
												.getHandlers()));
				handler = this.dialogMainPeerOwner;
			}
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, this.session);
			logger.info("SPHEREid here...: " + sphere_id);

			if (this.verifyAuth != null) {
				logger.info("adding verifyAuth...");
				dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, this.verifyAuth);
			} else {
				logger.info("CANNOT ADD VERIFY..NULL");
			}

			logger
					.info("sending sphere definition....check for critcombo info: ");

			dmpResponse.setDocumentValue(SC.SPHERE_DEFINITION,
					this.sphereDefinition);

			dmpResponse.setStringValue(SC.SPHERE, sphere_id);

			prepareDmpResponse(sphere_id, dmpResponse);
			Vector<String> surrounding = generateSurrounding(sphere_id, handler);
			// Show variables
			logCriteria(this.sphereDefinition);

			boolean isSupraQuery = isSupraQuery(this.sphereDefinition);

			logger.info("supraquery: " + isSupraQuery);

			if (isSupraQuery) {
				logger
						.error("processTrueSupraQuery is not supported. Session is "
								+ MapUtils.allValuesToString(this.session));
			}

			processSupraQuery(handler, dmpResponse, surrounding);
		} catch (Throwable ex) {
			logger.error("Sphere content handler failed", ex);
		}
	}

	/**
	 * @param sphere_id
	 * @param handler
	 * @return
	 */
	protected abstract Vector<String> generateSurrounding(String sphere_id,
			DialogsMainPeer handler);

	/**
	 * @param sphere_id
	 * @param dmpResponse
	 */
	protected void prepareDmpResponse(String sphere_id, DmpResponse dmpResponse) {
	}

	/**
	 * @param handler
	 * @param dmpResponse
	 * @param surrounding
	 */
	protected abstract void processSupraQuery(DialogsMainPeer handler,
			DmpResponse dmpResponse, Vector<String> surrounding);

	/**
	 * 
	 */
	protected void prepareSession() {
	}

	protected final Vector setUpPresenceInfoForGroups(DialogsMainPeer handler,
			Hashtable session, String sphere_id) {
		Vector presenceInfo;
		presenceInfo = handler.getXmldb().getSubPresence(sphere_id, sphere_id);

		presenceInfo = getOnlineForVectorOfStrings(presenceInfo, session);
		return presenceInfo;
	}

	protected final Vector setUpPresenceInfoForNonGroups(Hashtable session,
			Vector contactsOnly, String realName, String display) {
		Vector<String> available = new Vector<String>();
		if ( display != null && display.equals(realName)) {
			logger.info("CONTACTS ONLY.SIZE: " + contactsOnly.size());
			available = createMemberPresence(session, contactsOnly);
			if (available.size() == 0) {
				available.add(realName);
			}
		} else {
			available.add(display);
			available.add(realName);
		}
		return getOnlineForVectorOfStrings(available, session);
	}

	/**
	 * TODO:OPTIMIZATION_ISSUE: this functions calls countact_count*logged_users
	 * count.
	 */
	protected void sendPresenceUpdate(Vector presenceInfo,
			final DmpResponse dmpResponse, boolean checkReal) {
		logger.debug("sendPresenceUpdate " + presenceInfo.size());
		for (int i = 0; i < presenceInfo.size(); i++) {
			String check = (String) presenceInfo.get(i);
			FilteredHandlers filteredHandlers2 = FilteredHandlers
					.getExactNotHandlersForHandler(this.dialogMainPeerOwner);
			for (DialogsMainPeer handler2 : filteredHandlers2) {
				final String userNameFromHandler = handler2
						.get(HandlerKey.USERNAME);
				final String real = handler2.getVerifyAuth().getRealName(
						userNameFromHandler);

				if ( real != null && (check.lastIndexOf(checkReal ? real : userNameFromHandler) != -1)) {
					// Send to all but myself
					logger.debug("send presence update");
					handler2.sendFromQueue(dmpResponse);
				} else {
					logger.debug("skip, real " + real + ", user login "
							+ userNameFromHandler);
				}
			}
		}
	}

	protected void logHandlerVerifyAuth(DialogsMainPeer handler) {
		if (handler.getVerifyAuth() == null) {
			logger.info("handler verifyAuth null");
		} else {
			logger.info("handler verifyAuth not null");
		}
	}

	protected void logHandler(DialogsMainPeer handler) {
		if (handler == null) {
			logger.info("handler was null");
		} else {
			logger.info("handler not null");
		}
	}

	protected void processReallyAll(final DmpResponse temp,
			Document sphereDefinition, Hashtable<String, Document> noReallyAll,
			Vector<String> allOrder, Vector<String> contactsOnly,
			Hashtable<String, Document> reallyAll) {
		final Vector<String> order = (Vector<String>) reallyAll.get(SC.ORDER);
		reallyAll.remove(SC.ORDER);
		logger.info("ORDER RETURNED..." + order.size());
		String finalEndMoment = getFromReallyAll(reallyAll,
				SC.FINAL_END_MOMENT, null);
		String relativeBeginMoment = getFromReallyAll(reallyAll,
				SC.RELATIVE_BEGIN_MOMENT, null);
		/* #NOT_USED String pages = */getFromReallyAll(reallyAll,
				SC.TOTAL_PAGES, temp);
		String pagesType = getFromReallyAll(reallyAll, SC.TOTAL_PAGES_TYPE,
				temp);
		logger.info("putting totalpages: " + pagesType);
		if (finalEndMoment != null) {

			String queryId = NameTranslation.returnQueryId(sphereDefinition);
			setQueryMoments(queryId, relativeBeginMoment, finalEndMoment, "1");
		}
		Vector<String> contacts = (Vector<String>) reallyAll
				.get(SC.CONTACTS_ONLY);
		if (contacts != null) {
			contactsOnly.addAll(contacts);
			reallyAll.remove(SC.CONTACTS_ONLY);
		}
		noReallyAll.putAll(reallyAll);
		allOrder.addAll(order);
	}

	protected String getFromReallyAll(Hashtable reallyAll, String key,
			final DmpResponse temp) {
		String string = null;
		Object o = null;
		try {
			o = reallyAll.get(key);
			if (o != null) {
				string = o.toString();
			}
			if (string != null) {
				if (temp != null) {
					temp.setStringValue(key, string);
				}
				reallyAll.remove(key);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return string;
	}

	/**
	 * Used from processTrueSupraServer
	 * 
	 * @param handler
	 * @param surrounding
	 * @return
	 */
	@SuppressWarnings( { "unchecked", "unused" })
	protected Hashtable<String, Document> getReallyAll(DialogsMainPeer handler,
			Vector surrounding) {
		return handler.getXmldb().getForSphereLight(surrounding);
	}

	@SuppressWarnings("unchecked")
	protected Hashtable<String, Document> getReallyAll(DialogsMainPeer handler,
			Hashtable session, Document sphereDefinition, String dataId)
			throws DocumentException {
		return handler.getXmldb().getForSphereLight(session, handler, dataId,
				sphereDefinition, null, null);
	}

	protected boolean isSupraQuery(Document sphereDefinition) {
		boolean isSupraQuery = false;
		if (sphereDefinition.getRootElement().element("isSupraQuery") != null) {
			if (sphereDefinition.getRootElement().element("isSupraQuery")
					.attributeValue("value").equals("true")) {
				isSupraQuery = true;
			}
		}
		return isSupraQuery;
	}

	protected void logCriteria(Document sphereDefinition) {
		Element criteria = sphereDefinition.getRootElement()
				.element("criteria");
		if (criteria != null) {

			logger
					.info("criteria does not equal null....check for most used by me");

			String criteriaText = criteria.attributeValue("value");
			logger.info("criteria text: " + criteriaText);
			if (criteriaText.equals("Most used by me")) {
				// TODO set? where?
				logger
						.info("set criteria text to most used by me in query definition");
			}
		}
	}

	protected Vector<String> processSphereScope(Hashtable session,
			Document sphereDefinition, Vector<String> surrounding,
			String data_id, String scope) {

		if (!scope.equals("Everything in selected only")) {

			surrounding = getOtherSphereIds(session, sphereDefinition);

			if (scope.equals("Everyone else's bookmarks and feeds")
					|| scope.equals("Mine and everyone's bookmarks and feeds")) {

				logger
						.info("show only bookmarks and keywords...perhaps contacts if internal");
				Element threadTypes = sphereDefinition.getRootElement()
						.element("thread_types");

				if (threadTypes != null) {

					Element newThreadTypes = new DefaultElement("thread_types");
					Vector<String> enabled = new Vector<String>();
					enabled.add("keywords");
					enabled.add("bookmark");
					enabled.add("rss");

					for (String oneEnabled : enabled) {

						String xPath = "//sphere/thread_types/" + oneEnabled
								+ "[@enabled='true']";
						logger.info("TRYING THIS xpath: " + xPath);
						Object result = sphereDefinition.selectObject(xPath);
						if ((result != null) && (result instanceof Element)) {
							logger.info("IT WAS NOT NULL....must add: "
									+ oneEnabled + " : "
									+ ((Element) result).asXML());
							newThreadTypes.addElement(oneEnabled).addAttribute(
									"enabled", "true").addAttribute("modify",
									"own");
						} else {
							logger.info("it was null...cannot add: "
									+ oneEnabled);
						}
					}

					sphereDefinition.getRootElement().remove(threadTypes);
					sphereDefinition.getRootElement().add(newThreadTypes);
				}
			}

			logger.info("Surrounding size: " + surrounding.size());
		}
		return surrounding;
	}

	// / Delegates to DialogMainPeer

	/**
	 * @param session
	 * @param sphereDefinition
	 * @return
	 */
	protected final Vector<String> getOtherSphereIds(Hashtable session,
			Document sphereDefinition) {
		return this.dialogMainPeerOwner.getOtherSphereIds(session,
				sphereDefinition);
	}

	/**
	 * @return
	 */
	protected final XMLDB getXmldb() {
		return this.dialogMainPeerOwner.getXmldb();
	}

	/**
	 * @param dmpResponse
	 * @param name
	 */
	protected final void sendFromQueue(DmpResponse dmpResponse, String name) {
		DialogsMainPeer.sendFromQueue(dmpResponse, name);
	}

	/**
	 * @param presenceInfo
	 * @param session
	 * @return
	 */
	protected final Vector getOnlineForVectorOfStrings(Vector presenceInfo,
			Hashtable session) {
		return this.dialogMainPeerOwner.getOnlineForVectorOfStrings(
				presenceInfo, session);
	}

	/**
	 * @param session
	 * @param contactsOnly
	 * @return
	 */
	protected final Vector<String> createMemberPresence(Hashtable session,
			Vector contactsOnly) {
		return this.dialogMainPeerOwner.createMemberPresence(session,
				contactsOnly);
	}

	/**
	 * @param queryId
	 * @param relativeBeginMoment
	 * @param finalEndMoment
	 * @param currentPage
	 */
	protected final void setQueryMoments(String queryId,
			String relativeBeginMoment, String finalEndMoment,
			String currentPage) {
		this.dialogMainPeerOwner.setQueryMoments(queryId, relativeBeginMoment,
				finalEndMoment, currentPage);
	}

	/**
	 * @param handler
	 * @param dmpResponse
	 * @param sphere_id
	 * @param noReallyAll
	 * @param allOrder
	 * @param contactsOnly
	 * @param presenceInfo
	 */
	protected final void sendResults(DialogsMainPeer handler,
			final DmpResponse dmpResponse, String sphere_id,
			Hashtable<String, Document> noReallyAll,
			final Vector<String> allOrder, Vector<String> contactsOnly,
			Vector presenceInfo) {
		dmpResponse.setVectorValue(SC.PRESENCE_INFO, presenceInfo);
		dmpResponse.setVectorValue(SC.CONTACTS_ONLY, contactsOnly);
		dmpResponse.setMapValue(SC.ALL, noReallyAll);
		dmpResponse.setVectorValue(SC.ORDER, allOrder);
		dmpResponse.setStringValue(SC.SPHERE, sphere_id);
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.RECEIVE_RESULTS_FROM_XMLSEARCH);
		dmpResponse.setStringValue(SC.SHOW_PROGRESS, this.openBackground
				.equals("false") ? "true" : "false");

		final int largePacketId = PacketIdGenerator.INSTANCE.nextId();
		dmpResponse.setIntValue(DmCommand.DESIRED_PACKET_ID, largePacketId);
		String spherename = this.dialogMainPeerOwner.getVerifyAuth()
				.getDisplayName(sphere_id);
		if (spherename == null) {
			spherename = sphere_id;
		}
		LargePacketEvent event = new LargePacketEvent(largePacketId, spherename);
		event.fireAndForget(handler);
		handler.sendFromQueue(dmpResponse);
	}

}
