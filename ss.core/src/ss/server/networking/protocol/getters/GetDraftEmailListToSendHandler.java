/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;

import ss.client.networking.protocol.getters.GetDraftEmailListToSendCommand;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.smtp.defaultforwarding.EmailAddressesCreator;
import ss.smtp.defaultforwarding.ForcedForwardingInfo;

/**
 * @author zobo
 *
 */
public class GetDraftEmailListToSendHandler extends
		AbstractGetterCommandHandler<GetDraftEmailListToSendCommand, ArrayList<String>> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetDraftEmailListToSendHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetDraftEmailListToSendHandler( DialogsMainPeer peer ) {
		super(GetDraftEmailListToSendCommand.class, peer);
	}

	@Override
	protected ArrayList<String> evaluate(GetDraftEmailListToSendCommand command)
			throws CommandHandleException {
		final String sphereId = command.getSphereId();
		final ForcedForwardingInfo info = command.getForcedForwardingInfo();
		if ( sphereId == null ) {
			logger.error("sphereId is null");
			return null;
		}
		if ( info == null ) {
			logger.error("info is null");
			return null;
		}
		final SpherePossibleEmailsSet set = new SpherePossibleEmailsSet();
		if ( info.isAddContacts() ) {
			final String contactEmails = EmailAddressesCreator.getContactEmails( sphereId, this.peer.getXmldb() );
			set.addAddresses( contactEmails );
		}
		if ( info.isAddMembers() ) {
			final String memberEmails = EmailAddressesCreator.getMembersEmails( sphereId, this.peer.getXmldb() ); 
			set.addAddresses( memberEmails );
		}
		set.cleanUpAddresses();
		return (ArrayList<String>) set.getParsedEmailAddresses();
	}	
}
