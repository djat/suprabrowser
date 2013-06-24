/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.EmailController;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateEmailAction extends CreateAbstractAction {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateEmailAction.class);

    public static final String EMAIL_TITLE = "Email";

    private static Image image;

    /**
     * 
     */
    public CreateEmailAction(Hashtable session) {
        super();
        try {
           image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.EMAIL_OUT_ICON).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        MessagesPane mp = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedMessagesPane();
        if (mp == null) {
			logger.warn("Selected message pane is null");
			return;
		}
        new EmailController(mp, mp.getRawSession()).createEmail(SupraSphereFrame.INSTANCE.getSendText());
        super.performImpl();
    }

    @Override
	public String getName() {
        return EMAIL_TITLE;
    }

    @Override
	public Image getImage() {
        return image;
    }
}
