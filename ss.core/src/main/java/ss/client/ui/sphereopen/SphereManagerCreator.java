/**
 * 
 */
package ss.client.ui.sphereopen;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraMenuBar;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.balloons.BalloonsController;
import ss.client.ui.messagedeliver.AbstractDeliveringElement;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.client.ui.tempComponents.CompositeMessagesPane;
import ss.common.ThreadUtils;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereStatement;
import ss.util.SessionConstants;

/**
 * @author zobo
 * 
 */
class SphereManagerCreator {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereManagerCreator.class);

	private static final String VALUE = "value";

	private static final String CONFIRMED = "confirmed";

	private static final String DIV3 = "div3";

	private static final String DIV2 = "div2";

	private static final String DIV1 = "div1";

	private static final String DIV0 = "div0";

	private static final String QUERY_ID = "query_id";

	private static final String QUERY = "query";

	private static final String DIALOGS_MAIN_CLI = "DialogsMainCli";

	static void create(final SphereCreationContext context) {
		ThreadUtils.start(new Runnable() {
			public void run() {
				try {
					createBody(context);
				} catch (Throwable ex){
					logger.error("Exception while trying to create MessagesPane", ex);
				}
			}
		}, SphereManagerCreator.class);
	}

	@SuppressWarnings("unchecked")
	private static void createBody(final SphereCreationContext context) {
		final Hashtable update = context.getUpdate();
		final DialogsMainCli client = context.getClient();
		
		final String sphere_tab = (String) update.get(SessionConstants.SPHERE);

		logger.info("sphere tab: " + sphere_tab);

		final VerifyAuth verifyAuth = (VerifyAuth) update
				.get(SessionConstants.VERIFY_AUTH);
		final Hashtable newSession = (Hashtable) update.get(SessionConstants.SESSION);
		final String sphere_id = (String) newSession.get(SessionConstants.SPHERE_ID2);
		logger.info("sphereID recieved: " + sphere_id);
		String supraSphere = (String) newSession
				.get(SessionConstants.SUPRA_SPHERE);
		
		final SupraSphereFrame sf = SupraSphereFrame.INSTANCE;
		try {
			final String registeredSession = (String) (sf.getRegisteredSession(
				supraSphere, DIALOGS_MAIN_CLI)).get(SessionConstants.SESSION);
			newSession.put("session", registeredSession);
			final String sphereURL = (String) newSession.get(SessionConstants.SPHERE_URL);
			logger.warn("(String)sphereurl: " + sphereURL);
			final DialogsMainCli activeCli = sf.getActiveConnections()
				.getActiveConnection(sphereURL);
			if (activeCli != null) {
				activeCli.setSession(newSession);
			}
			logger.warn("cli was " + ((activeCli == null) ? "" : "not ") + "null"
					+ sphereURL);
		} catch (Throwable ex) {
			logger.error("Error in processing SF part of sphere creation");
			return;
		} finally {
			client.setVerifyAuth(verifyAuth);
		}
		// activeCli.setCompletedSession(newSession);

		sf.beforeCreateMessagesPane();

		logger.warn("here is the sphere id : " + sphere_id);
		logger.warn("sphere tab..." + sphere_tab);

		// String newmp = cli.getVerifyAuth().getDisplayName(sphere_tab);
		final boolean isSphereWasRequested = SphereOpenManager.INSTANCE
				.isSphereWasRequested(sphere_id);

		final Document sphereDefinition = (Document) update
				.get(SessionConstants.SPHERE_DEFINITION);
		if (sphereDefinition != null) {
			logger.info("SpherDefinition=" + sphereDefinition.asXML());
		}

		String newmp = SphereStatement.wrap(sphereDefinition).getDisplayName();

		Element query = null;
		try {
			query = sphereDefinition.getRootElement().element(QUERY);
		} catch (NullPointerException exc) {
			logger.error("NPE", exc);
		}

		if (query != null) {
			newSession.put(SessionConstants.QUERY_ID, query
					.attributeValue(QUERY_ID));
		}

		Document create_definition = (Document) update
				.get(SessionConstants.CREATE_DEFINITION);
		if (create_definition != null)
			logger.info("CreDefinition=" + create_definition.asXML());

		Element windowPosition = null;
		double div0 = -1;
		double div1 = -1;
		double div2 = -1;
		double div3 = -1;

		if (query != null) {
			Hashtable session = (Hashtable) update.get("session");
			if (session.containsKey("div0") && session.containsKey("div1")
					&& session.containsKey("div2")
					&& session.containsKey("div3")) {
				div0 = ((Double) session.get("div0")).doubleValue();
				div1 = ((Double) session.get("div1")).doubleValue();
				div2 = ((Double) session.get("div2")).doubleValue();
				div3 = ((Double) session.get("div3")).doubleValue();
			}
		} else {
			windowPosition = client.getVerifyAuth().getWindowPositionFor(
					sphere_id);
			if (windowPosition != null) {
				div0 = new Double(windowPosition.attributeValue(DIV0))
						.doubleValue();
				div1 = new Double(windowPosition.attributeValue(DIV1))
						.doubleValue();
				div2 = new Double(windowPosition.attributeValue(DIV2))
						.doubleValue();
				div3 = new Double(windowPosition.attributeValue(DIV3))
						.doubleValue();
			}
		}

		MessagesPane mp_new = null;
		if (!isHistoryRequest(sphereDefinition)) {
			try {
				if (div0 != -1 && div1 != -1 && div2 != -1 && div3 != -1) {
					mp_new = CompositeMessagesPane.createMessagesPane(
							newSession, sf, newmp, client,
							sphereDefinition, div0, div1, div2, div3);
				} else {
					mp_new = CompositeMessagesPane.createMessagesPane(
							newSession, sf, newmp, sf.client,
							sphereDefinition);
				}
				sf.addMessagesPane(newmp, mp_new);
				logger.info("there was order on store for that id: "
						+ sphere_id);
			} catch (Exception e) {
				mp_new = CompositeMessagesPane.createMessagesPane(newSession,
						sf, newmp, client, sphereDefinition);
				sf.addMessagesPane(newmp, mp_new);
				logger
						.info("there was Exception in creating with order. So create as default: "
								+ sphere_id);

			}
		} else {
			String mpUnique = getMPUnique(sphereDefinition);
			mp_new = getMpByUnuqieId(sphere_id, mpUnique);
			logger.info("uniqueId=" + mpUnique);
			logger.info("finded message_pane=" + mp_new);
		}

		mp_new.checkQueryAndSetSphereDefinition(sphereDefinition);
		mp_new.setCreateDefinition(create_definition);

		processAllMessages(update, mp_new, sphere_id);

		processFirstDoc(newSession, mp_new);		
		if (!isHistoryRequest(sphereDefinition)) {
			sf.addJTab(newmp, mp_new, getPresenceInfo(update),
					isSphereWasRequested);
		}
		final AtomicReference<MessagesPane> ar = new AtomicReference<MessagesPane>(
				mp_new);
		final SupraMenuBar smb = sf.getMenuBar();
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {				
				String systemName = SphereStatement.wrap(
						ar.get().getSphereDefinition()).getSystemName();
				if (client.isLoginSphere(systemName)) {
					smb.fillFavourites();
				}
			}
		});
		processUnlock(mp_new);
	}

	/**
	 * 
	 */
	private static void processUnlock(MessagesPane mp) {
		final AtomicReference<MessagesPane> ar = new AtomicReference<MessagesPane>(
				mp);		
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				ar.get().notifyUnlock();
			}
		});		
	}

	/**
	 * @param mpUnique
	 * @return
	 */
	private static MessagesPane getMpByUnuqieId(String sphereId, String uniqueId) {
		return SupraSphereFrame.INSTANCE.getMessagesPaneFromSphereId(sphereId,
				uniqueId);
	}

	/**
	 * @param sphereDefinition
	 * @return
	 */
	private static String getMPUnique(Document sphereDefinition) {
		return sphereDefinition.getRootElement().element("search").element(
				"message_pane_id").attributeValue("value");
	}

	/**
	 * @param sphereDefinition
	 * @return
	 */
	private static boolean isHistoryRequest(Document sphereDefinition) {
		Element search = sphereDefinition.getRootElement().element("search");
		return (search == null) ? false
				: search.element("message_pane_id") != null;
	}

	/**
	 * @param update
	 * @param mp_new
	 */
	@SuppressWarnings("unchecked")
	static private void processAllMessages(final Hashtable update,
			MessagesPane mp_new, String sphere_id) {
		Hashtable all = (Hashtable) update.get(SessionConstants.ALL);
		String highligth = (String) update.get(SessionConstants.HIGHLIGTH);
		
		Document[] docsInOrder = (Document[])all.get("docs_in_order");

		AbstractDeliveringElement elem = DeliverersManager.FACTORY.createList(
				all, highligth, sphere_id);
		DeliverersManager.INSTANCE.insert(elem, mp_new);

		// mp_new.insertAll(order, (Hashtable) all.clone(), highligth);
	}

	/**
	 * @param update
	 * @return
	 */
	static private Vector getPresenceInfo(final Hashtable update) {
		Vector presenceInfo = (Vector) update
				.get(SessionConstants.PRESENCE_INFO);
		logger.info("presence info "
				+ ((presenceInfo != null) ? "size: " + presenceInfo.size()
						: "null...."));
		return presenceInfo;
	}

	/**
	 * @param newSession
	 * @param mp_new
	 */
	static private void processFirstDoc(Hashtable newSession,
			MessagesPane mp_new) {
		Document firstDoc = (Document) newSession
				.get(SessionConstants.FIRST_DOC);

		if (firstDoc != null) {

			String testId = (String) newSession
					.get(SessionConstants.SPHERE_ID2);
			String confirmed = null;

			try {
				confirmed = firstDoc.getRootElement().element(CONFIRMED)
						.attributeValue(VALUE);
			} catch (Exception npe) {
			}

			if (!testId.equals("group") && confirmed.equals("true")) {

				BalloonsController.INSTANCE.addBalloon(firstDoc, true, mp_new);

			}
			newSession.remove(SessionConstants.FIRST_DOC);
		} else {
			logger.info("First doc did not survive the trip");
		}
	}

}
