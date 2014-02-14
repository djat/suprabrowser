/**
 * 
 */
package ss.client.debug;

import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.common.domainmodel2.Sphere;
import ss.common.domainmodel2.SphereHelper;

/**
 * @author d!ma
 * 
 */
public class DomainSpaceDebugCommand extends AbstractDebugCommand {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DomainSpaceDebugCommand.class);

	private String objectName;
	
	public DomainSpaceDebugCommand() {
		super("ds-settings", "Get settings for specified member, sphere or sphere memeber."  
				+ StringUtils.getLineSeparator()  
				+ "Syntax: ds-settings [sphere|member|invited_memeber] [system_name|login|system_name login]" );
	}  
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		this.getCurrentContext().getParsedDebugCommandLine();
		getCommandOutput().appendln("Receiving helper ");
		SphereHelper helper = SsDomain.SPHERE_HELPER;
		getCommandOutput().appendln( "Getting sphere with system name " + this.objectName ); 
		Sphere sphere = helper.getSphereBySystemName(this.objectName );
		getCommandOutput().appendln( sphere.allFieldsToString() );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#processCommandLine(ss.client.debug.ParsedDebugCommandLine)
	 */
	@Override
	protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
		super.processCommandLine(parsedDebugCommandLine);
		this.objectName = parsedDebugCommandLine.getArg0();		
	}
	
	
}
