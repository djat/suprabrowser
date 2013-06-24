package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.GetEmailInfoCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetEmailInfoHandler extends AbstractGetterCommandHandler<GetEmailInfoCommand, Hashtable<String, String>> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetEmailInfoHandler(DialogsMainPeer peer) {
		super(GetEmailInfoCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String, String> evaluate(GetEmailInfoCommand command) throws CommandHandleException {
		String contact_name = command.getStringArg( SC.CONTACT_NAME);
		return this.peer.getEmailInfo( command.getSessionArg(), contact_name);
	}


}
