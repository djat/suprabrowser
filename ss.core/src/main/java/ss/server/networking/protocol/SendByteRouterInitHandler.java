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

public class SendByteRouterInitHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendByteRouterInitHandler.class);

	public SendByteRouterInitHandler() {

	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_BYTE_ROUTER_INIT;
	}

	public void handle(Hashtable update) {
		handleSendByteRouterInit(update);
	}

	public void handleSendByteRouterInit(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document doc = (Document) update.get(SC.DOC);
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getSphereUserHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,
				session)) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, session);
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.SEND_BYTE_ROUTER_INIT);
			dmpResponse.setDocumentValue(SC.DOC, doc);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "false");
			handler.sendFromQueue(dmpResponse);
		}
	}

}
