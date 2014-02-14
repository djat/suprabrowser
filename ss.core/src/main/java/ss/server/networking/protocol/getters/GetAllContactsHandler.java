package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllContactsCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetAllContactsHandler extends AbstractGetterCommandHandler<GetAllContactsCommand, Vector<Document>> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetAllContactsHandler(DialogsMainPeer peer) {
		super(GetAllContactsCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Document> evaluate(GetAllContactsCommand command) throws CommandHandleException {
		String sphereId = command.getStringArg(SC.SPHERE_ID2);
		return this.peer.getXmldb().getAllContactsForMembers(sphereId);
	}
}
