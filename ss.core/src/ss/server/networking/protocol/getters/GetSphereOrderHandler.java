package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetSphereOrderCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

public class GetSphereOrderHandler extends AbstractGetterCommandHandler<GetSphereOrderCommand, AbstractDocument> {
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetSphereOrderHandler.class);
	
	/**
	 * @param peer
	 */
	public GetSphereOrderHandler( DialogsMainPeer peer) {
		super(GetSphereOrderCommand.class, peer);
	}
	
	
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetSphereOrderCommand command) throws CommandHandleException {
		logger.info("getsphereorder in dialogsmainper");
		final Hashtable session = command.getSessionArg();
		return (AbstractDocument) this.peer.getXmldb().getSphereOrder(session);
	}

}
