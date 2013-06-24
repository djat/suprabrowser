/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllTagsCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class GetAllTagsCommandHandler extends AbstractGetterCommandHandler<GetAllTagsCommand, Vector<Document>> {

	
	public GetAllTagsCommandHandler(DialogsMainPeer peer) {
		super(GetAllTagsCommand.class, peer);
	}
	
	@Override
	protected Vector<Document> evaluate(GetAllTagsCommand command)
			throws CommandHandleException {
		return this.peer.getXmldb().getAllKeywords();
	}

}
