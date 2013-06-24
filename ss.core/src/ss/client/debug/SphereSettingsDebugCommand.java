/**
 * 
 */
package ss.client.debug;

import ss.common.domainmodel2.SsDomain;

/**
 *
 */
public class SphereSettingsDebugCommand extends AbstractDebugCommand {

	private String sphereId;
	/**
	 * @param displayName
	 */
	protected SphereSettingsDebugCommand() {
		super("sphere-settings", "Gets sphere settings");
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		getCommandOutput().appendln( "" + SsDomain.SPHERE_HELPER.getSphereBySystemName( this.sphereId ).getSystemName() );
		
		
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#processCommandLine(ss.client.debug.ParsedDebugCommandLine)
	 */
	@Override
	protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
		super.processCommandLine(parsedDebugCommandLine);
		this.sphereId = parsedDebugCommandLine.getArg0();
	}

	
}
