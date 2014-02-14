/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetContactMessageIdsCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetContactMessageIdsHandler extends AbstractGetterCommandHandler<GetContactMessageIdsCommand, AbstractDocument> {

	public GetContactMessageIdsHandler(DialogsMainPeer peer) {
		super(GetContactMessageIdsCommand.class, peer);
	}
	
	@Override
	protected AbstractDocument evaluate(GetContactMessageIdsCommand command)
			throws CommandHandleException {
		return this.peer.getXmldb().getContactMessageIds(command.getStringArg(SessionConstants.CONTACT_NAME));
	}

}
