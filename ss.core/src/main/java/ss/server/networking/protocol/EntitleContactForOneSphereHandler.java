package ss.server.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.domain.service.IEntitleCotactForOneSphere;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.NameTranslation;

public class EntitleContactForOneSphereHandler implements ProtocolHandler {

	private static final String VALUE = "value";

	private static final String LOGIN = "login";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public EntitleContactForOneSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ENTITLE_CONTACT_FOR_ONE_SPHERE;
	}

	public void handle(Hashtable update) {
		handleEntitleContactForOneSphere(update);
	}

	public void handleEntitleContactForOneSphere(final Hashtable update) {
		Document contactDoc = (Document) update.get(SC.CONTACT_DOC);
		String existingMemberLogin = (String) update
				.get(SC.EXISTING_MEMBER_LOGIN);
		String existingMemberContact = (String) update
				.get(SC.EXISTING_MEMBER_CONTACT);		

		String loginBeingEntitled = contactDoc.getRootElement().element(LOGIN)
				.attributeValue(VALUE);
		String contactBeingEntitled = NameTranslation
				.createContactNameFromContactDoc(contactDoc);
		
		SupraSphereProvider.INSTANCE.get( this, IEntitleCotactForOneSphere.class ).entitleContactForOneSphere(existingMemberLogin, existingMemberContact,
			loginBeingEntitled, contactBeingEntitled);
	}


}
