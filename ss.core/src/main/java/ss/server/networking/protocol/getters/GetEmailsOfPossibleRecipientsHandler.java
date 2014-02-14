/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;

import ss.client.networking.protocol.getters.GetEmailsOfPossibleRecipientsCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetEmailsOfPossibleRecipientsHandler extends
		AbstractGetterCommandHandler<GetEmailsOfPossibleRecipientsCommand, ArrayList<String>> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetEmailsOfPossibleRecipientsHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetEmailsOfPossibleRecipientsHandler(DialogsMainPeer peer) {
		super(GetEmailsOfPossibleRecipientsCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected ArrayList<String> evaluate(GetEmailsOfPossibleRecipientsCommand command) throws CommandHandleException {
		final String sphereId = command.getLookupSphere();
		try {
			final ArrayList<String> emails = this.peer.getEmailsOfPossibleRecipients(sphereId);
			return emails;
		} catch (Throwable ex) {
			logger.error("Error getting emails for possible recipients");
			return new ArrayList<String>();
		}
	}
}
