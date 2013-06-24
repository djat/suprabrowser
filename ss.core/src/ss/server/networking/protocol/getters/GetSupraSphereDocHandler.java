/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetSupraSphereDocCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class GetSupraSphereDocHandler extends AbstractGetterCommandHandler<GetSupraSphereDocCommand, AbstractDocument> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetSupraSphereDocHandler.class);
	
	public GetSupraSphereDocHandler(DialogsMainPeer peer) {
		super(GetSupraSphereDocCommand.class, peer);
	}

	@Override
	protected AbstractDocument evaluate(GetSupraSphereDocCommand command) throws CommandHandleException {
		try {
			Document returnDoc = this.peer.getXmlDbOld().getSupraSphereDocument();
			return (AbstractDocument) returnDoc;
		} catch (DocumentException ex) {
			logger.error("Can't get suprasphere document", ex);
			return null;
		}
	}

}
