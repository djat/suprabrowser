package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetMyContactCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetMyContactHandler extends AbstractGetterCommandHandler<GetMyContactCommand, AbstractDocument> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetMyContactHandler.class);
	/**
	 * @param peer
	 */
	public GetMyContactHandler( DialogsMainPeer peer) {
		super(GetMyContactCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetMyContactCommand command) throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String realName = (String) session.get(SC.REAL_NAME);
		return (AbstractDocument) this.peer.getXmldb().getMyContact(realName, this.peer.getVerifyAuth().getSystemName(realName));
	}

}
