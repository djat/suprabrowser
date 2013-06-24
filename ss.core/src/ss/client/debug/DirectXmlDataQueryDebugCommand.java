package ss.client.debug;

import ss.common.XmlDocumentUtils;

public class DirectXmlDataQueryDebugCommand extends AbstractDebugCommand {
	

	/**
	 * @param mainCommandName
	 */
	public DirectXmlDataQueryDebugCommand() {
		super( "dxdq", "Execute direct xml query to database" );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		//this.getMessagesPageOwner().selectXmlData( "" );
		String text = XmlDocumentUtils.toPrettyString(this.getMessagesPageOwner().getLastSelectedDoc());
		super.getCommandOutput().appendln( text  );
	}

}
