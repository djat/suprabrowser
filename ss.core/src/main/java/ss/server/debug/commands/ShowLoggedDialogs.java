/**
 * 
 */
package ss.server.debug.commands;

import java.util.List;

import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;

/**
 *
 */
public class ShowLoggedDialogs implements IRemoteCommand {

	/* (non-Javadoc)
	 * @see ss.server.debug.IRemoteCommand#evaluate(ss.server.debug.RemoteCommandContext)
	 */
	public String evaluate(RemoteCommandContext context) throws Exception {
		List<DialogsMainPeer> handlers = ListUtils.toList( DialogsMainPeerManager.INSTANCE.getHandlers() );
		StringBuilder sb = new StringBuilder();
		sb.append( "Logged clients count: " );
		sb.append( handlers.size() );
		sb.append( StringUtils.getLineSeparator() );
		for ( DialogsMainPeer peer : handlers ) {
			sb.append( peer.getName() );
			sb.append( StringUtils.getLineSeparator() );
		}
		return sb.toString();
	}

	
}
