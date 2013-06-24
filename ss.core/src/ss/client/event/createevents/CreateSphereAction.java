/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;

import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewSphere;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 *
 */
public class CreateSphereAction extends CreateAbstractAction {

    public static final String SPHERE_TITLE = "Sphere";

    private static Image image;
    
    private final static Logger logger = SSLogger.getLogger(CreateSphereAction.class);
    
    private Hashtable session = null;

    /**
     * 
     */
    public CreateSphereAction(final Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(SDisplay.display.get(),getClass().getResource(
            ImagesPaths.SPHERE).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);

        logger.info("selected type " + selectedType);
        final ISphereView selectedSphereView = SupraSphereFrame.INSTANCE.tabbedPane
				.getSelectedSphereView();
		if (selectedSphereView == null) {
			logger.warn("Selected sphere view is null");
			return;
		} 
        final List<String> selectedMembersNames = selectedSphereView.getSelectedMembersNames();

        if (SupraSphereFrame.INSTANCE.isReplyChecked()) {
        	final MessagesPane selected = SupraSphereFrame.INSTANCE.tabbedPane
					.getSelectedMessagesPane();
			if (selected == null) {
				logger.warn("Selected message pane is null");
				return;
			}
			Document tempSelectedDoc = null;
			if (selected.getLastSelectedDoc() != null) {
				tempSelectedDoc = (Document) selected.getLastSelectedDoc()
						.clone();
			}

			final Document lastSelectedDoc = tempSelectedDoc;
            if (selectedMembersNames.size() > 0) {
                new NewSphere(this.session, selectedSphereView, lastSelectedDoc
                        .getRootElement().element("message_id")
                        .attributeValue("value"), lastSelectedDoc
                        .getRootElement().element("thread_id")
                        .attributeValue("value"), selectedMembersNames, SupraSphereFrame.INSTANCE
                        .getSendText());
            } else {
                new NewSphere(this.session, selectedSphereView, lastSelectedDoc
                        .getRootElement().element("message_id")
                        .attributeValue("value"), lastSelectedDoc
                        .getRootElement().element("thread_id")
                        .attributeValue("value"), null, SupraSphereFrame.INSTANCE.getSendText());
            }

        } else {
            if (selectedMembersNames.size() > 0) {

                new NewSphere(this.session, selectedSphereView, null, null, selectedMembersNames, SupraSphereFrame.INSTANCE
                        .getSendText());
        } else {
                new NewSphere(this.session, selectedSphereView, null, null, null, SupraSphereFrame.INSTANCE
                        .getSendText());
            }
        }
        
        super.performImpl();
    }

    public String getName() {
        return SPHERE_TITLE;
    }

    public Image getImage() {
        return image;
    }

}
