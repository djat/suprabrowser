/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.SSProtocolConstants;
import ss.server.domain.service.IEntitleCotactForOneSphere;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;

/**
 *
 */
public class EntitleCotactForOneSphere extends AbstractSsDocFeature implements IEntitleCotactForOneSphere {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EntitleCotactForOneSphere.class);
	
	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.ssdoc.IEntitleCotactForOneSphere#entitleContactForOneSphere(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void entitleContactForOneSphere(String existingMemberLogin,
			String existingMemberContact, String loginBeingEntitled,
			String contactBeingEntitled) {
		try {
			Document returnDoc = getUtils()
					.entitleContactForMemberSphere(loginBeingEntitled,
							contactBeingEntitled, existingMemberContact, null);

			returnDoc = getUtils().entitleContactForMemberSphere(
					existingMemberLogin, existingMemberContact,
					contactBeingEntitled, null);

			logger.warn("ENTITLED THIS WAY: " + loginBeingEntitled + " : "
					+ contactBeingEntitled + " : " + existingMemberContact
					+ " : " + existingMemberLogin + " : "
					+ existingMemberContact + " : " + contactBeingEntitled);

			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT);
				dmpResponse.setDocumentValue(SC.SUPRA_SPHERE_DOCUMENT, (Document) returnDoc
								.clone());
				handler.sendFromQueue(dmpResponse);

			}
		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}
}
