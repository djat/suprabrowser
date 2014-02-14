package ss.server.networking.protocol.getters;

import ss.client.networking.protocol.getters.CheckExistingUsernameCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class CheckExistingUsernameHandler extends AbstractGetterCommandHandler<CheckExistingUsernameCommand,Boolean> {
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public CheckExistingUsernameHandler( DialogsMainPeer peer) {
		super(CheckExistingUsernameCommand.class, peer );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Boolean evaluate(CheckExistingUsernameCommand command) throws CommandHandleException {
		String username = command.getStringArg( SC.USERNAME);
		return this.peer.getVerifyAuth().isUserExist( username);
	}

}
