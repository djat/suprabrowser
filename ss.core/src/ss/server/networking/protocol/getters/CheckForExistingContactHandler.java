package ss.server.networking.protocol.getters;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.CheckForExistingContactCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class CheckForExistingContactHandler extends AbstractGetterCommandHandler<CheckForExistingContactCommand, AbstractDocument>  {


	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public CheckForExistingContactHandler(DialogsMainPeer peer) {
		super(CheckForExistingContactCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(CheckForExistingContactCommand command) throws CommandHandleException {
		Document checkDoc = command.getDocumentArg( SC.DOCUMENT );
		return (AbstractDocument)this.peer.getXmldb().checkForExistingContact(checkDoc);
	}

}
