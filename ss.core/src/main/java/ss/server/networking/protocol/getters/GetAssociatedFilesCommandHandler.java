/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAssociatedFilesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetAssociatedFilesCommandHandler extends
		AbstractGetterCommandHandler<GetAssociatedFilesCommand, Vector<Document>> {

	
	public GetAssociatedFilesCommandHandler(DialogsMainPeer peer) {
		super(GetAssociatedFilesCommand.class, peer);
	}
	
	@Override
	protected Vector<Document> evaluate(GetAssociatedFilesCommand command)
			throws CommandHandleException {
		String sphereId = command.getStringArg(SessionConstants.SPHERE_ID2);
		return this.peer.getXmldb().getFilesFromSphere(sphereId);
	}

}
