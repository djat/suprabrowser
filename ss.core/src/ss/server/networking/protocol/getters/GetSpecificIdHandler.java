package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetSpecificIdCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetSpecificIdHandler extends AbstractGetterCommandHandler<GetSpecificIdCommand, AbstractDocument> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(GetSpecificIdHandler.class);
	
	/**
	 * @param peer
	 */
	public GetSpecificIdHandler( DialogsMainPeer peer) {
		super(GetSpecificIdCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetSpecificIdCommand command) throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String messageId = command.getStringArg(SC.MESSAGE_ID);
		String sphereId = (String) session.get(SC.SPHERE_ID);
		return (AbstractDocument) this.peer.getXmldb().getSpecificID( sphereId, messageId );
	}
}
