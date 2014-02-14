/**
 * Jul 4, 2006 : 5:18:31 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class VoteDocumentHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VoteDocumentHandler.class);

	private final DialogsMainCli cli;

	public VoteDocumentHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleVoteDocument(final Hashtable update) {
		downloadVoteDocumentDone(update);
	}

	@SuppressWarnings("unchecked")
	public void downloadVoteDocumentDone(final Hashtable update) {
		// TODO this look very strange (is_update + protocol)
		update.put(SessionConstants.IS_UPDATE, SSProtocolConstants.VOTE);
		update.put(SessionConstants.CONFIRM_DELIVERY, "false");
		this.cli.callInsert(new PostponedUpdate(update));
	}

	public String getProtocol() {
		return SSProtocolConstants.VOTE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleVoteDocument(update);
	}

	@SuppressWarnings("unchecked")
	public void voteDocument(final Hashtable session, final String filename,
			final org.dom4j.Document sendDoc) {
		Hashtable toSend = (Hashtable) session.clone();

		Hashtable update = new Hashtable();
		update
				.put(SessionConstants.PROTOCOL,
						SSProtocolConstants.VOTE_DOCUMENT);

		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.FILENAME, filename);
		update.put(SessionConstants.DOCUMENT, sendDoc);
		this.cli.sendFromQueue(update);
	}

}
