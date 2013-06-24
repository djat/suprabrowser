/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewMessage;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateMessageAction extends CreateAbstractAction {

    private static Image image;

    public static final String MESSAGE_TITLE = "Message";
    
    private static String subject  = null;
    
    private Statement viewStatement = null;

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateMessageAction.class);
    
    private Hashtable session = null;
    /**
     * 
     */
    public CreateMessageAction(Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.MESSAGE).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public CreateMessageAction(final Hashtable session, final Statement viewStatement) {
        super();
        this.session = session;
        subject = "RE: "+viewStatement.getSubject();
        this.viewStatement = viewStatement;
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.MESSAGE).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    

    @Override
    public void performImpl() {
        
    	String subjectText = null;
    	if (subject!=null) {
    		subjectText = subject;
    	}
    	else {
    		subjectText = SupraSphereFrame.INSTANCE.getSendText();
    	}
        final String sendText = subjectText;
        final AbstractDelivery delivery = SupraSphereFrame.INSTANCE.getDefaultDelivery(this.session);

        final Hashtable send_session = (Hashtable) this.session.clone();
        final MessagesPane mp = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
        		.getSelectedMessagesPane();
        if (mp == null) {
			logger.warn("Selected message pane is null");
			return;
		}
        final Document lastSelectedDoc = (mp.getLastSelectedDoc() == null) ? null : ((Document) mp.getLastSelectedDoc().clone());
        String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);

        logger.info("selected type " + selectedType);
        
        NewMessage nm;
        if (SupraSphereFrame.INSTANCE.isReplyChecked()) {
			nm = new NewMessage(send_session, mp, Statement.wrap(lastSelectedDoc), delivery);
        } else {
            logger.info("MAKE IT A MESSAGE: " + delivery);
            nm = new NewMessage(send_session, mp, null, delivery);
        }
        nm.addKeyListener(mp.getInputListener());
        if (logger.isDebugEnabled()) {
			logger.debug("new message : "+nm);
		}
        super.performImpl();
    }

    public String getName() {
        return MESSAGE_TITLE;
    }

    public Image getImage() {
        return image;
    }

}