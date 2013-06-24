/**
 * 
 */
package ss.client.ui.Listeners.browser;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.browser.SupraBrowser;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class BrowserControlPanelForwardListener implements SelectionListener{

    private Logger logger = SSLogger.getLogger(this.getClass());
    
    private SupraBrowser mb = null;

    public BrowserControlPanelForwardListener(SupraBrowser mb) {
        super();
        this.mb = mb;
    }

    public void widgetSelected(SelectionEvent arg0) {
        this.logger.info("Clicked item Forward");
        this.mb.forward();
        // TODO implement
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}