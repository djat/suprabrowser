/**
 * 
 */
package ss.client.ui.Listeners.emailbrowser;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.EmailControlPanel;
import ss.client.ui.tempComponents.ExternalEmailPane;

/**
 * @author roman
 *
 */
public class EmailControlPanelDiscussListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailControlPanelDiscussListener.class);
	
	private ExternalEmailPane ep;
	private SupraBrowser mb;
	private EmailControlPanel cp;
	
	public EmailControlPanelDiscussListener(EmailControlPanel cp) {
		this.cp = cp;
		this.ep = this.cp.getDocking().getEP();
		this.mb = this.ep.getBrowserDocking().getBrowser();
	}
	public void widgetSelected(SelectionEvent se) {
        if(this.mb==null || this.mb.isDisposed()) {
        	logger.error("browser is null or default");
        }
        if(((Button)se.widget).getSelection()) {
        	this.mb.highlightAllCommentedPlaces(this.ep.getEmail().getMessageId());
        } else {
        	this.mb.unhighlightCommentedPlaces();
        }
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}
