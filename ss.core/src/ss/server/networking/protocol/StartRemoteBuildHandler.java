package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.MethodProcessing;
import ss.server.networking.SC;

public class StartRemoteBuildHandler implements ProtocolHandler {
	private static final String SERVER = "server";

	private static final String SERVANT = "servant";

	private Logger logger = SSLogger.getLogger(this.getClass());

	public StartRemoteBuildHandler() {
	}

	public String getProtocol() {
		return SSProtocolConstants.START_REMOTE_BUILD;
	}

	public void handle(Hashtable update) {
		handleStartRemoteBuild(update);
	}

	public void handleStartRemoteBuild(final Hashtable update) {
		this.logger.info("start remote build at this dir...prob need port too");

		final Hashtable finalSession = (Hashtable) update.get(SC.SESSION);
		Document doc = (Document) update.get(SC.DOC);
		String cliSerServant = (String) update.get(SC.CLI_SER_SERVANT);
		String restart = (String) update.get(SC.RESTART);

		this.logger.info("cliservant: " + cliSerServant);

		if (cliSerServant.equals(SERVANT)) {
			restart = "true";
		}

		this.logger.info("restart..." + restart);
		MethodProcessing.startBuild(finalSession, doc, SERVER, restart
				.equals("true"));
	}

}
