package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;

public class ReplaceDocHandler implements ProtocolHandler {

	private static final String ORIGINAL_ID = "original_id";

	private static final String MESSAGE_ID = "message_id";

	private static final String THREAD_ID = "thread_id";

	private static final String EX_MESSAGE = "ex_message";

	private static final String EX_SYSTEM = "ex_system";

	public static final String KEYWORDS = "keywords";

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String LOCATIONS = "locations";

	private DialogsMainPeer peer;

	private static final Logger logger = SSLogger.getLogger(ReplaceDocHandler.class);

	public ReplaceDocHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.REPLACE_DOC;
	}

	public void handle(Hashtable update) {
		handleReplaceDoc(update);
	}

	@SuppressWarnings("unchecked")
	public void handleReplaceDoc(final Hashtable update) {
		logger.warn("Got replace doc");
		final Document doc = (org.dom4j.Document) update.get(SC.DOCUMENT);
		final Hashtable finalSession = (Hashtable) update.get(SC.SESSION);
		
		final String sphereId = (String) finalSession.get(SC.SPHERE_ID);
		if (logger.isDebugEnabled()) {
			logger.debug("Update doc for " + sphereId );
		}
		final String multiLoc = (String) finalSession.get(SC.MULTI_LOC_SPHERE);
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setMapValue(SC.SESSION, (Hashtable) finalSession.clone());
		if (logger.isDebugEnabled()) {
			logger.debug("REPLACE THIS DOCUMENT: "+doc.asXML());
		}
		Vector locations = null;
		if (doc.getRootElement().element(LOCATIONS) != null) {
			locations = new Vector(doc.getRootElement().element(LOCATIONS)
					.elements());
		}
		boolean isKeywords = false;
		if ( doc != null  ) {
			final Statement statement = Statement.wrap( doc );
			isKeywords = statement.isKeywords(); 
			if ( statement.isContact() ) {
				// Get original contact document 
				final Document originalDocument = this.peer.getXmldb().getSpecificMessage(statement.getMessageId() );
				if ( originalDocument != null ) {
					ContactStatement originalContact = ContactStatement.wrap( originalDocument );
					// Recheck that we received contact 
					if ( originalContact.isContact() ) {
						locations = new Vector( this.peer.getXmldb().findAllLocationsByMessageId( originalContact ) );						
					}
				}
			}
		}

		if (locations == null) {
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);
			String data_sphere = this.peer.getXmldb().getUtils()
					.getInheritedName(sphereId);
			if (data_sphere == null) {
				data_sphere = sphereId;
			}
			Document res = this.peer.getXmldb().replaceDoc(doc, data_sphere);
			dmpResponse.setStringValue(SC.SPHERE, sphereId);

			if (res.getRootElement().element(CURRENT_SPHERE) != null) {
				res.getRootElement().element(CURRENT_SPHERE).detach();
			}
			res.getRootElement().addElement(CURRENT_SPHERE).addAttribute(VALUE,
					sphereId);
			dmpResponse.setDocumentValue(SC.DOCUMENT, res);
			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
				String login = handler.get(HandlerKey.USERNAME);				
				boolean sphereEnabled = handler.getVerifyAuth().isSphereEnabledForMember(sphereId,login);
				boolean multilockEnabled = multiLoc != null ? handler.getVerifyAuth().isSphereEnabledForMember(multiLoc,login) : false;
				if (multiLoc != null) {
					logger.warn("whenupdate: " + multiLoc + " : "
							+ sphereId);
					dmpResponse.setStringValue(SC.SPHERE, multiLoc);
				} else {
					multilockEnabled = sphereEnabled;
				}
				if (isKeywords || sphereEnabled	|| multilockEnabled ) {
					handler.sendFromQueue(dmpResponse);
				}
			}
			/*
			 * } catch (NullPointerException npe) { //System.out.println("NULL
			 * IN DOCUMENT"); }
			 */
		} else {

			// logger.info("there are freaking
			// locations..."+doc.asXML());

			for (int i = 0; i < locations.size(); i++) {

				Element loc = (Element) locations.get(i);
				String locSphereId = loc.attributeValue(EX_SYSTEM);
				String locMessageId = loc.attributeValue(EX_MESSAGE);

				logger.info("Sending to this loc sphere id: "
						+ locSphereId);

				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_DOCUMENT);

				String data_sphere = this.peer.getXmldb().getUtils().getInheritedName(
						locSphereId);

				logger.info("REPLACING DOC: " + data_sphere);
				if (data_sphere == null) {
					data_sphere = locSphereId;
				}
				// logger.info("before
				// replacing...doc."+doc.asXML());

				Document otherLocDoc = this.peer.getXmldb().getSpecificID(
						locSphereId, locMessageId);

				Document toReplaceDoc = (Document) doc.clone();

				try {
					toReplaceDoc.getRootElement().element(THREAD_ID)
							.addAttribute(
									VALUE,
									otherLocDoc.getRootElement().element(
											THREAD_ID).attributeValue(VALUE));

					toReplaceDoc.getRootElement().element(MESSAGE_ID)
							.addAttribute(
									VALUE,
									otherLocDoc.getRootElement().element(
											MESSAGE_ID).attributeValue(VALUE));
					toReplaceDoc.getRootElement().element(ORIGINAL_ID)
							.addAttribute(
									VALUE,
									otherLocDoc.getRootElement().element(
											ORIGINAL_ID).attributeValue(VALUE));

					Document res = this.peer.getXmldb().replaceDoc(
							toReplaceDoc, locSphereId);

					dmpResponse.setStringValue(SC.SPHERE, locSphereId);
					if (res.getRootElement().element(CURRENT_SPHERE) != null) {
						res.getRootElement().element(CURRENT_SPHERE).detach();
					}

					res.getRootElement().addElement(CURRENT_SPHERE)
							.addAttribute(VALUE, locSphereId);
					dmpResponse.setDocumentValue(SC.DOCUMENT, res);

					if ( isKeywords ) {
						for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
							handler.sendFromQueue(dmpResponse);
						}
					}
					else {
						for (DialogsMainPeer handler : DmpFilter.filter(locSphereId) ) {
							handler.sendFromQueue(dmpResponse);
						}
					}
				} catch (Exception e) {
					logger.error( "Can't send replaced document update", e);
				}
			}

		}
	}

}
