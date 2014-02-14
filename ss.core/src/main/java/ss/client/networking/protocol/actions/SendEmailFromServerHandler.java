package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class SendEmailFromServerHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendEmailFromServerHandler.class);

	private final DialogsMainCli cli;
	
	public SendEmailFromServerHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_EMAIL_FROM_SERVER;
	}

	@SuppressWarnings("unchecked")
	public void sendEmailFromServer(Hashtable session,
			EmailAddressesContainer addressesContainer,
			AttachedFileCollection files, StringBuffer sb, String subject,
			String replySphere) {

		logger.warn("Sending here");
		Hashtable toSend = (Hashtable) session.clone();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SEND_EMAIL_FROM_SERVER);
		if ((files != null) && (files.getCount() != 0)) {
			update.put(SessionConstants.TO_EMAIL_ATTACHED_FILES, files);
		}

		update.put(SessionConstants.TO_EMAIL_CONTAINER, addressesContainer);
		update.put(SessionConstants.BODY, sb.toString());
		update.put(SessionConstants.SUBJECT, subject);
		update.put(SessionConstants.REPLY_SPHERE, replySphere);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);

	}

}
