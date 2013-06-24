/**
 * Jul 5, 2006 : 5:26:53 PM
 */
package ss.client.networking.protocol.obosolete;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @deprecated
 * @author dankosedin
 * 
 */
public class RebootHandler implements ProtocolHandler {

	// TODO move
	private static final String SYSTEM_NAME = "system_name";

	private static final String DISPLAY_NAME = "display_name";

	private static final String VALUE = "value";

	private static final String ORDER = "order";

	private static final String BUILD_ORDER = "build_order";

	private static final String AUTOLOGIN = "autologin";

	private DialogsMainCli cli;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RebootHandler.class);

	/**
	 * @deprecated
	 * @param cli
	 */
	public RebootHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleReboot(final Hashtable update) {
		// System.out.println("Got reboot request");
		Document createDoc = DocumentHelper.createDocument();
		Element root = createDoc.addElement(BUILD_ORDER);
		int total = 0; // this.cli.getSF().tabbedPane.getTabCount();
		for (int i = 0; i < total; i++) {
			final String titleAt = null; // this.cli.getSF().tabbedPane.getTitleAt(i);
			root.addElement(ORDER).addAttribute(VALUE,
					(new Integer(i)).toString()).addAttribute(DISPLAY_NAME,
					titleAt).addAttribute(
					SYSTEM_NAME,
					this.cli.getVerifyAuth().getSystemName(
							titleAt));
		}

		this.cli.saveTabOrderToContact(this.cli.session, createDoc);
		this.cli.restartWithAnt(AUTOLOGIN);
	}

	public String getProtocol() {
		return SSProtocolConstants.REBOOT;
	}

	public void handle(Hashtable update) {
		handleReboot(update);
	}

	@SuppressWarnings("unchecked")
	public void sendRebootOrder(final Hashtable session) {
		Hashtable toSend = (Hashtable) session.clone();

		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.REBOOT);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
