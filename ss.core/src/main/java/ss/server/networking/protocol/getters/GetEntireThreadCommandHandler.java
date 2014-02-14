/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetEntireThreadCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetEntireThreadCommandHandler extends AbstractGetterCommandHandler<GetEntireThreadCommand, Vector<Document>> {

	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetEntireThreadCommandHandler(DialogsMainPeer peer) {
		super(GetEntireThreadCommand.class, peer);
	}

	@Override
	protected Vector<Document> evaluate(GetEntireThreadCommand command)
			throws CommandHandleException {
		String sphereId = (String)command.getSessionArg().get(SessionConstants.SPHERE_ID2);
		String messageId = command.getStringArg(SessionConstants.MESSAGE_ID);
		
		Document[] docs = this.peer.getXmldb().getEntireThread(sphereId, messageId);
		Vector<Document> docVector = new Vector<Document>();
		for(Document doc : docs) {
			docVector.add(doc);
		}
		return docVector;
	}
}
