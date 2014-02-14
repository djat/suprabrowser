/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SimpleBrowserDataSource;

/**
 * @author zobo
 *
 */
public class AboutMenuListener implements SelectionListener{
	
	private static final String TITLE = "SupraSphere";

	private static String Url = "http://www.suprasphere.com/";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AboutMenuListener.class);

	private SupraSphereFrame sF;

	public AboutMenuListener(SupraSphereFrame sF) {
		super();
		this.sF = sF;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		
	}

	public void widgetSelected(SelectionEvent e) {
		this.sF.addSimpleMozillaTab(this.sF.client.session, TITLE, new SimpleBrowserDataSource(Url), true, Url, false);
	}

}
