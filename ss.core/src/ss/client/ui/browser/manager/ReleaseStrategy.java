/**
 * 
 */
package ss.client.ui.browser.manager;

/**
 *
 */
public enum ReleaseStrategy {

	AUTO( false, false ),
	REFERENCE_HOLD( false, true ),
	MANUAL_FINALIZE_AND_REFERENCE_HOLD( true, true );
	
	private boolean manualFinalization;
	
	private boolean holdDisposedBrowser;

	/**
	 * @param manualFinalization
	 * @param holdToDisposedBrowser
	 */
	private ReleaseStrategy(boolean manualFinalization, boolean holdToDisposedBrowser) {
		this.manualFinalization = manualFinalization;
		this.holdDisposedBrowser = holdToDisposedBrowser;
		if ( this.manualFinalization && ! this.holdDisposedBrowser ) {
			throw new IllegalArgumentException( "Unsupported flags combination" );
		}
	}

	/**
	 * @return the holdToDisposedBrowser
	 */
	public boolean isHoldDisposedBrowser() {
		return this.holdDisposedBrowser;
	}

	/**
	 * @return the manualFinalization
	 */
	public boolean isManualFinalization() {
		return this.manualFinalization;
	}
	
	
	
	
}
