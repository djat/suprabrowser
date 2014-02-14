/**
 * Jul 5, 2006 : 4:49:25 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class SendPopupNotificationHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(SendPopupNotificationHandler.class);
	
	private final DialogsMainCli cli;

	public SendPopupNotificationHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleSendPopupNotification(final Hashtable update) {
		logger.info("got popup notification request");

		final Document doc = (Document) update.get(SessionConstants.DOCUMENT);
		final Hashtable bwSession = (Hashtable) update
				.get(SessionConstants.SESSION);

		String sphereId = (String) bwSession.get(SessionConstants.SPHERE_ID);
		MessagesPane mP = this.cli.getSF()
				.getMessagesPaneFromSphereId(sphereId);
		// doc.getRootElement().element("reconfirmed").detach();
		if (mP != null) {
			PopUpController.INSTANCE.popupNotification(doc);
		}
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_POPUP_NOTIFICATION;
	}

	public void handle(Hashtable update) {
		handleSendPopupNotification(update);

	}

	@SuppressWarnings("unchecked")
	public void sendPopupNotification(final Hashtable session,
			Vector memberList, Document doc) {
		String realName = (String) session.get(SessionConstants.REAL_NAME);

		// Remove self so I don't get notification
		memberList.remove(realName);

		Hashtable toSend = (Hashtable) session.clone();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SEND_POPUP_NOTIFICATION);

		update.put(SessionConstants.MEMBER_LIST, memberList);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, doc);
		this.cli.sendFromQueue(update);

	}

}
