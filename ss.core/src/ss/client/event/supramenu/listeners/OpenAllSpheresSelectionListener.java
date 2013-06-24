package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.common.UiUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.framework.threads.LinearExecutors;

public class OpenAllSpheresSelectionListener implements SelectionListener {

	/**
	 * 
	 */
	private static final String OPEN_CLOSE_DEAMON_NAME = "OpenCloseDeamon";

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		if ( !LinearExecutors.INSTANCE.hasLine(OPEN_CLOSE_DEAMON_NAME)) {
			LinearExecutors.beginExecute( OPEN_CLOSE_DEAMON_NAME, new OpenCloseDeamon() );			
		}
	}

}

class OpenCloseDeamon implements Runnable {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(OpenCloseDeamon.class);
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		for( int n = 0; n < 10; ++ n ) {
			openAll();
			try {
				Thread.sleep( 7 * 1000 );
			} catch (InterruptedException ex) {
				return;
			}
			getWebBrowserForAll();
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException ex) {
				return;
			}
			closeAll();
		}
		logger.debug( "Done" );
	}

	/**
	 * 
	 */
	private void getWebBrowserForAll() {
		UiUtils.swtInvoke( new Runnable() {
			public void run() {
				for( SupraCTabItem item : SupraSphereFrame.INSTANCE.tabbedPane.getSupraItems() ) {
					final SupraBrowser browser = item.getMBrowser();
					if ( browser != null ) { 
						browser.testWebBrowser();
					}
				}
			}
		} );
	}

	/**
	 * 
	 */
	private void closeAll() {
		UiUtils.swtInvoke( new Runnable() {
			public void run() {
				SupraSphereFrame.INSTANCE.closeAllTabs();
			}
		} );
	}

	/**
	 * 
	 */
	private void openAll() {
		final VerifyAuth verifyAuth = SupraSphereFrame.INSTANCE.client.getVerifyAuth();
		SupraSphereMember member = verifyAuth.getSupraSphere().getSupraMemberByLoginName( verifyAuth.getUserSession().getUserLogin() );
		for( SphereItem sphereItem : member.getSpheres().getEnabledSpheres() ) {
			SphereOpenManager.INSTANCE.request(sphereItem.getSystemName());
		}
		
	}
	
	
}