/**
 * 
 */
package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.smtp.defaultforwarding.EmailForwarder;
import ss.smtp.defaultforwarding.ForwardingElement;

/**
 * @author roman
 *
 */
public class PublishForwardedMessagesHandler implements ProtocolHandler {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PublishForwardedMessagesHandler.class);
	
	private DialogsMainPeer peer;

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	public static final String CONTACT = "contact";

	public static final String KEYWORDS = "keywords";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String LAST_UPDATED = "last_updated";

	private static final String MOMENT = "moment";
	
	public PublishForwardedMessagesHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public void publishMessage(Hashtable update) {

		
		final Hashtable session = (Hashtable) update.get(SC.SESSION);			
		Document doc = (Document) update.get(SC.DOCUMENT);
		String remoteUsername = (String) update.get(SC.REMOTE_USERNAME);
		String repress = (String) update.get(SC.REPRESS_NOTIFICATION);
		String externalConnection = (String) update.get(SC.EXTERNAL_CONNECTION);
		
		String sphereId = (String) session.get(SC.SPHERE_ID);		
		String multiLoc = (String) session.get(SC.MULTI_LOC_SPHERE);
		String realName = (String) session.get(SC.REAL_NAME);
	
		try {

			boolean repressNotification = false;

			try {
				if (repress.equals("true")) {
					repressNotification = true;
				}

			} catch (NullPointerException npe) {

			}

			boolean external = false;

			try {
				if (externalConnection != null) {
					if (externalConnection.equals("true")) {
						external = true;
					}
				}

			} catch (NullPointerException npe) {

			}
			
			Element email = doc.getRootElement();

			String moment = DialogsMainPeer.getCurrentMoment();
		
			if (email.element(MOMENT) == null) {
				email.addElement(MOMENT).addAttribute(VALUE, moment);

				email.addElement(LAST_UPDATED).addAttribute(VALUE, moment);
			}

			String type = doc.getRootElement().element(TYPE).attributeValue(
					VALUE);

			final DmpResponse dmpResponse = new DmpResponse();
			if(email.element(CURRENT_SPHERE) == null) {
				doc.getRootElement().addElement(CURRENT_SPHERE).addAttribute(VALUE,
						sphereId);
			}

			if (!type.equals(KEYWORDS)) {
			
				this.peer.getXmldb().insertDoc(doc, sphereId);

				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE);
				dmpResponse.setDocumentValue(SC.DOCUMENT, doc);
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				this.peer.sendFromQueue(dmpResponse);

				if (external) {
					dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "true");
					dmpResponse.setStringValue(SC.REMOTE_USERNAME, remoteUsername);

				} else {
					dmpResponse.setStringValue(SC.EXTERNAL_CONNECTION, "false");
				}
				FilteredHandlers filteredHandlers = FilteredHandlers
						.getAllNonUserHandlersFromSession(session);
				for (DialogsMainPeer handler : filteredHandlers) {
					String contact = handler.get(HandlerKey.USERNAME);
					boolean sphereEnabled = handler.getVerifyAuth().isSphereEnabledForMember(sphereId, contact);

					if (multiLoc == null) {

						if (sphereEnabled
								&& !repressNotification) {
							handler.sendFromQueue(dmpResponse);
						}
					} else {
						
						boolean multilocEnabled = handler.getVerifyAuth().isSphereEnabledForMember(multiLoc, contact);
						if (sphereEnabled) {

							dmpResponse.setStringValue(SC.SPHERE, sphereId);
							handler.sendFromQueue(dmpResponse);

						} else if (multilocEnabled) {

							dmpResponse.setStringValue(SC.SPHERE, multiLoc);
							handler.sendFromQueue(dmpResponse);
						}
					}
				}

			} else {
				this.peer.getXmldb().insertDoc(doc, sphereId);
			}
			
			final Document finalDoc = doc;
			final Hashtable finalSession = session;

			processEmailForwardingRules(finalSession, finalDoc);

		} catch (Exception ex) {
			logger.error("Problem publishing for : " + realName,ex);
		}
	}
	
	/**
	 * @param finalSession
	 * @param finalDoc
	 */
	private void processEmailForwardingRules(Hashtable session, Document doc) {
		EmailForwarder.INSTANCE.send(
				new ForwardingElement(doc, (String) session.get(SC.SPHERE_ID)));
	}

	/* (non-Javadoc)
	 * @see ss.common.ProtocolHandler#getProtocol()
	 */
	public String getProtocol() {
		return SSProtocolConstants.PUBLISH_FORWARDED;
	}

	/* (non-Javadoc)
	 * @see ss.common.ProtocolHandler#handle(java.util.Hashtable)
	 */
	public void handle(Hashtable update) {
		publishMessage(update);
	}
}
