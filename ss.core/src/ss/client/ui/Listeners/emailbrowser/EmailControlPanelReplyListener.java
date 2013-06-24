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
import ss.domainmodel.ExternalEmailStatement;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class EmailControlPanelReplyListener implements SelectionListener{

    private static final Logger logger = 
        SSLogger.getLogger(EmailControlPanelReplyListener.class);
    
    private MessagesPane mb = null;

    private ExternalEmailStatement email;
    
    private Hashtable session;
    
    public EmailControlPanelReplyListener(MessagesPane mb, ExternalEmailStatement email, Hashtable session) {
        super();
        this.mb = mb;
        this.email = email;
        this.session = session;
    }

    public void widgetSelected(SelectionEvent arg0) {
        logger.info("Clicked item Reply Email");
        (new EmailController(this.mb, this.session)).clickedReplyEmail(this.email);
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

}