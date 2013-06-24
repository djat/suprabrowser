package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetRecentBookmarksCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetRecentBookmarksHandler extends AbstractGetterCommandHandler<GetRecentBookmarksCommand, Vector<Document>> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetRecentBookmarksHandler.class);
	
	/**
	 * @param peer
	 */
	public GetRecentBookmarksHandler( DialogsMainPeer peer) {
		super(GetRecentBookmarksCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Document> evaluate(GetRecentBookmarksCommand command) throws CommandHandleException {
		final Hashtable finalSession = command.getSessionArg();
		String homeSphereId = command.getStringArg(SC.HOME_SPHERE_ID);
		String homeMessageId = command.getStringArg(SC.HOME_MESSAGE_ID);		
		return this.peer.getXmldb().getRecentBookmarksFor( finalSession, homeSphereId, homeMessageId);
	}

}
