/**
 * 
 */
package ss.client.ui.email;

import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.ExternalEmailStatement;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class ComposeEmailShell extends EmailCommonShell {


    private static final String COMPOSE_EMAIL = "Compose Email";

    public ComposeEmailShell(SupraSphereFrame sF, ExternalEmailStatement originalEmail, EmailController controller) {
        super(sF, originalEmail, controller);
    }
    
    public ComposeEmailShell(SupraSphereFrame sF, EmailController controller) {
        this(sF, null, controller);
    }

    @Override
    protected String getTitle() {
        return COMPOSE_EMAIL;
    }

    @Override
    protected String getImagePath() {
        return ImagesPaths.EMAIL_COMPOSE_ICON;
    }

    @Override
    protected boolean getIsReply() {
        return false;
    }
}
