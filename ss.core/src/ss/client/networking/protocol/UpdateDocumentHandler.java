/**
 * Jul 5, 2006 : 6:13:10 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.event.tagging.TagManager;
import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UpdateDocumentHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateDocumentHandler.class);

	protected final DialogsMainCli cli;

	public UpdateDocumentHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleUpdateDocument(final Hashtable update) {
		logger.warn("Got acknowledgement!");
		update.put(SessionConstants.IS_UPDATE, "false");

		String isUpdate2 = (String) update.get(SessionConstants.IS_UPDATE2);

		if (isUpdate2 != null) {
			if ((isUpdate2).equals("onlyIfExists")) {
				logger.warn("Only if Exists!");
				update.put(SessionConstants.IS_UPDATE, "onlyIfExists");
			}
		}

		final PostponedUpdate postponedUpdate = new PostponedUpdate( update );
		Statement statement = postponedUpdate.getStatement();
		if ( statement.isKeywords() ) {
			updateTags(statement);
		}
		else {
			this.cli.callInsert(postponedUpdate);
		}		
	}


	/**
	 * @param statement
	 */
	protected void updateTags(Statement statement) {
		KeywordStatement updatedKeyword = KeywordStatement.wrap( statement.getBindedDocument() );
		TagManager.INSTANCE.tagUpdated(updatedKeyword);
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE_DOCUMENT;
	}

	@SuppressWarnings("unchecked")
	public void handle(Hashtable update) {
		handleUpdateDocument(update);
	}

	@SuppressWarnings("unchecked")
	public void updateDocument(final Hashtable session, final String filename,
			final org.dom4j.Document sendDoc) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.UPDATE_DOCUMENT);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.FILENAME, filename);
		update.put(SessionConstants.DOCUMENT, sendDoc);
		this.cli.sendFromQueue(update);
	}

}
