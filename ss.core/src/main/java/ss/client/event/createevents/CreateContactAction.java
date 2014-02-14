/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewContact;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateContactAction extends CreateAbstractAction {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateContactAction.class);

    public static final String CONTACT_TITLE = "Contact";

    private static Image image;
    
    private Hashtable session = null;

    /**
     * 
     */
    public CreateContactAction(Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.CONTACT).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        final String sendText = SupraSphereFrame.INSTANCE.getSendText();

        final Hashtable send_session = (Hashtable) this.session.clone();

        final String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);

        logger.debug("selected type " + selectedType);
        final ISphereView sphereView = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
                .getSelectedSphereView();
        
        Thread t = new Thread() {
            public void run() {

                final NewContact newUI = new NewContact(send_session, sphereView);
                newUI.setTextArea(sendText);
                newUI.createNewToolBar();
                newUI.runEventLoop();
            }
        };
        t.start();
        
        super.performImpl();
    }

    public String getName() {
        return CONTACT_TITLE;
    }

    public Image getImage() {
        return image;
    }

}
