package ss.server.networking.protocol.getters;

import java.util.Hashtable;

import ss.client.networking.protocol.getters.GetPersonalContactsForSphereCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;


public class GetPersonalContactsForSphereHandler extends AbstractGetterCommandHandler<GetPersonalContactsForSphereCommand, Hashtable<String, String>> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetPersonalContactsForSphereHandler.class);
	
	/**
	 * @param peer
	 */
	public GetPersonalContactsForSphereHandler( DialogsMainPeer peer) {
		super(GetPersonalContactsForSphereCommand.class, peer);
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Hashtable<String,String> evaluate(GetPersonalContactsForSphereCommand command) throws CommandHandleException {	
		Hashtable session = command.getSessionArg();
		String realName = (String) session.get(SC.REAL_NAME);
		return this.peer.getXmldb()
					.getPersonalContactsForSphere(
							this.peer.getVerifyAuth().getSystemName(realName));
	}

}
