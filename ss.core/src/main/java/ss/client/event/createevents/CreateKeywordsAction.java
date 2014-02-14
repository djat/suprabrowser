/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class CreateKeywordsAction extends CreateAbstractAction {

    private static Image image;

    public static final String KEYWORD_TITLE = "Keywords";

    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateKeywordsAction.class);
    

    /**
     * 
     */
    public CreateKeywordsAction() {
        super();
        try {
            image = new Image(Display.getDefault(),getClass().getResource(
            ImagesPaths.KEYWORDS).openStream());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void performImpl() {
        logger.info("selected type " + KEYWORD_TITLE);
        super.performImpl();
    }

    public String getName() {

        return KEYWORD_TITLE;
    }

    public Image getImage() {

        return image;
    }

}
