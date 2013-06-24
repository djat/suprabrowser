/**
 * 
 */
package ss.client.debug;

import ss.common.debug.DumpSupraSphereCommand;

/**
 *
 */
public class DumpSupraSphereDebugCommand extends AbstractRemoteDebugCommand {

	/**
	 */
	public DumpSupraSphereDebugCommand() {
		super( "dump-supra-sphere", "Shows server suprasphere");
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		super.beginExecute( new DumpSupraSphereCommand() );
	}

}
