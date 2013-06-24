package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class NotifySystemTrayHandler implements ProtocolHandler {

	public NotifySystemTrayHandler() {
	}

	public String getProtocol() {
		return SSProtocolConstants.NOTIFY_SYSTEM_TRAY;
	}

	public void handle(Hashtable update) {
		handleNotifySystemTray(update);
	}

	public void handleNotifySystemTray(final Hashtable update) {
		final Hashtable finalSession = (Hashtable) update.get(SC.SESSION);
		Document notifyDoc = (Document) update.get(SC.DOCUMENT);
		Vector memberList = (Vector) update.get(SC.MEMBER_LIST);
		
		String supraSphere = (String) finalSession.get(SC.SUPRA_SPHERE);
		for (int j = 0; j < memberList.size(); j++) {

			String member = (String) memberList.get(j);
			FilteredHandlers filteredHandlers = FilteredHandlers
					.getSphereUserHandlers(supraSphere, member);
			for (DialogsMainPeer handler : filteredHandlers) {
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.NOTIFY_SYSTEM_TRAY);
				dmpResponse.setMapValue(SC.SESSION, finalSession);
				dmpResponse.setDocumentValue(SC.DOCUMENT, notifyDoc);
				handler.sendFromQueue(dmpResponse);

			}
		}
	}

}
