package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.event.createevents.CreateEmailAction;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.TimeLogWriter;
import ss.common.VerifyAuth;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.email.EmailAliasesCreator;
import ss.domainmodel.SphereEmail;
import ss.server.db.XMLDB;
import ss.server.domain.service.IRegisterMember;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.WorkflowConfigurationSetup;
import ss.smtp.reciever.EmailProcessor;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

public class RegisterMemberHandler implements ProtocolHandler {


	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RegisterMemberHandler.class);

	private DialogsMainPeer peer;

	public RegisterMemberHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.REGISTER_MEMBER;
	}

	public void handle(Hashtable update) {
		handleRegisterMember(update);
	}

	public void handleRegisterMember(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String inviteUsername = (String) update.get(SC.INVITE_USERNAME);
		String inviteContact = (String) update.get(SC.INVITE_CONTACT);
		String sphereName = (String) update.get(SC.SPHERE_NAME2);// RC
		String sphereId = (String) update.get(SC.SPHERE_ID2);// RC
		String realName = (String) update.get(SC.REAL_NAME2);// RC
		String username = (String) update.get(SC.USERNAME);
		String inviteSphereType = (String) update.get(SC.INVITE_SPHERE_TYPE);
		Document contactDoc = (Document) update.get(SC.CONTACT_DOC);

		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);

		SupraSphereProvider.INSTANCE.get( this, IRegisterMember.class ).registerMember(update, session, inviteUsername, inviteContact,
			sphereName, sphereId, realName, username, inviteSphereType,
			contactDoc, supraSphere);
	}

	
}

/*
 * <?xml version="1.0" encoding="UTF-8"?> <sphere display_name="igor's emails
 * box" system_name="4179176102690876004" sphere_type="group"> <voting_model
 * type="absolute" desc="Absolute without qualification"> <specific> <member
 * contact_name="__NOBODY__"/> </specific> <tally number="0.0" value="0.0">
 * <member value="igor Novenkii" vote_moment="14:12:35 EET 10.01.2007"/>
 * </tally> </voting_model> <thread_type value="sphere"/> <type value="sphere"/>
 * <member contact_name="igor Novenkii"/>< body> <version value="3000"/>
 * <orig_body/> </body> <subject value="igor's emails box"/> <giver value="Danko
 * Sedin"/> <default_delivery value="normal"/> <default_type value="Email"/>
 * <thread_types> <message modify="own" enabled="false"/> <externalemail
 * modify="own" enabled="true"/> <bookmark modify="own" enabled="true"/> <terse
 * modify="own" enabled="true"/> <rss modify="own" enabled="true"/> <keywords
 * modify="own" enabled="true"/> <contact modify="own" enabled="false"/> <file
 * modify="own" enabled="true"/> <sphere modify="own" enabled="true"/>
 * </thread_types> <expiration value="All"/> <moment value="14:12:35 EET
 * 10.01.2007"/> <last_updated value="14:12:35 EET 10.01.2007"/> <message_id
 * value="706186509570711312"/> <thread_id value="706186509570711312"/>
 * <original_id value="706186509570711312"/> <interest total="40"> <accrual
 * giver="igor Novenkii"/> <accrual giver="igor Novenkii"/> <accrual giver="igor
 * Novenkii"/> <accrual giver="igor Novenkii"/> </interest> </sphere>
 * 
 */