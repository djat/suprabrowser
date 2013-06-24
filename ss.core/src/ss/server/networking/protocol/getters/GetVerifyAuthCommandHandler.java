/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.apache.log4j.Logger;

import ss.client.networking.protocol.getters.GetVerifyAuthCommand;
import ss.common.VerifyAuth;
import ss.framework.networking2.CommandHandleException;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetVerifyAuthCommandHandler extends AbstractGetterCommandHandler<GetVerifyAuthCommand, VerifyAuth> {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(GetVerifyAuthCommandHandler.class);
	
	public GetVerifyAuthCommandHandler(final DialogsMainPeer peer) {
		super(GetVerifyAuthCommand.class, peer);
	}
	
	
	@Override
	protected VerifyAuth evaluate(GetVerifyAuthCommand command)
			throws CommandHandleException {
		String loginName = command.getStringArg(SessionConstants.USERNAME);
		for(DialogsMainPeer peer : DialogsMainPeerManager.INSTANCE.getHandlers()) {
			if(peer.getUserLogin().equals(loginName)) {
				return peer.getVerifyAuth();
			}
		}
		return null;
	}

}
