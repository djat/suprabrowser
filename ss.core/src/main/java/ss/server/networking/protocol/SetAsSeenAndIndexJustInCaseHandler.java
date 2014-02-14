package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.DnldURL;

public class SetAsSeenAndIndexJustInCaseHandler implements ProtocolHandler {

	private static final String MESSAGE_ID = "message_id";

	private static final String THREAD_ID = "thread_id";

	private static final String VALUE = "value";

	private static final String ADDRESS = "address";

	private DialogsMainPeer peer;

	public SetAsSeenAndIndexJustInCaseHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SET_AS_SEEN_AND_INDEX_JUST_IN_CASE;
	}

	public void handle(Hashtable update) {
		handleSetAsSeenAndIndexJustInCase(update);

	}

	public void handleSetAsSeenAndIndexJustInCase(final Hashtable update) {
		final Document rssSubDoc = (Document) update.get(SC.DOC);
		new Thread() {
			private Logger logger = SSLogger.getLogger(this.getClass());

			public void run() {

				this.logger.info("Now set it as seen: " + rssSubDoc.asXML());

				String url = rssSubDoc.getRootElement().element(ADDRESS)
						.attributeValue(VALUE);
				String threadId = rssSubDoc.getRootElement().element(THREAD_ID)
						.attributeValue(VALUE);

				DnldURL.INSTANCE.downloadOnly(url, rssSubDoc, threadId);
				DnldURL.INSTANCE.indexOnly(rssSubDoc.getRootElement().element(MESSAGE_ID)
						.attributeValue(VALUE), threadId, SetAsSeenAndIndexJustInCaseHandler.this.peer.getXmldb());
			}
		}.start();
	}

}
