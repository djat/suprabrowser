package ss.server.networking.protocol.getters;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetKeywordsWithUniqueCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetKeywordsWithUniqueHandler extends AbstractGetterCommandHandler<GetKeywordsWithUniqueCommand, AbstractDocument>  {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetKeywordsWithUniqueHandler.class);
	/**
	 * @param peer
	 */
	public GetKeywordsWithUniqueHandler( DialogsMainPeer peer) {
		super(GetKeywordsWithUniqueCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetKeywordsWithUniqueCommand command) throws CommandHandleException {
		String uniqueId = command.getStringArg(SC.UNIQUE_ID2);
		if ( uniqueId == null ) {
			return null;
		}
		String currentSphere = command.getStringArg(SC.CURRENT_SPHERE);
		AbstractDocument doc = (AbstractDocument)this.peer.getXmldb().getKeywordsWithUnique(currentSphere, uniqueId);
		return doc;		
	}

	

}
