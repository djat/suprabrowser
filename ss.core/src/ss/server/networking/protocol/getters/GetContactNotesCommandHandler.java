/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetContactNotesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class GetContactNotesCommandHandler extends AbstractGetterCommandHandler<GetContactNotesCommand, Vector<Document>> {

	public GetContactNotesCommandHandler(DialogsMainPeer peer) {
		super(GetContactNotesCommand.class, peer);
	}
	
	@Override
	protected Vector<Document> evaluate(GetContactNotesCommand command)
			throws CommandHandleException {
		String contactName = command.getStringArg(SessionConstants.CONTACT_NAME);
		Vector<Document> docs = this.peer.getXmldb().getAllNotesAboutContact(this.peer.getVerifyAuth(), contactName);
		return docs;
	}
}
