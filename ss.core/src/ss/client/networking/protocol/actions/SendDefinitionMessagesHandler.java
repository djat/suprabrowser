package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class SendDefinitionMessagesHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendDefinitionMessagesHandler.class);

	private final DialogsMainCli cli;
	
	public SendDefinitionMessagesHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_DEFINITION_MESSAGES;
	}

	@SuppressWarnings("unchecked")
	public void sendDefinitionMessage(Hashtable mySession) {
		Hashtable temp = new Hashtable();
		temp.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SEND_DEFINITION_MESSAGES);

		temp.put(SessionConstants.SESSION, mySession);
		this.cli.sendFromQueue(temp);
	}

}
