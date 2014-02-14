package ss.client.debug;


public class SelectedNodeDocumentDebugCommand extends AbstractDebugCommand {

	/**
	 * @param mainCommandName
	 */
	public SelectedNodeDocumentDebugCommand() {
		super( "selected-node", "Show selectd node xml document" );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		super.getCommandOutput().appendln( this.getMessagesPageOwner().getLastSelectedDoc()  );
	}
}
