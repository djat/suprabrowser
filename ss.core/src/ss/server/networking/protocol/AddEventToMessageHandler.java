package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class AddEventToMessageHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	public AddEventToMessageHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}
	
	public String getProtocol() {
		return SSProtocolConstants.ADD_EVENT_TO_MESSAGE;
	}

	public void handle(Hashtable update) {
		handleAddEventToMessage(update);
	}

	public void handleAddEventToMessage(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);		
	
		Element event = (Element) update.get(SC.ELEMENT);
	
		String messageId = (String) update.get(SC.MESSAGE_ID);
	
		this.peer.getXmldb().addEventToMessage(session, messageId, event);
	}

}
