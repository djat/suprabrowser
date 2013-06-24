/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.IsAdminCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class IsAdminCommandHandler extends AbstractGetterCommandHandler<IsAdminCommand, Boolean> {

	public IsAdminCommandHandler(final DialogsMainPeer peer) {
		super(IsAdminCommand.class, peer);
	}
	
	@Override
	protected Boolean evaluate(IsAdminCommand command)
			throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String realName = (String)session.get(SessionConstants.REAL_NAME);
		String loginName = (String)session.get(SessionConstants.USERNAME);
		return this.peer.getVerifyAuth().isAdmin(realName, loginName);
	}

}
