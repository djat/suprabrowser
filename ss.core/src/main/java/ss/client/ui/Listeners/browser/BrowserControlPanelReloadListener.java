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
public class BrowserControlPanelReloadListener implements SelectionListener{

    private Logger logger = SSLogger.getLogger(this.getClass());
    
    private SupraBrowser mb = null;

    public BrowserControlPanelReloadListener(SupraBrowser mb) {
        super();
        this.mb = mb;
    }

    public void widgetSelected(SelectionEvent arg0) {
        this.logger.info("Clicked item Reload");
        // TODO implement
        this.mb.refresh();
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}