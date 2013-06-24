package ss.client.debug;

import org.dom4j.Document;

public class SphereDefinitionDebugCommand extends AbstractDebugCommand {
	
	private String sphereId;
	
	public SphereDefinitionDebugCommand () {
		super("sphere-definition", "Show sphere document");
	}
	
	

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#processCommandLine(ss.client.debug.ParsedDebugCommandLine)
	 */
	@Override
	protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
		super.processCommandLine(parsedDebugCommandLine);
		this.sphereId = parsedDebugCommandLine.getArg0();
	}



	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		Document sphereDocument;
		if( this.sphereId == null ) {
			sphereDocument = this.getMessagesPageOwner().getSphereDefinition();
		}
		else {
			sphereDocument = super.getMessagesPageOwner().getConcreteSphereDefinition( this.sphereId );
		}
		
		if ( sphereDocument == null ) {
			super.getCommandOutput().append( "Document for " )
				.append( this.sphereId != null ? "sphrere " + this.sphereId : "current sphere" ) 
				.appendln( " is null." );
			return;
		}
		super.getCommandOutput().appendln( sphereDocument );		
	}

}
