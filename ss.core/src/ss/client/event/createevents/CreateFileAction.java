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
import ss.client.ui.viewers.NewBinarySWT;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateFileAction extends CreateAbstractAction {

    private Image image;

    public static final String FILE_TITLE = "File";

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateFileAction.class);
    
    private Hashtable session = null;

    /**
     * 
     */
    public CreateFileAction(Hashtable session) {
        super();
        this.session = session;
        try {
            this.image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.FILE).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        final String sendText = SupraSphereFrame.INSTANCE.getSendText();
        final MessagesPane mp = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
        	.getSelectedMessagesPane();
        if ( mp == null ) {
        	logger.warn( "Selected message pane is null");
        	return;
        }
        final Hashtable send_session = (Hashtable) this.session.clone();
        final Document lastSelectedDoc = (mp.getLastSelectedDoc() == null) ? null : ((Document) mp.getLastSelectedDoc().clone());

        String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);

        logger.info("selected type " + selectedType);
        
        if (SupraSphereFrame.INSTANCE.isReplyChecked()) {
        	String fileName = NewBinarySWT.createFileName();
        	
        	if(fileName == null || fileName.equals("/")) {
				logger.warn("no file choosed");
				return;
			}
        	
            NewBinarySWT binary = new NewBinarySWT(send_session, mp,
            		fileName, lastSelectedDoc);

            binary.setSubject( sendText );
            binary.addButtons();
            binary.addKeyListener(mp.getInputListener());
            if (sendText.length()>0) {
              binary.giveBodyFocus();
            }
        } else {
        	String fileName = NewBinarySWT.createFileName();
        	
        	if(fileName == null || fileName.equals("/")) {
				logger.warn("no file choosed");
				return;
			}
        	
            NewBinarySWT binary = new NewBinarySWT(send_session, mp, fileName, false);
            binary.setSubject( sendText );
            binary.addButtons();
            binary.addKeyListener(mp.getInputListener());
            if (sendText.length()>0) {
              binary.giveBodyFocus();
            }

        }    
        super.performImpl();
    }

    public String getName() {
        return FILE_TITLE;
    }

    public Image getImage() {
        return this.image;
    }

}
