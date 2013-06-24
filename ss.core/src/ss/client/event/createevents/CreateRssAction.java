/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.MessagesPane;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewWeblink;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateRssAction extends CreateAbstractAction {

    private static Image image;

    public static final String RSS_TITLE = "Rss";

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateRssAction.class);
    
    private final Hashtable session;
    /**
     * 
     */
    public CreateRssAction(Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(SDisplay.display.get(),getClass().getResource(
            ImagesPaths.RSS).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        final String sendText = SupraSphereFrame.INSTANCE.getSendText();
        final AbstractDelivery delivery = SupraSphereFrame.INSTANCE.getDefaultDelivery(this.session);

        final Hashtable send_session = (Hashtable) this.session.clone();
        final MessagesPane mp = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
        	.getSelectedMessagesPane();
        if (mp == null) {
        	logger.warn("Selected message pane is null");
        	return;
        }

        //Document doc = null;
        // Document orig_doc = null;
        final Document lastSelectedDoc = (mp.getLastSelectedDoc() == null) ? null : ((Document) mp.getLastSelectedDoc().clone());

        final String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);

        logger.info("selected type " + selectedType);
        
        boolean isReply = SupraSphereFrame.INSTANCE.isReplyChecked(); 
        
        NewWeblink weblinkWindow = new NewWeblink(send_session, mp,
                    lastSelectedDoc, delivery, isReply, sendText, null);
        weblinkWindow.setIsRssCreation(true);
        super.performImpl();
    }

    @Override
	public String getName() {
        return RSS_TITLE;
    }

    @Override
	public Image getImage() {
        return image;
    }

}
