/**
 * 
 */
package ss.client.ui.root.actions;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.event.createevents.CreateContactAction;
import ss.client.ui.ISphereView;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.root.RootTab;
import ss.client.ui.tempComponents.DropDownItemAbstractAction;
import ss.client.ui.viewers.NewContact;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

/**
 * @author zobo
 *
 */
public class RootCreateUserAction implements DropDownItemAbstractAction {

	@SuppressWarnings("unused")
	private static Logger logger = ss.global.SSLogger
			.getLogger(CreateContactAction.class);

    public static final String CONTACT_TITLE = "Contact";

    private Image image;

    private final RootTab rootTab;

    /**
     * 
     */
    public RootCreateUserAction(final RootTab rootTab) {
        super();
        this.rootTab = rootTab;

        try {
            this.image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.CONTACT).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public void perform() {
    	// TODO: remake!
        
		final Hashtable session = (Hashtable) SupraSphereFrame.INSTANCE.client.session
		.clone();

		String sphereId = this.rootTab.getSelectedSphere();
		if (sphereId == null) {
			UserMessageDialogCreator.warning("Please select sphere to be login sphere for member");
			logger.info("Selected sphere is null, returning");
			return;
		}
		session.put(SessionConstants.SPHERE_ID2, sphereId);

		final ISphereView sphereView = new RootSphereView(session,
				sphereId);
        
        Thread t = new Thread() {
            public void run() {
                final NewContact newUI = new NewContact( session, sphereView );
                newUI.createNewToolBar();
                newUI.runEventLoop();
            }
        };
        t.start();
    }

    public String getName() {
        return CONTACT_TITLE;
    }

    public Image getImage() {
        return this.image;
    }
}
