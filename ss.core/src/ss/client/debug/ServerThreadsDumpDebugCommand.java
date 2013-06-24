/**
 * 
 */
package ss.client.debug;

import ss.common.debug.ThreadsDumpCommand;

/**
 *
 */
public class ServerThreadsDumpDebugCommand extends AbstractRemoteDebugCommand {

	/**
	 * @param displayName
	 */
	protected ServerThreadsDumpDebugCommand() {
		super( "server-threads-dump", "Print dump of server threas." );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		beginExecute( new ThreadsDumpCommand() );
	}

}
