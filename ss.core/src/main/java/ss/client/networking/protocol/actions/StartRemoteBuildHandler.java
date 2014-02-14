package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class StartRemoteBuildHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public StartRemoteBuildHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.START_REMOTE_BUILD;
	}

	@SuppressWarnings("unchecked")
	public void startRemoteBuild(Hashtable session2, Document doc,
			String cliSerServant, String restart) {

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable update = new Hashtable();

		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.START_REMOTE_BUILD);
		update.put(SessionConstants.DOC, doc);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.RESTART, restart);
		update.put(SessionConstants.CLI_SER_SERVANT, cliSerServant);
		this.cli.sendFromQueue(update);

	}

}
