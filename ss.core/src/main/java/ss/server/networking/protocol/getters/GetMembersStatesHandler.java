package ss.server.networking.protocol.getters;


import java.util.Hashtable;

import ss.client.networking.protocol.getters.GetMembersStatesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

public class GetMembersStatesHandler extends AbstractGetterCommandHandler<GetMembersStatesCommand, Hashtable<String,Boolean>> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetMembersStatesHandler(DialogsMainPeer peer) {
		super(GetMembersStatesCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected Hashtable<String, Boolean> evaluate(GetMembersStatesCommand command) throws CommandHandleException {
		Hashtable<String, Boolean> ret = new Hashtable<String, Boolean>();

		for( String contactName : command.getContacts() ) {
			ret.put( contactName, DialogsMainPeer.isContactOnline( contactName ) );
		}
		return ret;
	}

}
