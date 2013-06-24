package ss.server.networking.protocol.obsolete;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

/**
 * @deprecated
 * @author dankosedin
 *
 */
public class GetStatsForSphereHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	/**
	 * @deprecated
	 * @param peer
	 */
	public GetStatsForSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_STATS_FOR_SPHERE;
	}

	public void handle(Hashtable update) {
		handleGetStatsForSphere(update);
	}

	public void handleGetStatsForSphere(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String systemName = (String) update.get(SC.SYSTEM_NAME);
		String messageId = (String) update.get(SC.MESSAGE_ID);
		
		String username = (String) session.get(SC.USERNAME);
		String realName = (String) session.get(SC.REAL_NAME);		

		String personalSphere = this.peer.getXmldb().getUtils().getPersonalSphere(
				username, realName);

		Document stats = this.peer.getXmldb().getStatisticsDoc(personalSphere,
				systemName);

		// logger.warn("STATISTICS FOR THIS SPHERE:
		// "+systemName);
		if (stats != null) {
			// logger.warn("total..."+stats.getRootElement().element("since_local_mark").attributeValue("total_in_sphere"));
			// logger.warn("replies to
			// mine..."+stats.getRootElement().element("since_local_mark").attributeValue("replies_to_mine"));
			// logger.warn("since
			// mark..."+stats.getRootElement().element("since_local_mark").attributeValue("since_mark"));
			FilteredHandlers filteredHandlers = FilteredHandlers
					.getExactHandlersFromSession(session);
			for ( DialogsMainPeer handler : filteredHandlers) {
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.GET_STATS_FOR_SPHERE);
				dmpResponse.setDocumentValue(SC.STATS_DOC, stats);
				dmpResponse.setStringValue(SC.SYSTEM_NAME, systemName);
				dmpResponse.setMapValue(SC.SESSION, session);
				dmpResponse.setStringValue(SC.MESSAGE_ID, messageId);
				this.peer.sendFromQueue(dmpResponse);
			}
		}
	}

}
