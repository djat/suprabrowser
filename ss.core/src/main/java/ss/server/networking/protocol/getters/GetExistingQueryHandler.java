package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetExistingQueryCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetExistingQueryHandler extends AbstractGetterCommandHandler<GetExistingQueryCommand, AbstractDocument> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(GetExistingQueryHandler.class);
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetExistingQueryHandler(DialogsMainPeer peer) {
		super(GetExistingQueryCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetExistingQueryCommand command) throws CommandHandleException {
		final Hashtable session = command.getSessionArg();
		final String queryText = command.getStringArg(SC.QUERY_TEXT);
		final String sphereId = (String) session.get(SC.SPHERE_ID);
		return (AbstractDocument)this.peer.getXmldb().getExistingQuery(sphereId, queryText);
	}

	

}
