/**
 * 
 */
package ss.server.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.protocol.actions.TagActionCommand;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.processing.keywords.ServerTagActionProcessor;

/**
 * @author roman
 *
 */
public class TagActionCommandHandler extends AbstractActionHandler<TagActionCommand> {


	public TagActionCommandHandler(final DialogsMainPeer peer) {
		super(TagActionCommand.class, peer);
	}
	
	@Override
	protected void execute(final TagActionCommand action) {
		final String keywordText = action.getKeywordText().trim();
		final String sphereId = action.getSphereId();
		final Document parentDocument = action.getDocument();
		final Hashtable sendSession = action.getSessionArg();
		final ServerTagActionProcessor processor = new ServerTagActionProcessor(this.peer, sendSession, parentDocument, sphereId, keywordText);
		processor.doTagAction();
	}
}
