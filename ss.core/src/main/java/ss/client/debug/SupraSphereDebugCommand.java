package ss.client.debug;

import ss.common.VerifyAuth;

public class SupraSphereDebugCommand  extends AbstractDebugCommand {
	
	public SupraSphereDebugCommand () {
		super("suprasphere", "Show supra sphere xml document");
	}
	
	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	@ss.refactor.Refactoring(classify=ss.refactor.supraspheredoc.SupraSphereRefactor.class)
	protected void performExecute() throws DebugCommanRunntimeException {
		final VerifyAuth verifyAuth = this.getMessagesPageOwner().client.getVerifyAuth();
		super.getCommandOutput().appendln( verifyAuth.getSupraSphereInformationForDump() );		
	}

}
