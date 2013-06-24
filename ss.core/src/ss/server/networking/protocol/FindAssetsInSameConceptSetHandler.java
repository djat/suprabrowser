package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class FindAssetsInSameConceptSetHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public FindAssetsInSameConceptSetHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.FIND_ASSETS_IN_SAME_CONCEPT_SET;
	}

	public void handle(Hashtable update) {
		handleFindAssetsInSameConceptSet(update);
	}

	public void handleFindAssetsInSameConceptSet(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String uniqueId = (String) update.get(SC.UNIQUE_ID2);
		String messageId = (String) update.get(SC.MESSAGE_ID);
		String keywordSphereId = (String) update.get(SC.SPHERE_ID2);// ok.
		String queryId = (String) update.get(SC.QUERY_ID);
		Vector messageIdsToExclude = (Vector) update
				.get(SC.MESSAGE_IDS_TO_EXCLUDE);

		this.logger.info("query id got here: " + queryId);
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,session)) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, session);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
			dmpResponse.setStringValue(SC.MESSAGE_ID, messageId);
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.FIND_ASSETS_IN_SAME_CONCEPT_SET);
			Vector results = handler.getXmldb().findAssetsInSameConceptSet(
					keywordSphereId, uniqueId, messageIdsToExclude);
			this.logger.warn("results size!!! : " + results.size() + " : "
					+ keywordSphereId + " : " + uniqueId);
			dmpResponse.setVectorValue(SC.RESULTS, results);
			if (queryId != null) {
				dmpResponse.setStringValue(SC.QUERY_ID, queryId);
			}
			handler.sendFromQueue(dmpResponse);
			
		}
	}

}
