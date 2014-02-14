package ss.server.networking.protocol;

import java.util.Hashtable;

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
public class RebootrpmHandler implements ProtocolHandler {

	/**
	 * @deprecated
	 * @param peer
	 */
	public RebootrpmHandler() {
		
	}

	public String getProtocol() {
		return SSProtocolConstants.REBOOTRPM;
	}

	public void handle(Hashtable update) {
		handleRebootrpm(update);
	}

	public void handleRebootrpm(Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		FilteredHandlers filteredHandlers = FilteredHandlers.getAllNonSphereUserHandlersFromSession(session);
		for (DialogsMainPeer handler : filteredHandlers) {
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.REBOOT);
			handler.sendFromQueue(dmpResponse);
		}
	}

}
