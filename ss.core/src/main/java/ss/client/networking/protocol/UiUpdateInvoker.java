package ss.client.networking.protocol;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SDisplay;
import ss.common.ArgumentNullPointerException;

public final class UiUpdateInvoker {

	@SuppressWarnings("unused")
	private final DialogsMainCli cli;

	/**
	 * @param cli
	 */
	public UiUpdateInvoker(final DialogsMainCli cli ) {
		super();
		this.cli = cli;
	}
	
	public void swtBeginInvoke( final Runnable uiTask ) {
		if ( uiTask == null ) {
			throw new ArgumentNullPointerException( "uiTask" );
		}		
		doTask( uiTask );
		
	}

	/**
	 * @param uiTask
	 */
	private void doTask(Runnable uiTask) {
		SDisplay.display.async( uiTask );
	}
	
}
