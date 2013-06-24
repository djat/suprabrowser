package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.Expression;
import ss.server.networking.util.Filter;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;

public class SendPopupNotificationHandler implements ProtocolHandler {

	private Logger logger = SSLogger.getLogger(this.getClass());

	public SendPopupNotificationHandler() {
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_POPUP_NOTIFICATION;
	}

	public void handle(Hashtable update) {
		handleSendPopupNotification(update);
	}

	public void handleSendPopupNotification(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document notifyDoc = (Document) update.get(SC.DOCUMENT);
		Vector memberList = (Vector) update.get(SC.MEMBER_LIST);

		this.logger.warn("Got send popup: " + memberList.size());
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getAllSphereHandlersFromSession(session);

		// For each member (person)
		for (int j = 0; j < memberList.size(); j++) {
			Document member = (Document) memberList.get(j);

			String login = member.getRootElement().element("login_name")
					.attributeValue("value");

			this.logger.warn("Send to this member: " + member.asXML());

			// For each Client (aka handler or ClientHandler)
			for (DialogsMainPeer handler : filteredHandlers) {

				// if it matche
				String sSession = (String) handler.getSession().get(SC.SESSION);

				Filter filter = createUsernameSessionFilter(login, sSession);
				if (filter.filter(handler)) {
					final DmpResponse dmpResponse = new DmpResponse();
					dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.SEND_POPUP_NOTIFICATION);
					dmpResponse.setMapValue(SC.SESSION, session);
					dmpResponse.setDocumentValue(SC.DOCUMENT, notifyDoc);
					handler.sendFromQueue(dmpResponse);
				}
			}
		}
	}

	private static Filter createUsernameSessionFilter(String login,
			String sSession) {
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.USERNAME, login));
		filter.add(new Expression(HandlerKey.SESSION, sSession));
		return filter;
	}

}
