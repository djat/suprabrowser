/**
 * 
 */
package ss.client.debug;

import ss.common.debug.DebugUtils;

/**
 *
 */
public class ClientThreadsDumpDebugCommand extends AbstractDebugCommand {

	/**
	 */
	protected ClientThreadsDumpDebugCommand() {
		super("client-threads-dump", "Print all client threads stact trace");
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		getCommandOutput().append( DebugUtils.dumpAllThreads( "Client threads") );
	}

}
