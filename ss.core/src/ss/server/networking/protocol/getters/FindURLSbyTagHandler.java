package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.FindURLSbyTagCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class FindURLSbyTagHandler extends AbstractGetterCommandHandler<FindURLSbyTagCommand, Vector<String>> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(FindURLSbyTagHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public FindURLSbyTagHandler(DialogsMainPeer peer) {
		super(FindURLSbyTagCommand.class, peer);
	}


	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<String> evaluate(FindURLSbyTagCommand command) throws CommandHandleException {
		String uniqueId = command.getStringArg( SC.UNIQUE_ID2 );
		String sphereId = command.getStringArg( SC.SPHERE_ID2 );
		Vector<Document> resultDocs = this.peer.getXmldb().findURLSbyTag(sphereId, uniqueId);
		Vector<String> result = new Vector<String>();
		for (Document doc : resultDocs) {
			result.add(doc.getRootElement().element("address")
					.attributeValue("value"));
		}
		logger.warn("results size!!! : " + result.size() + " : "
					+ sphereId + " : " + uniqueId );
		return result;
	}

}
