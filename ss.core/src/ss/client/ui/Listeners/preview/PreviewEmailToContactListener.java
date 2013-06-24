/**
 * 
 */
package ss.client.ui.Listeners.preview;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ss.client.ui.MessagesPane;
import ss.client.ui.email.EmailController;
import ss.domainmodel.ContactStatement;

/**
 * @author roman
 *
 */
public class PreviewEmailToContactListener extends SelectionAdapter {

	private MessagesPane mp;
	
	public PreviewEmailToContactListener(MessagesPane mp) {
		this.mp = mp;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		ContactStatement contact = ContactStatement.wrap(this.mp.getSelectedStatement().getBindedDocument());
		(new EmailController(this.mp, this.mp.client.session)).clickedComposeEmail(contact.getEmailAddress());
	}
	
	
}
