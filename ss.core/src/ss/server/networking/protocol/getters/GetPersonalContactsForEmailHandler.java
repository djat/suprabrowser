package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.GetPersonalContactsForEmailCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;


public class GetPersonalContactsForEmailHandler  extends AbstractGetterCommandHandler<GetPersonalContactsForEmailCommand, Hashtable<String, String>> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetPersonalContactsForEmailHandler.class);
	
	/**
	 * @param peer
	 */
	public GetPersonalContactsForEmailHandler( DialogsMainPeer peer) {
		super(GetPersonalContactsForEmailCommand.class, peer);
	}
	
	
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String, String> evaluate(GetPersonalContactsForEmailCommand command) throws CommandHandleException {
		Hashtable session = command.getSessionArg();
		String realName = (String) session.get(SC.REAL_NAME);
		return this.peer.getXmldb().getPersonalContactsForEmail(this.peer.getVerifyAuth().getSystemName(realName));
	}

}
