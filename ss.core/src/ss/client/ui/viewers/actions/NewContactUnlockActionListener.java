/**
 * 
 */
package ss.client.ui.viewers.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewContact;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class NewContactUnlockActionListener implements Listener {

private final NewContact newContactWindow;
	
	public NewContactUnlockActionListener(final NewContact newContactWindow) {
		this.newContactWindow = newContactWindow;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		ContactStatement contact = this.newContactWindow.getOrigContact();
		if(contact==null || StringUtils.isBlank(contact.getLogin())) {
			return;
		}
		SupraSphereFrame.INSTANCE.client.unlockContact(contact.getLogin());
		NewContact.getSShell().close();
	}
}
