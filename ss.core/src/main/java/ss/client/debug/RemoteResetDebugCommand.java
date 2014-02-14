package ss.client.debug;


public class RemoteResetDebugCommand extends AbstractRemoteDebugCommand {

	/**
	 */
	protected RemoteResetDebugCommand() {
		super( "remote-reset", "Run remote command." );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		//ResetRemoteCommand command = new ResetRemoteCommand();
		//super.beginExecute( command );		
	}

}
