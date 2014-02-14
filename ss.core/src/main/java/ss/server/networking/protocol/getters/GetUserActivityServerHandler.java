/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetUserActivityCommand;
import ss.common.XmlDocumentUtils;
import ss.domainmodel.UserActivity;
import ss.framework.networking2.CommandHandleException;
import ss.server.db.dataaccesscomponents.UserActivityDac;
import ss.server.networking.DialogsMainPeer;

/**
 * @author d!ma
 *
 */
public class GetUserActivityServerHandler extends AbstractGetterCommandHandler<GetUserActivityCommand, AbstractDocument> {
	
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(GetUserActivityServerHandler.class);
	
	/**
	 * @param peer
	 */
	public GetUserActivityServerHandler( DialogsMainPeer peer) {
		super(GetUserActivityCommand.class, peer);
	}
	
	
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetUserActivityCommand command) throws CommandHandleException {
		Document document = UserActivityDac.INSTANCE.getUserActivityDocument( command.getSphereId(), command.getLogin() );
		if ( document == null ) {
			document = new UserActivity().getDocumentCopy();
		}
		if ( logger.isDebugEnabled() ) {
			logger.debug( "return document " + XmlDocumentUtils.toPrettyString( document ) );
		}		
		return (AbstractDocument) document;		
	}
	
}