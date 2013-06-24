/**
 * Jul 5, 2006 : 1:49:19 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.root.SupraTab;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DmpResponse;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring This is passive handler called by server when some
 * user login/logout.
 * 
 */
public class RefreshPresenceHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RefreshPresenceHandler.class);

	private final DialogsMainCli cli;

	public RefreshPresenceHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	/**
	 * @return
	 */
	private SupraSphereFrame getSF() {
		return this.cli.getSF();
	}

	public void handleAddPresence(final Hashtable update) {
		DmpResponse dmpResponse = new DmpResponse(update);
		final String contactName = dmpResponse
				.getStringValue(SessionConstants.CONTACT_NAME);
		final boolean isOnline = dmpResponse
				.getBooleanValue(SessionConstants.IS_ONLINE);
		final UiUpdateInvoker invoker = new UiUpdateInvoker(this.cli);
		invoker.swtBeginInvoke(new Runnable() {
			public void run() {
				for (MessagesPane messagePane : getSF()
						.getMessagePanesController().getAll()) {
					logger.info(" refresh presence for "
							+ messagePane.getSphereStatement().getDisplayName()
							+ " -> contact " + contactName);
					messagePane.refreshContactPresence(contactName, isOnline);
				}
				SupraTab tab = getSF().getRootTab();
				if (tab != null) {
					tab.refreshMemberPresence( contactName, isOnline );
				}
			}
		});
	}

	public String getProtocol() {
		return SSProtocolConstants.REFRESH_PRESENCE;
	}

	public void handle(Hashtable update) {
		handleAddPresence(update);
	}

}
