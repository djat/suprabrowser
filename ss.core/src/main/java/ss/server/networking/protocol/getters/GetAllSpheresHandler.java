package ss.server.networking.protocol.getters;

import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllSpheresCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

public class GetAllSpheresHandler extends AbstractGetterCommandHandler<GetAllSpheresCommand,Vector<Document> > {
		
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetAllSpheresHandler.class);

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetAllSpheresHandler(DialogsMainPeer peer) {
		super(GetAllSpheresCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Document> evaluate(GetAllSpheresCommand command) throws CommandHandleException {
		return this.peer.getXmldb().getAllSpheres();
	}
}
