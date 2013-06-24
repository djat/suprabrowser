/**
 * Jul 5, 2006 : 4:38:18 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.balloons.BalloonsController;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class NotifySystemTrayHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NotifySystemTrayHandler.class);
	
	private final DialogsMainCli cli;

    public NotifySystemTrayHandler(DialogsMainCli cli) {
	this.cli = cli;
    }

    public void handleNotifySystemTray(final Hashtable update) {
	logger.info("got system tray notify request");

	final Document doc = (Document) update.get(SessionConstants.DOCUMENT);
	final Hashtable bwSession = (Hashtable) update
		.get(SessionConstants.SESSION);

	String sphereId = (String) bwSession.get(SessionConstants.SPHERE_ID2);
	MessagesPane mp = this.cli.getSF().getMessagesPaneFromSphereId(sphereId);

	if (mp != null) {
		logger.error("NOMER 2");
		BalloonsController.INSTANCE.addBalloon(doc, false, mp);
	}
    }

    public String getProtocol() {
	return SSProtocolConstants.NOTIFY_SYSTEM_TRAY;
    }

    public void handle(Hashtable update) {
	handleNotifySystemTray(update);
    }

    @SuppressWarnings("unchecked")
	public void notifySystemTray(final Hashtable session, Vector memberList,
	    Document doc) {
	String realName = (String) session.get(SessionConstants.REAL_NAME);
	memberList.remove(realName);
	logger.info(" ");
	Vector members = new Vector();
	for (Object o : memberList) {
	    if (o instanceof String) {
		String loginName = (String) o;
		members.add(loginName);
		logger.info("member =" + loginName);
	    }
	    if (o instanceof Document) {
		Document membership = (Document) o;
		members.add(membership.getRootElement().element("login_name")
			.attributeValue("value"));
		logger.info("member =" + membership.asXML());
	    }
	}
	logger.info(" ");
	Hashtable toSend = (Hashtable) session.clone();
	// System.out.println("when publishing, the sphere_id is: " +
	// (String) toSend.get("sphere_id"));
	toSend.remove(SessionConstants.PASSPHRASE);
	Hashtable update = new Hashtable();
	update.put(SessionConstants.PROTOCOL,
		SSProtocolConstants.NOTIFY_SYSTEM_TRAY);

	update.put(SessionConstants.MEMBER_LIST, members);
	update.put(SessionConstants.SESSION, toSend);
	update.put(SessionConstants.DOCUMENT, doc);
	this.cli.sendFromQueue(update);

    }

}
