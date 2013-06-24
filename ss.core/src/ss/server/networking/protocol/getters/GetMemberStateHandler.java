/**
 * 
 */
package ss.server.networking.protocol.getters;

import ss.client.networking.protocol.getters.GetMemberStateCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetMemberStateHandler extends AbstractGetterCommandHandler<GetMemberStateCommand, Boolean> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetMemberStateHandler(DialogsMainPeer peer) {
		super(GetMemberStateCommand.class, peer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Boolean evaluate(GetMemberStateCommand command) throws CommandHandleException {
		String userName = command.getStringArg(SessionConstants.USERNAME);
		return new Boolean(DialogsMainPeer.isContactOnline(this.peer.getVerifyAuth().getRealName(userName)));
	}
}
