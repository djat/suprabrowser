/**
 * 
 */
package ss.client.ui.Listeners.browser;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.BrowserControlPanel;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class BrowserControlPanelDiscussListener implements SelectionListener{

    private Logger logger = SSLogger.getLogger(this.getClass());
    
    private SupraBrowser mb = null;
    
    private BrowserControlPanel cp = null;

    public BrowserControlPanelDiscussListener(BrowserControlPanel cp) {
        super();
        this.mb = cp.getBrowser();
        this.cp = cp;
    }

    public void widgetSelected(SelectionEvent se) {
        this.logger.info("Clicked item Discuss");
        
        if(((Button)se.widget).getSelection()) {
        	this.mb.highlightAllCommentedPlaces(this.cp.getBrowserPane().getBookmark().getMessageId());
        } else {
        	this.mb.unhighlightCommentedPlaces();
        }
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}
