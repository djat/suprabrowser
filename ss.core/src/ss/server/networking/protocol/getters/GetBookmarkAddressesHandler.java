/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;

import ss.client.networking.protocol.getters.GetBookmarkAddressesCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetBookmarkAddressesHandler extends AbstractGetterCommandHandler<GetBookmarkAddressesCommand, ArrayList<String>>{

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetBookmarkAddressesHandler(DialogsMainPeer peer) {
		super(GetBookmarkAddressesCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected ArrayList<String> evaluate(GetBookmarkAddressesCommand command) throws CommandHandleException {
		return this.peer.getXmldb().getBookmarksAddresses(command.getLookupSphere(), command.getFilter());
	}
}
