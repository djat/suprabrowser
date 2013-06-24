/**
 * 
 */
package ss.client.ui.viewers.actions;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.viewers.NewContact;
import ss.common.SphereDefinitionCreator;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class NewContactOpenSphereActionListener implements Listener {
    private NewContact newContact;
    private Logger logger = SSLogger.getLogger(this.getClass());

    /**
     * @param newContact
     */
    public NewContactOpenSphereActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    @SuppressWarnings("unchecked")
	public void handleEvent(Event event) {

        String cname = null;

        this.logger
                .info("origdoc in invite: "
                        + this.newContact.getOrigDoc().asXML());
        if (this.newContact.getOrigDoc().getRootElement().element("last_name")
                .attributeValue("value").length() > 0) {
            cname = this.newContact.getOrigDoc().getRootElement().element(
                    "first_name").attributeValue("value")
                    + " "
                    + this.newContact.getOrigDoc().getRootElement().element(
                            "last_name").attributeValue("value");
        } else {

            cname = this.newContact.getOrigDoc().getRootElement().element(
                    "first_name").attributeValue("value");

        }
        String systemName = this.newContact.getClient().getVerifyAuth()
                .getSystemName(cname);

        this.logger.info("will open this sphere id for this contact: "
                + systemName + " : " + cname);
        Hashtable newSession = (Hashtable) this.newContact.getSession().clone();
        newSession.put("sphere_id", systemName);
        Document sphereDefinition = null;

        this.logger.info("it was null");
        SphereDefinitionCreator sdc = new SphereDefinitionCreator();
        sphereDefinition = sdc.createDefinition(cname,
                (String) this.newContact.getSession().get("sphere_id"));
        newSession.put("sphere_definition", sphereDefinition);
        this.logger.info("RESULTING SPHERE DEF: " + sphereDefinition.asXML());

        this.newContact.getClient().searchSphere(newSession,
                sphereDefinition, "false");
        NewContact.getSShell().dispose();

    }
}