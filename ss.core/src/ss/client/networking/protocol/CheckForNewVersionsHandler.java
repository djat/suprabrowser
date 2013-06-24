/**
 * Jul 5, 2006 : 1:11:06 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class CheckForNewVersionsHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CheckForNewVersionsHandler.class);

	private final DialogsMainCli cli;

	public CheckForNewVersionsHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleCheckForNewVersions(final Hashtable update) {

		logger.info("Check for new versions handler performed");

		Document toDownload = (Document) update
				.get(SessionConstants.TO_DOWNLOAD);

		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);

		Vector files = new Vector(toDownload.getRootElement().elements());

		if (files.size() > 0) {
			logger.error("Download new version is not supported: "
					+ files.size());
			// Object[] options = { "Ok" };
			//
			// this.cli.getSF().getWelcomeScreen().closeFromWithin();
			//
			// this.cli.getSF().onTop();
			//
			// JOptionPane
			// .showOptionDialog(
			// null,// this.cli.getSF().tabbedPane,
			// "New versions of SupraSphere libraries will be downloaded. You
			// might have to login again after the downloading has completed.",
			// "SupraSphere Software Update",
			// JOptionPane.DEFAULT_OPTION,
			// JOptionPane.INFORMATION_MESSAGE, null, options,
			// options[0]);
			//
			// Hashtable sendSession = (Hashtable) this.cli.session.clone();
			// String address = (String)
			// sendSession.get(SessionConstants.ADDRESS);
			// String port = (String) sendSession.get(SessionConstants.PORT);
			// SupraClient sClient = new SupraClient(address, port);
			//
			// sClient.setSupraSphereFrame(this.cli.getSF());
			// Hashtable getInfo = new Hashtable();
			//
			// getInfo.put(SessionConstants.FILES, files);
			//
			// sendSession.put(SessionConstants.GET_INFO, getInfo);
			// sClient
			// .startZeroKnowledgeAuth(sendSession,
			// "DownloadMultipleFiles");
		}

		logger.info("send Definition Message");

		try {
			Thread.sleep(250);
		} catch (InterruptedException ex) {
		}

		this.cli.sendDefinitionMessage();
	}

	public String getProtocol() {
		return SSProtocolConstants.CHECK_FOR_NEW_VERSIONS;
	}

	public void handle(Hashtable update) {
		handleCheckForNewVersions(update);
	}

	@SuppressWarnings("unchecked")
	public void checkForNewVersion(Hashtable session, Document versionsDoc) {
		Hashtable forQueue = new Hashtable();
		forQueue.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.CHECK_FOR_NEW_VERSIONS);
		forQueue.put(SessionConstants.SESSION, session);
		forQueue.put(SessionConstants.VERSIONS_DOC, versionsDoc);
		this.cli.sendFromQueue(forQueue);
	}

}
