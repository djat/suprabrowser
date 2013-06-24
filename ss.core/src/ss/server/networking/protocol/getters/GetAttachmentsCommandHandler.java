/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAttachmentsCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetAttachmentsCommandHandler extends AbstractGetterCommandHandler<GetAttachmentsCommand, Vector<Document>> {

	public GetAttachmentsCommandHandler(DialogsMainPeer peer) {
		super(GetAttachmentsCommand.class, peer);
	}
	
	@Override
	protected Vector<Document> evaluate(GetAttachmentsCommand command)
			throws CommandHandleException {
		String sphereId = command.getStringArg(SessionConstants.SPHERE_ID2);
		String messageId = command.getStringArg(SessionConstants.MESSAGE_ID);
		return this.peer.getXmldb().getAttachments(sphereId, messageId);
	}

}
