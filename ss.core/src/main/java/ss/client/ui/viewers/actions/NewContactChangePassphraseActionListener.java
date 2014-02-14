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
public class NewContactChangePassphraseActionListener implements Listener {
    private NewContact newContact;

    /**
     * @param newContact
     */
    public NewContactChangePassphraseActionListener(NewContact newContact) {
        super();
        this.newContact = newContact;
    }

    public void handleEvent(Event event) {

        this.newContact.getClient().addChangePassphraseNextLogin(
                this.newContact.getSession(), this.newContact.getOrigDoc()
                        .getRootElement().element("login").attributeValue(
                                "value"));
        NewContact.getSShell().dispose();

    }
}