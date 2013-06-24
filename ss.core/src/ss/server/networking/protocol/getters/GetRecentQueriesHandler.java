package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Element;

import ss.client.networking.protocol.getters.GetRecentQueriesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetRecentQueriesHandler extends AbstractGetterCommandHandler<GetRecentQueriesCommand, Vector<Element>> {
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetRecentQueriesHandler.class);
	
	/**
	 * @param peer
	 */
	public GetRecentQueriesHandler( DialogsMainPeer peer) {
		super(GetRecentQueriesCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Element> evaluate(GetRecentQueriesCommand command) throws CommandHandleException {
		logger.info("starting getrecentonserver");
		final Hashtable finalSession = command.getSessionArg();
		String homeSphereId = command.getStringArg(SC.HOME_SPHERE_ID);
		String homeMessageId = command.getStringArg(SC.HOME_MESSAGE_ID);
		return this.peer.getXmldb().getRecentQueriesFor(
				finalSession, homeSphereId, homeMessageId);
	}

}
