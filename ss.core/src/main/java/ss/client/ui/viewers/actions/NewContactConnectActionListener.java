/**
 * 
 */
package ss.client.ui.viewers.actions;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewContact;
import ss.global.SSLogger;
import ss.util.NameTranslation;

/**
 * @author zobo
 *
 */
public class NewContactConnectActionListener implements Listener {
    private NewContact newContact;
    private Logger logger = SSLogger.getLogger(this.getClass());
    
    /**
     * @param newContact
     */
    public NewContactConnectActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    public void handleEvent(Event event) {
        String sphereURL = this.newContact.getOrigDoc().getRootElement().element("home_sphere")
                .attributeValue("value");

        this.logger.info("Sphere URL : " + sphereURL);
        try {
            String reciprocalLogin = this.newContact.getOrigDoc().getRootElement().element(
                    "reciprocal_login").attributeValue("value");
            String cname = NameTranslation
                    .createContactNameFromContactDoc(this.newContact.getOrigDoc());
            String systemName = this.newContact.getClient().getVerifyAuth().getSystemName(
                    cname);

            SupraSphereFrame.INSTANCE.startConnection(null, this.newContact.getSession(), sphereURL,
                    reciprocalLogin, systemName);

        } catch (NullPointerException npe) {

        }

        NewContact.getSShell().dispose();
    }
}