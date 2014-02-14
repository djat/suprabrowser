package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class GetInfoForHanlder implements ProtocolHandler {

	public GetInfoForHanlder() {
		
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_INFO_FOR;
	}

	public void handle(Hashtable update) {
		handleGetInfoFor(update);
	}

	public void handleGetInfoFor(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document doc = (Document) update.get(SC.DOC);

		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,
				session)) {
			Hashtable finalSession = (Hashtable) session.clone();
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, finalSession);
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.GET_INFO_FOR);
			dmpResponse.setDocumentValue(SC.DOC, doc);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "false");
			handler.sendFromQueue(dmpResponse);
		}
	}
}
