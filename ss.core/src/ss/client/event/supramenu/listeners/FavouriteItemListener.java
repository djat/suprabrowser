/**
 * 
 */
package ss.client.event.supramenu.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.sphereopen.SphereOpenManager;

/**
 * @author roman
 * 
 */
public class FavouriteItemListener implements SelectionListener {

	private SupraSphereFrame sF;

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FavouriteItemListener.class);

	public FavouriteItemListener(SupraSphereFrame sF) {
		this.sF = sF;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	public void widgetSelected(SelectionEvent se) {
		logger
				.info("------ item to show : "
						+ ((MenuItem) se.widget).getText());
		MenuItem name = (MenuItem) se.widget;
		String systemName = this.sF.getMenuBar().getItemId(name);

		SphereOpenManager.INSTANCE.request(systemName);
		//MenuSphereLoader.openSphere(systemName, this.sF);
	}
}
