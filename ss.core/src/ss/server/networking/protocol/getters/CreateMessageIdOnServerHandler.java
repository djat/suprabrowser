package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.CreateMessageIdOnServerCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.VariousUtils;

public class CreateMessageIdOnServerHandler extends AbstractGetterCommandHandler<CreateMessageIdOnServerCommand, Hashtable<String,String>> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public CreateMessageIdOnServerHandler(DialogsMainPeer peer) {
		super(CreateMessageIdOnServerCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String, String> evaluate(CreateMessageIdOnServerCommand command) throws CommandHandleException {
		String moment = DialogsMainPeer.getCurrentMoment();
		String messageId = VariousUtils.createMessageId();
		Hashtable<String,String> messageIdServer = new Hashtable<String,String>();
		messageIdServer.put(SC.MESSAGE_ID, messageId);
		messageIdServer.put(SC.MOMENT, moment);
		return messageIdServer;
	}
}
