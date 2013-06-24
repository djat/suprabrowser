package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class GetSubListHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public GetSubListHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_SUB_LIST;
	}

	public void handle(Hashtable update) {
		handleGetSubList(update);
	}

	public void handleGetSubList(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);		
		Document doc = (Document) update.get(SC.DOCUMENT);
		
		Hashtable finalSession = (Hashtable) session.clone();

		this.logger.info("DOc in getsublist; " + doc.asXML());
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getSphereUserHandlersFromSession(finalSession);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,
				finalSession)) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, finalSession);
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.SEND_SUB_LIST);
			dmpResponse.setDocumentValue(SC.DOC, doc);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "false");
			handler.sendFromQueue(dmpResponse);
		}
	}

}
