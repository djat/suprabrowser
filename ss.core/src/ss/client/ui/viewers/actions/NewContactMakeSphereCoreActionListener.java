/**
 * 
 */
package ss.client.ui.viewers.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.viewers.NewContact;

/**
 * @author zobo
 *
 */
public class NewContactMakeSphereCoreActionListener implements Listener {
    private NewContact newContact;

    /**
     * @param newContact
     */
    public NewContactMakeSphereCoreActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    public void handleEvent(Event event) {

        this.newContact.getClient().makeCurrentSphereCore(
                this.newContact.getSession(), this.newContact.getLogin());

        NewContact.getSShell().dispose();

    }
}