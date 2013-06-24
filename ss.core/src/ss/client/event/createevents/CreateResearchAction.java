/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.SupraSphereFrame;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateResearchAction extends CreateAbstractAction {

    private static Image image;

    public static final String RESEARCH_TITLE = "Research";

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateResearchAction.class);
    
    private Hashtable session = null;
    
    public CreateResearchAction(Hashtable session) {
        super();
        this.session = session;
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.RESEARCH).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        String selectedType = SupraSphereFrame.INSTANCE.getMessageType(this.session);
        logger.info("selected type " + selectedType);
        // TODO: Implement functionality
        super.performImpl();
    }

    public String getName() {
        return RESEARCH_TITLE;
    }

    public Image getImage() {
        return image;
    }

}
