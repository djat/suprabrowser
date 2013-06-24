package ss.client.debug;

import java.util.Hashtable;

public class SessionDebugCommand extends AbstractDebugCommand {


	public SessionDebugCommand() {
		super("session", "Show session"); 
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		Hashtable hastable = super.getMessagesPageOwner().getRawSession();
		for( Object key : hastable.keySet() ) {
			super.getCommandOutput().append( key )
			.append( " = " )
			.append( hastable.get( key ) )
			.appendln();
		}
	}


}
