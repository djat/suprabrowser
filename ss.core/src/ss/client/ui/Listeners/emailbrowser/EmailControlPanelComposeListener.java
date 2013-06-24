/**
 * 
 */
package ss.client.ui.Listeners.emailbrowser;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.MessagesPane;
import ss.client.ui.email.EmailController;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class EmailControlPanelComposeListener implements SelectionListener{

    private static final Logger logger = 
        SSLogger.getLogger(EmailControlPanelComposeListener.class);
    
    private MessagesPane mb = null;
    
    private Hashtable session;

    public EmailControlPanelComposeListener(MessagesPane mb, Hashtable session) {
        super();
        this.mb = mb;
        this.session = session;
    }

    public void widgetSelected(SelectionEvent arg0) {
        logger.info("Clicked item Compose Email");
        (new EmailController(this.mb, this.session)).clickedComposeEmail(null);
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}